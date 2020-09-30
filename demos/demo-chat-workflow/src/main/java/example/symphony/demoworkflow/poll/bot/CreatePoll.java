package example.symphony.demoworkflow.poll.bot;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.deutschebank.symphony.workflow.Workflow;
import com.github.deutschebank.symphony.workflow.content.Room;
import com.github.deutschebank.symphony.workflow.content.User;
import com.github.deutschebank.symphony.workflow.history.History;
import com.github.deutschebank.symphony.workflow.java.Exposed;
import com.github.deutschebank.symphony.workflow.java.Work;
import com.github.deutschebank.symphony.workflow.response.MessageResponse;
import com.github.deutschebank.symphony.workflow.response.Response;

import example.symphony.demoworkflow.poll.service.MarkupService;
import example.symphony.demoworkflow.util.MessageUtils;
import lombok.extern.slf4j.Slf4j;

@Work(name = "Poll Create", instructions = "Please participate in our poll", editable = false)
@JsonIgnoreProperties(ignoreUnknown = true)
@Slf4j
public class CreatePoll extends Poll {
	public String option1;
	public String option2;
	public String option3;
	public String option4;
	public String option5;
	public String option6;

	public CreatePoll(String question, String option1, String option2, String option3, String option4, String option5,
			String option6, Integer timeLimit, String pollId) {
		super(pollId, question, timeLimit);
		this.option1 = option1;
		this.option2 = option2;
		this.option3 = option3;
		this.option4 = option4;
		this.option5 = option5;
		this.option6 = option6;
	}

	public CreatePoll() {
		super();
	}

	@Exposed
	public static List<Response> createpoll(CreatePoll cp, User u, Room r, Workflow wf) {

		List<Response> responses = new ArrayList<>();

		List<String> answers = Arrays.asList(cp.option1, cp.option2, cp.option3, cp.option4, cp.option5, cp.option6)
				.stream().filter(s -> !StringUtils.isEmpty(s)).collect(Collectors.toList());

		if (answers.size() < 2) {
			String rejectMsg = String.format("<mention uid=\"%d\"/> Your poll contains less than 2 valid options",
					u.getName());
			log.info("Create poll by {} rejected as there are less than 2 valid options", u.getName());
			responses.add(new MessageResponse(wf, r, null, "Reject Poll", "", rejectMsg));
			return responses;
		}

		List<Poll> polls = wf.getHistoryApi().getFromHistory(Poll.class, null, null);
		List<PollResultsData> pollResultDataList = wf.getHistoryApi().getFromHistory(PollResultsData.class, null, null);
		
		Optional<Poll> poll = polls.stream().filter(p -> {
			Optional<PollResultsData> pollResultFound = pollResultDataList.stream()
					.filter(pd -> (pd.getClass().equals(PollResultsData.class) && p.getId().equalsIgnoreCase(pd.getId()))).findFirst();
			if (p.getClass().equals(Poll.class) && !pollResultFound.isPresent()
					&& p.getId().equalsIgnoreCase(cp.getId())) {
				return true;
			}
			return false;
		}).findFirst();

		if (poll.isPresent()) {
			poll.get().setTimeLimit(cp.getTimeLimit());
			poll.get().setQuestion(MessageUtils.escapeText(cp.getQuestion().toString()));
			poll.get().setAnswers(answers);

			cp.setAnswers(answers);
			cp.setCreatorDisplayName(poll.get().getCreatorDisplayName());

			responses.addAll(poll.get().getParticipants().stream().map(user -> createResponseForUser(cp, wf, user))
					.collect(Collectors.toList()));

			// Start timer
			String endPollByTimerNote = "";
			if (cp.getTimeLimit() > 0) {
				Timer timer = new Timer("PollTimer" + poll.get().getId());
				timer.schedule(new TimerTask() {
					public void run() {
						handleEndPoll(null, poll.get().getCreator(), null, wf);
					}
				}, 60000L * cp.getTimeLimit());

				endPollByTimerNote = " or wait " + cp.getTimeLimit() + " minute" + (cp.getTimeLimit() > 1 ? "s" : "");
			}

			log.info("New poll by {} creation complete", u.getName());

			responses.add(new MessageResponse(wf, r, poll.get(), "Poll Created", "",
					String.format(
							"Your poll has been created. You can use <b>/endpoll</b>%s to end this poll: <i>%s</i>",
							endPollByTimerNote, poll.get().getQuestion())));
		} else {
			responses.add(new MessageResponse(wf, r, null, "Poll Creation Failed", "",
					"Your poll has not been initialized. You can use <b>/initpoll</b> to initialize poll!"));
		}
		return responses;
	}

	private static Response createResponseForUser(CreatePoll cp, Workflow wf, User r) {
		MarkupService markupService = new MarkupService();
		return new MessageResponse(wf, r, cp, "Blast Poll", "", markupService.getBlastPollTemplate());
	}

	public static List<Response> handleEndPoll(Room r, User user, String displayName, Workflow wf) {
		log.info("End poll requested by {}", displayName != null ? displayName : "[Timer]");

		List<Response> responses = new ArrayList<>();
		History h = wf.getHistoryApi();

		List<Poll> polls = h.getFromHistory(Poll.class, null, null);
		List<PollResultsData> pollResultDataList = h.getFromHistory(PollResultsData.class, null, null);
		
		Optional<Poll> poll = polls.stream().filter(p -> {
			Optional<PollResultsData> pollResultFound = pollResultDataList.stream()
					.filter(pd -> (pd.getClass().equals(PollResultsData.class) && p.getId().equalsIgnoreCase(pd.getId()))).findFirst();
			if (p.getClass().equals(Poll.class) && !pollResultFound.isPresent()
					&& p.getCreator() != null && p.getCreator().getAddress().equalsIgnoreCase(user.getAddress())) {
				return true;
			}
			return false;
		}).findFirst();

		if (!poll.isPresent()) {
			if (r != null) {
				responses.add(new MessageResponse(wf, r, null, "End Poll", "", "You have no active poll to end"));
				log.info("User {} has no active poll to end", displayName);
			} else {
				log.info("Poll by {} time limit reached but poll was already ended", user.getName());
			}
			return responses;
		}

		List<PollVote> v = h.getFromHistory(PollVote.class, null, null);
		List<PollVote> votes = v.stream().filter(vote -> poll.get().getId().equalsIgnoreCase(vote.getPollId()))
				.collect(Collectors.toList());

		Optional<Room> rm = wf.getRoomsApi().getAllRooms().stream()
				.filter(room -> room.getId().equalsIgnoreCase(poll.get().getPollStreamId())).findFirst();

		if (votes.isEmpty()) {
			String response = String.format("<mention uid=\"%s\" /> Poll ended but with no results to show",
					poll.get().getCreator().getId());
			log.info("Poll {} ended with no votes", poll.get().getId());
			
			PollResultsData pollResultData = new PollResultsData(poll.get().getId(), poll.get().getCreatorDisplayName(),
					poll.get().getQuestion(), null, Instant.now());
			responses.add(new MessageResponse(wf, rm.get(), pollResultData, "End Poll", "", response));
		} else {
			List<PollResult> pollResults = new ArrayList<>();

			// Initially Add in 0 votes for options
			poll.get().getAnswers().stream().map(PollResult::new).forEach(pollResults::add);

			// Aggregate vote results
			for (PollVote pollVote : votes) {
				pollResults.forEach(rs -> {
					if (pollVote.getAnswer().equalsIgnoreCase(rs.getAnswer())) {
						rs.setCount(rs.getCount() + 1l);
					}
				});
			}

			// Add in widths
			long maxVal = Collections.max(pollResults, Comparator.comparingLong(PollResult::getCount)).getCount();
			pollResults.forEach(rs -> rs.setWidth(Math.max(1, (int) (((float) rs.getCount() / maxVal) * 200))));

			PollResultsData pollResultData = new PollResultsData(poll.get().getId(), poll.get().getCreatorDisplayName(),
					poll.get().getQuestion(), pollResults, Instant.now());

			log.info("Poll {} ended with results {}", poll.get().getId(), pollResults.toString());

			MarkupService markupService = new MarkupService();
			responses.add(new MessageResponse(wf, rm.get(), pollResultData, "Poll Result", "",
					markupService.getResultsTemplate()));
		}

		return responses;
	}

	public String getOption1() {
		return option1;
	}

	public void setOption1(String option1) {
		this.option1 = option1;
	}

	public String getOption2() {
		return option2;
	}

	public void setOption2(String option2) {
		this.option2 = option2;
	}

	public String getOption3() {
		return option3;
	}

	public void setOption3(String option3) {
		this.option3 = option3;
	}

	public String getOption4() {
		return option4;
	}

	public void setOption4(String option4) {
		this.option4 = option4;
	}

	public String getOption5() {
		return option5;
	}

	public void setOption5(String option5) {
		this.option5 = option5;
	}

	public String getOption6() {
		return option6;
	}

	public void setOption6(String option6) {
		this.option6 = option6;
	}

	@Override
	public String toString() {
		return "CreatePoll [question=" + getQuestion() + ", option1=" + option1 + ", option2=" + option2 + ", option3="
				+ option3 + ", option4=" + option4 + ", option5=" + option5 + ", option6=" + option6 + ", timeLimit="
				+ getTimeLimit() + ", id=" + getId() + "]";
	}

}
