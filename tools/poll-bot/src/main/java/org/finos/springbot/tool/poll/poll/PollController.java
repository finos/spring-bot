package org.finos.springbot.tool.poll.poll;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.finos.springbot.workflow.annotations.ChatButton;
import org.finos.springbot.workflow.annotations.ChatRequest;
import org.finos.springbot.workflow.annotations.ChatResponseBody;
import org.finos.springbot.workflow.annotations.ChatVariable;
import org.finos.springbot.workflow.annotations.WorkMode;
import org.finos.springbot.workflow.content.Chat;
import org.finos.springbot.workflow.content.Message;
import org.finos.springbot.workflow.content.Paragraph;
import org.finos.springbot.workflow.content.User;
import org.finos.springbot.workflow.content.Word;
import org.finos.springbot.workflow.conversations.AllConversations;
import org.finos.springbot.workflow.form.Button;
import org.finos.springbot.workflow.form.Button.Type;
import org.finos.springbot.workflow.form.ButtonList;
import org.finos.springbot.workflow.form.ErrorMap;
import org.finos.springbot.workflow.history.AllHistory;
import org.finos.springbot.workflow.response.DataResponse;
import org.finos.springbot.workflow.response.MessageResponse;
import org.finos.springbot.workflow.response.WorkResponse;
import org.finos.springbot.workflow.response.handlers.ResponseHandlers;
import org.finos.springbot.workflow.tags.HeaderDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

@Controller
public class PollController {
	
	private static final String ANSWER_SUFFIX = "-a";

	private static final String QUESTION_SUFFIX = "-q";

	@Autowired
	ResponseHandlers rh;
	
	@Autowired
	AllHistory h;
	
	@Autowired
	AllConversations rooms;
	
	@Autowired
	TaskScheduler taskScheduler;
	
	@ChatRequest(value="poll", description = "Start A Poll")
	@ChatResponseBody(workMode = WorkMode.EDIT) 
	public PollCreateForm pollForm(Chat r) {
		if (r == null) {
			throw new RuntimeException("You can't create a poll in a 1-1 chat");
		}
		return new PollCreateForm();
	}

	@ChatButton(buttonText ="start", showWhen = WorkMode.EDIT, value = PollCreateForm.class)
	public List<DataResponse> poll(
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
		
		String id = UUID.randomUUID().toString();
		id = "p"+id.substring(id.length()-6);
	
		Question p = new Question(cf.getQuestion(), options, id, a);	
		doScheduling(p, cf, r);
		
		MessageResponse out1 = createQuestionMessage(p, r, a);
		WorkResponse out2 = createQuestionResponse(p, buttons, r);
		return Arrays.asList(out1, out2);
	}
	
	private static MessageResponse createQuestionMessage(Question q, Chat r, User u) {
		String start = "can end the poll at any time by typing \"/end "+(q.id)+"\"";
		Paragraph p2 = Paragraph.of(u, Word.of(start));
				
		Message m = Message.of(p2);		
		return new MessageResponse(r, m);		
	}

	private static WorkResponse createQuestionResponse(Question q, ButtonList buttons, Chat u) {
		WorkResponse out = new WorkResponse(u, q, WorkMode.VIEW, buttons, new ErrorMap());
		HeaderDetails hd = new HeaderDetails(Arrays.asList(q.id+QUESTION_SUFFIX));
		out.getData().put(HeaderDetails.KEY, hd);
		return out;
	}
	

	private void doScheduling(Question p, PollCreateForm cf, Chat r) {
		if (cf.isEndAutomatically() && (cf.getTimeUnit() != null)) {
			Instant endTime = Instant.now().plus(cf.getTime(), cf.getTimeUnit().getChronoUnit());
			p.setEndTime(endTime);
			taskScheduler.schedule(() -> {
				Result result = end(Word.of(p.getId()), r, h);
				WorkResponse out = new WorkResponse(r, result, WorkMode.VIEW);
				rh.accept(out);
				
			}, endTime);
		}
	}

	@ChatRequest(value = "end {pollId}", description = "End a poll that you have started")
	public Result end(@ChatVariable("pollId") Word pollId, Chat r, AllHistory h) {
		
		Question q = h.getLastFromHistory(Question.class, pollId.getText()+QUESTION_SUFFIX, r)
				.orElseThrow(() -> new RuntimeException("Couldn't find poll with that tag"));
		
		List<Answer> responses = h.getFromHistory(Answer.class, pollId.getText()+ANSWER_SUFFIX, r, null);
		
		// make sure people only vote once
		Set<User> seen = new HashSet<User>();
 
		List<Integer> counts = new ArrayList<>(q.options.size());
		
		for (int i = 0; i < q.getOptions().size(); i++) {
			counts.add(0);
		}
		
		long totalResponses = responses.stream().mapToInt(a -> {
				if (seen.add(a.getUser())) {
					counts.set(a.getChoice(), counts.get(a.getChoice()) + 1);	
					return 1;
				} else {
					return 0;
				}
			}).sum();

		return new Result(counts, q.getOptions(), q.getQuestion(), q.getPoller(), (int) totalResponses);
	}

	@ChatButton(buttonText = "poll0", value=Question.class)
	public WorkResponse poll0(User u, Question q, Chat c) {
		return chooseResponse(q, u, 0, c);
	}

	private WorkResponse chooseResponse(Question q, User u, int r, Chat c) {
		Answer a = new Answer(u, Instant.now(), r, q.question, q.options.get(r));
		WorkResponse wr = new WorkResponse(c, a, WorkMode.VIEW);
		HeaderDetails h = new HeaderDetails(Arrays.asList(q.getId()+ANSWER_SUFFIX));
		wr.getData().put(HeaderDetails.KEY, h);
		return wr;
	}

	@ChatButton(buttonText = "poll1", value=Question.class)
	public WorkResponse poll1(User u, Question q, Chat c) {
		return chooseResponse(q, u, 1, c);
	}

	@ChatButton(buttonText = "poll2", value=Question.class)
	public WorkResponse poll2(User u, Question q, Chat c) {
		return chooseResponse(q, u, 2, c);
	}

	@ChatButton(buttonText = "poll3", value=Question.class)
	public WorkResponse poll3(User u, Question q, Chat c) {
		return chooseResponse(q, u, 3, c);
	}

	@ChatButton(buttonText = "poll4", value=Question.class)
	public WorkResponse poll4(User u, Question q, Chat c) {
		return chooseResponse(q, u, 4, c);
	}

	@ChatButton(buttonText = "poll5", value=Question.class)
	public WorkResponse poll5(User u, Question q, Chat c) {
		return chooseResponse(q, u, 5, c);
	}

}
