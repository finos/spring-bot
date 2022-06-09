package org.finos.springbot.tool.poll.poll;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.finos.springbot.tool.poll.poll.Results.Count;
import org.finos.springbot.workflow.actions.FormAction;
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
import org.finos.springbot.workflow.response.DataResponse;
import org.finos.springbot.workflow.response.WorkResponse;
import org.finos.springbot.workflow.response.handlers.ResponseHandlers;
import org.springframework.beans.factory.annotation.Autowired;
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
	AllHistory history;
	
//	@Autowired
//	TaskScheduler taskScheduler;
//
//	
	
	@ChatRequest(value="poll", description = "Start A Poll")
	@ChatResponseBody(workMode = WorkMode.EDIT) 
	public PollCreateForm pollForm() {
		return new PollCreateForm();
	}

	@ChatButton(buttonText ="start", showWhen = WorkMode.EDIT, value = PollCreateForm.class)
	public void poll(
			PollCreateForm cf, 
			Chat r, 
			User a) {
		
		Results newResults = createResultsMessage(cf, r, a);
		ButtonList buttons = buildButtons(newResults);
		WorkResponse wr = new WorkResponse(r, newResults, WorkMode.VIEW, buttons, new ErrorMap());
		rh.accept(wr);
	}
	

	private Results createResultsMessage(PollCreateForm cf, Chat r, User a) {
		List<Count> counts = Arrays.asList(cf.option1, cf.option2, cf.option3, cf.option4, cf.option5, cf.option6)	
				.stream()
				.filter(s -> StringUtils.hasText(s))
				.map(s -> new Count(s, 0))
				.collect(Collectors.toList());
		
		
		Results results = new Results();
		results.setPoller(a);
		results.setQuestion(cf.getQuestion());
		results.setCounts(counts);
		results.setVoted(Collections.emptyList());
		
		return results;
	}

	private ButtonList buildButtons(Results r) {
		int[] i = { 0 };
		
		ButtonList buttons = new ButtonList(r.getCounts().stream()
				.map(s -> new Button(PollController.class,"poll"+(i[0]++), Type.ACTION, s.getAnswer()))
				.collect(Collectors.toList()));

		return buttons;
	}
	
	private void resendResults(Chat r, Results results, String originatingMessageId) {
		ButtonList buttons = buildButtons(results);

		WorkResponse wr = new WorkResponse(r, results, WorkMode.VIEW, buttons, new ErrorMap());
		wr.getData().put(DataResponse.MESSAGE_UPDATE_ID_KEY, originatingMessageId);
		rh.accept(wr);
	}
	
	private void chooseResponse(Chat c, Results q, User u, int r, FormAction fa) {
		if (q.getVoted().contains(u)) {
			// user has already voted - ignore?
		} else {
			// increment count
			Count count = q.getCounts().get(r);
			count.setVotes(count.getVotes()+1);
			q.getVoted().add(u);
			resendResults(c, q, fa.getOriginatingMessageId());
		}
		
	}

//	private void doScheduling(PollVoteForm p, PollCreateForm cf, Chat r) {
//		if (cf.isEndAutomatically()) {
//			Instant endTime = Instant.now().plus(cf.getTime(), cf.getTimeUnit());
//			p.setEndTime(endTime);
//			taskScheduler.schedule(() -> {
//				Results result = end(p, r, h);
//				WorkResponse out = new WorkResponse(r, result, WorkMode.VIEW);
//				rh.accept(out);
//				
//			}, endTime);
//		}
//	}
//
//	@ChatButton(value = PollVoteForm.class, showWhen = WorkMode.VIEW, buttonText = "End Poll Now")
//	public Results end(PollVoteForm p, Chat r, AllHistory h) {
//		List<Answer> responses = h.getFromHistory(Answer.class, p.getId(), null, null);
//
//		List<Integer> counts = new ArrayList<>(p.getOptions().size());
//		int totalResponses = 0;
//		
//		for (int i = 0; i < p.getOptions().size(); i++) {
//			counts.add(0);
//		}
//
//		for (Answer pollResponse : responses) {
//			counts.set(pollResponse.getChoice(), counts.get(pollResponse.getChoice()) + 1);
//			totalResponses ++;
//		}
//
//
//		return new Results(p.getId(), counts, p.getOptions(), p.getQuestion(), p.getPoller(), totalResponses);
//	}
//
//	private static WorkResponse createResponseForUser(PollCreateForm cf, List<String> choices, HashTag id,
//			ButtonList buttons, User u) {
//		Question q = new Question(cf.getQuestion(), choices, id, u);
//		return new WorkResponse(u, q, WorkMode.VIEW, buttons, new ErrorMap());
//	}
//	
	@ChatButton(buttonText = "poll0", value=Results.class)
	public void poll0(Chat c, User u, Results q, FormAction fa) {
		chooseResponse(c, q, u, 0, fa);
	}

	@ChatButton(buttonText = "poll1", value=Results.class)
	public void poll1(Chat c, User u, Results q, FormAction fa) {
		chooseResponse(c, q, u, 1, fa);
	}

	@ChatButton(buttonText = "poll2", value=Results.class)
	public void poll2(Chat c, User u, Results q, FormAction fa) {
		chooseResponse(c, q, u, 2, fa);
	}

	@ChatButton(buttonText = "poll3", value=Results.class)
	public void poll3(Chat c, User u, Results q, FormAction fa) {
		chooseResponse(c, q, u, 3, fa);
	}

	@ChatButton(buttonText = "poll4", value=Results.class)
	public void poll4(Chat c, User u, Results q, FormAction fa) {
		chooseResponse(c, q, u, 4, fa);
	}

	@ChatButton(buttonText = "poll5", value=Results.class)
	public void poll5(Chat c, User u, Results q, FormAction fa) {
		chooseResponse(c, q, u, 5, fa);
	}

}
