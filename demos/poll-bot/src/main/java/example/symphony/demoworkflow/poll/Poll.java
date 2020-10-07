package example.symphony.demoworkflow.poll;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;

import com.github.deutschebank.symphony.workflow.Workflow;
import com.github.deutschebank.symphony.workflow.content.Author;
import com.github.deutschebank.symphony.workflow.content.ID;
import com.github.deutschebank.symphony.workflow.content.Room;
import com.github.deutschebank.symphony.workflow.content.User;
import com.github.deutschebank.symphony.workflow.form.Button;
import com.github.deutschebank.symphony.workflow.form.Button.Type;
import com.github.deutschebank.symphony.workflow.form.ButtonList;
import com.github.deutschebank.symphony.workflow.history.History;
import com.github.deutschebank.symphony.workflow.java.Exposed;
import com.github.deutschebank.symphony.workflow.java.Work;
import com.github.deutschebank.symphony.workflow.response.FormResponse;
import com.github.deutschebank.symphony.workflow.sources.symphony.Template;

import example.symphony.demoworkflow.WorkflowConfig.MemberQueryWorkflow;

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

	@Exposed(description = "Start A Poll")
	public static List<FormResponse> poll(PollCreateForm cf, Room r, Workflow wf) {
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
		p.setPoller(Author.CURRENT_AUTHOR.get());
		p.setQuestion(cf.getQuestion());
		p.setOptions(options);
		p.setId(id);
		
		List<User> users = ((MemberQueryWorkflow)wf).getMembersInRoom(r);
		List<FormResponse> out = users.stream()
			.filter(u -> !((MemberQueryWorkflow)wf).isMe(u))
			.map(u -> createResponseForUser(cf, wf, options, id, buttons, u))
			.collect(Collectors.toList());
		
		ButtonList bl = new ButtonList();
		bl.add(new Button("end", Type.ACTION, "End Poll Now"));
		
		out.add(new FormResponse(wf, r, p, "Poll Created : "+cf.getQuestion(), "", Poll.class, false, bl));
		
		return out;
	}

	@Exposed
	public Result end(Room r, Workflow wf) {
		History h = wf.getHistoryApi();

		List<Answer> responses = new ArrayList<>();

		List<Object> results = h.getFromHistory(id, null, null);

		for (Object o : results) {
			if (o instanceof Answer) {
				responses.add((Answer) o);
			}
		}

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
		return new FormResponse(wf, u, null, cf.getQuestion(), "Pick one of: ", q, false, buttons);
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
