package example.symphony.demoworkflow.poll;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;

import com.github.deutschebank.symphony.workflow.Workflow;
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
import com.github.deutschebank.symphony.workflow.response.MessageResponse;
import com.github.deutschebank.symphony.workflow.response.Response;

import example.symphony.demoworkflow.WorkflowConfig.MemberQueryWorkflow;
import example.symphony.demoworkflow.poll.bot.CreatePoll;
import example.symphony.demoworkflow.poll.service.MarkupService;

@Work(name = "Poll", instructions = "Please participate in our poll", editable = false)
public class Poll {

	private ID id = new ID(UUID.randomUUID());
	private Instant startTime = Instant.now();
	private List<String> options;

	public Poll(List<String> options) {
		super();
		this.options = options;
	}

	public Poll() {
		super();
	}

	@Exposed
	public static List<FormResponse> poll(ChoiceForm cf, Room r, Workflow wf) {
		int[] i = { 0 };
		List<String> options = Arrays.asList(cf.option1, cf.option2, cf.option3, cf.option4, cf.option5, cf.option6)	
				.stream()
				.filter(s -> !StringUtils.isEmpty(s))
				.collect(Collectors.toList());
		
		List<Button> buttons = options.stream()
			.map(s -> new Button("poll"+(i[0]++), Type.ACTION, s))
			.collect(Collectors.toList());
		
		Poll p = new Poll(options);
		
		List<User> users = ((MemberQueryWorkflow)wf).getMembersInRoom(r);
		List<FormResponse> out = users.stream()
			.filter(u -> !((MemberQueryWorkflow)wf).isMe(u))
			.map(u -> createResponseForUser(cf, wf, options, p.getId(), new ButtonList(buttons), u))
			.collect(Collectors.toList());
		
		out.add(new FormResponse(wf, r, p, "Poll Created", "", p, false, 
				ButtonList.of(new Button("end", Type.ACTION, "End Poll"))));
		
		return out;
	}

	@Exposed(description = "Initialize Poll setup")
	public Response initpoll(User u, Room r, Workflow wf) {
		MarkupService markupService = new MarkupService();
		return new MessageResponse(wf, r, markupService.getPollCreateData(u, r, wf, new ID(UUID.randomUUID()).getId()), "Create Poll", "",
				markupService.getCreatePollTemplate());
	}
	
	@Exposed
	public List<Response> endpoll(User u, Room r, Workflow wf) {
		return CreatePoll.handleEndPoll(r, u, u.getName(), wf);
	}

	@Exposed
	public PollResult end(Room r, Workflow wf) {
		History h = wf.getHistoryApi();

		List<PollResponse> responses = new ArrayList<>();

		List<Object> results = h.getFromHistory(id, null, null);

		for (Object o : results) {
			if (o instanceof PollResponse) {
				responses.add((PollResponse) o);
			}
		}

		PollResult out = new PollResult();

		List<Integer> counts = new ArrayList<>(options.size());
		for (int i = 0; i < options.size(); i++) {
			counts.add(0);
		}

		for (PollResponse pollResponse : responses) {
			counts.set(pollResponse.getChoice(), counts.get(pollResponse.getChoice()) + 1);
			out.setTotalResponses(out.getTotalResponses() + 1);
		}

		out.setCounts(counts);

		return out;
	}

	private static FormResponse createResponseForUser(ChoiceForm cf, Workflow wf, List<String> choices, ID id,
			ButtonList buttons, User u) {
		Question q = new Question(cf.getQuestion(), choices, id);
		return new FormResponse(wf, u, q, cf.getQuestion(), "Pick one of: ", q, false, buttons);
	}

	public List<String> getOptions() {
		return options;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}

	public Instant getStartTime() {
		return startTime;
	}

	public void setStartTime(Instant startTime) {
		this.startTime = startTime;
	}

	public ID getId() {
		return id;
	}

	public void setId(ID id) {
		this.id = id;
	}

}
