package example.symphony.demoworkflow.poll;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.finos.springbot.symphony.content.HashTag;
import org.finos.springbot.workflow.annotations.ChatButton;
import org.finos.springbot.workflow.annotations.ChatRequest;
import org.finos.springbot.workflow.annotations.ChatResponseBody;
import org.finos.springbot.workflow.annotations.WorkMode;
import org.finos.springbot.workflow.content.Chat;
import org.finos.springbot.workflow.content.User;
import org.finos.springbot.workflow.conversations.AllConversations;
import org.finos.springbot.workflow.form.Button;
import org.finos.springbot.workflow.form.Button.Type;
import org.finos.springbot.workflow.form.ButtonList;
import org.finos.springbot.workflow.form.ErrorMap;
import org.finos.springbot.workflow.history.AllHistory;
import org.finos.springbot.workflow.response.WorkResponse;
import org.finos.springbot.workflow.response.handlers.ResponseHandlers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

@Controller
public class PollController {
	
	@Autowired
	ResponseHandlers rh;
	
	@Autowired
	AllHistory h;
	
	@Autowired
	AllConversations rooms;
	
	@Autowired
	TaskScheduler taskScheduler;
	
	public boolean isMe(User u) {
		return rooms.isThisBot(u);
	}
	
	
	@ChatRequest(value="poll", description = "Start A Poll")
	@ChatResponseBody(workMode = WorkMode.EDIT) 
	public PollCreateForm pollForm() {
		return new PollCreateForm();
	}

	@ChatButton(buttonText ="start", showWhen = WorkMode.EDIT, value = PollCreateForm.class)
	public List<WorkResponse> poll(
			PollCreateForm cf, 
			Chat r, 
			User a) {
		int[] i = { 0 };
		
		List<String> options = Arrays.asList(cf.option1, cf.option2, cf.option3, cf.option4, cf.option5, cf.option6)	
				.stream()
				.filter(s -> StringUtils.hasText(s))
				.collect(Collectors.toList());
		
		ButtonList buttons = new ButtonList(options.stream()
		.map(s -> new Button(PollController.class,"poll"+(i[0]++), Type.ACTION, s))
			.collect(Collectors.toList()));
		
		HashTag id = new HashTag(UUID.randomUUID().toString());
		
		Poll p = new Poll(options);
		p.setPoller(a);
		p.setQuestion(cf.getQuestion());
		p.setOptions(options);
		p.setId(id);
		
		List<User> users = rooms.getChatMembers(r);
		List<WorkResponse> out = users.stream()
			.filter(u -> !isMe(u))
			.map(u -> createResponseForUser(cf, options, id, buttons, u))
			.collect(Collectors.toList());
		
		out.add(new WorkResponse(r, p, WorkMode.VIEW));
		
		doScheduling(p, cf, r);
		
		return out;
	}

	private void doScheduling(Poll p, PollCreateForm cf, Chat r) {
		if (cf.isEndAutomatically()) {
			Instant endTime = Instant.now().plus(cf.getTime(), cf.getTimeUnit());
			p.setEndTime(endTime);
			taskScheduler.schedule(() -> {
				Result result = end(p, r, h);
				WorkResponse out = new WorkResponse(r, result, WorkMode.VIEW);
				rh.accept(out);
				
			}, endTime);
		}
	}

	@ChatButton(value = Poll.class, showWhen = WorkMode.VIEW, buttonText = "End Poll Now")
	public Result end(Poll p, Chat r, AllHistory h) {
		List<Answer> responses = h.getFromHistory(Answer.class, p.getId(), null, null);

		List<Integer> counts = new ArrayList<>(p.getOptions().size());
		int totalResponses = 0;
		
		for (int i = 0; i < p.getOptions().size(); i++) {
			counts.add(0);
		}

		for (Answer pollResponse : responses) {
			counts.set(pollResponse.getChoice(), counts.get(pollResponse.getChoice()) + 1);
			totalResponses ++;
		}


		return new Result(p.getId(), counts, p.getOptions(), p.getQuestion(), p.getPoller(), totalResponses);
	}

	private static WorkResponse createResponseForUser(PollCreateForm cf, List<String> choices, HashTag id,
			ButtonList buttons, User u) {
		Question q = new Question(cf.getQuestion(), choices, id, u);
		return new WorkResponse(u, q, WorkMode.VIEW, buttons, new ErrorMap());
	}
	
	@ChatButton(buttonText = "poll0", value=Question.class)
	public Answer poll0(User u, Question q) {
		return chooseResponse(q, u, 0);
	}

	private Answer chooseResponse(Question q, User u, int r) {
		return new Answer(q.id, u, Instant.now(), r, q.question, q.options.get(r));
	}

	@ChatButton(buttonText = "poll1", value=Question.class)
	public Answer poll1(User u, Question q) {
		return chooseResponse(q, u, 1);
	}

	@ChatButton(buttonText = "poll2", value=Question.class)
	public Answer poll2(User u, Question q) {
		return chooseResponse(q, u, 2);
	}

	@ChatButton(buttonText = "poll3", value=Question.class)
	public Answer poll3(User u, Question q) {
		return chooseResponse(q, u, 3);
	}

	@ChatButton(buttonText = "poll4", value=Question.class)
	public Answer poll4(User u, Question q) {
		return chooseResponse(q, u, 4);
	}

	@ChatButton(buttonText = "poll5", value=Question.class)
	public Answer poll5(User u, Question q) {
		return chooseResponse(q, u, 5);
	}

}
