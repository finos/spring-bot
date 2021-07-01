package example.symphony.demoworkflow.poll;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.annotations.Exposed;
import org.finos.symphony.toolkit.workflow.annotations.Work;
import org.finos.symphony.toolkit.workflow.content.Author;
import org.finos.symphony.toolkit.workflow.content.ID;
import org.finos.symphony.toolkit.workflow.content.Room;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.form.Button;
import org.finos.symphony.toolkit.workflow.form.ButtonList;
import org.finos.symphony.toolkit.workflow.form.Button.Type;
import org.finos.symphony.toolkit.workflow.history.History;
import org.finos.symphony.toolkit.workflow.response.FormResponse;
import org.finos.symphony.toolkit.workflow.room.Rooms;
import org.finos.symphony.toolkit.workflow.sources.symphony.Template;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.EntityJsonConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.ResponseHandler;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.util.StringUtils;

import com.symphony.api.id.SymphonyIdentity;

@Work(name = "Poll", instructions = "Please participate in our poll", editable = false)
@Template(view = "classpath:/template/poll.ftl")
public class Poll {

	private ID id;
	private Instant endTime;
	private List<String> options;
	private String question;
	private User poller;

	public Poll(List<String> options) {
		super();
		this.options = options;
	}

	public Poll() {
		super();
	}
	

	public static boolean isMe(User u, SymphonyIdentity botIdentity) {
		return u.getAddress().equals(botIdentity.getEmail());
	}

	@Exposed(description = "Start A Poll")
	public static List<FormResponse> poll(
			Workflow wf, 
			PollCreateForm cf, 
			Room r, 
			Author a, 
			TaskScheduler taskScheduler, 
			SymphonyIdentity botIdentity, 
			Rooms rooms,
			ResponseHandler rh,
			History h) {
		int[] i = { 0 };
		List<String> options = Arrays.asList(cf.option1, cf.option2, cf.option3, cf.option4, cf.option5, cf.option6)	
				.stream()
				.filter(s -> !StringUtils.isEmpty(s))
				.collect(Collectors.toList());
		
		ButtonList buttons = new ButtonList(options.stream()
			.map(s -> new Button("poll"+(i[0]++), Type.ACTION, s))
			.collect(Collectors.toList()));
		
		ID id = new ID(UUID.randomUUID());
		
		Poll p = new Poll(options);
		p.setPoller(a);
		p.setQuestion(cf.getQuestion());
		p.setOptions(options);
		p.setId(id);
		
		List<User> users = rooms.getRoomMembers(r);
		List<FormResponse> out = users.stream()
			.filter(u -> !isMe(u, botIdentity))
			.map(u -> createResponseForUser(cf, wf, options, id, buttons, u))
			.collect(Collectors.toList());
		
		ButtonList bl = new ButtonList();
		bl.add(new Button("end", Type.ACTION, "End Poll Now"));
		
		EntityJson json = EntityJsonConverter.newWorkflow(p);
		
		out.add(new FormResponse(wf, r, json, "Poll Created : "+cf.getQuestion(), "", Poll.class, false, bl));
		
		doScheduling(p, taskScheduler, cf, rh, h, r, wf);
		
		return out;
	}

	private static void doScheduling(Poll p, TaskScheduler taskScheduler, PollCreateForm cf, ResponseHandler rh, History h, Room r, Workflow wf) {
		if (cf.isEndAutomatically()) {
			Instant endTime = Instant.now().plus(cf.getTime(), cf.getTimeUnit());
			p.setEndTime(endTime);
			taskScheduler.schedule(() -> {
				Result result = p.end(r, h);
				EntityJson data = EntityJsonConverter.newWorkflow(result);
				FormResponse out = new FormResponse(wf, r, data, wf.getName(Result.class), wf.getInstructions(Result.class), Result.class, false, wf.gatherButtons(result, r));
				rh.accept(out);
				
			}, endTime);
		}
	}

	@Exposed
	public Result end(Room r, History h) {
		List<Answer> responses = h.getFromHistory(Answer.class, id, null, null);

		List<Integer> counts = new ArrayList<>(options.size());
		int totalResponses = 0;
		
		for (int i = 0; i < options.size(); i++) {
			counts.add(0);
		}

		for (Answer pollResponse : responses) {
			counts.set(pollResponse.getChoice(), counts.get(pollResponse.getChoice()) + 1);
			totalResponses ++;
		}


		return new Result(this.getId(), counts, this.getOptions(), this.getQuestion(), this.getPoller(), totalResponses);
	}

	private static FormResponse createResponseForUser(PollCreateForm cf, Workflow wf, List<String> choices, ID id,
			ButtonList buttons, User u) {
		Question q = new Question(cf.getQuestion(), choices, id, u);
		return new FormResponse(wf, u, new EntityJson(), cf.getQuestion(), "Pick one of: ", q, false, buttons);
	}

	public List<String> getOptions() {
		return options;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}

	public Instant getEndTime() {
		return endTime;
	}

	public void setEndTime(Instant t) {
		this.endTime = t;
	}

	public ID getId() {
		return id;
	}

	public void setId(ID id) {
		this.id = id;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public User getPoller() {
		return poller;
	}

	public void setPoller(User poller) {
		this.poller = poller;
	}

}
