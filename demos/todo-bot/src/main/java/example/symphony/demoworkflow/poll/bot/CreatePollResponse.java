package example.symphony.demoworkflow.poll.bot;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.deutschebank.symphony.workflow.Workflow;
import com.github.deutschebank.symphony.workflow.content.Room;
import com.github.deutschebank.symphony.workflow.content.User;
import com.github.deutschebank.symphony.workflow.history.History;
import com.github.deutschebank.symphony.workflow.java.Exposed;
import com.github.deutschebank.symphony.workflow.java.Work;
import com.github.deutschebank.symphony.workflow.response.MessageResponse;
import com.github.deutschebank.symphony.workflow.response.Response;

import lombok.extern.slf4j.Slf4j;

/**
 * @author rupnsur
 *
 */
@Work(name = "Poll Response", editable = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@Slf4j
public class CreatePollResponse extends Poll {

	public CreatePollResponse() {
		super();
	}

	public CreatePollResponse(String id) {
		super(id);
	}

	@Exposed
	public static List<Response> option_0(CreatePollResponse pr, User u, Workflow wf) {
		return chooseResponse(pr, u, 0, wf);
	}

	@Exposed
	public static List<Response> option_1(CreatePollResponse pr, User u, Workflow wf) {
		return chooseResponse(pr, u, 1, wf);
	}

	@Exposed
	public static List<Response> option_2(CreatePollResponse pr, User u, Workflow wf) {
		return chooseResponse(pr, u, 2, wf);
	}

	@Exposed
	public static List<Response> option_3(CreatePollResponse pr, User u, Workflow wf) {
		return chooseResponse(pr, u, 3, wf);
	}

	@Exposed
	public static List<Response> option_4(CreatePollResponse pr, User u, Workflow wf) {
		return chooseResponse(pr, u, 4, wf);
	}

	@Exposed
	public static List<Response> option_5(CreatePollResponse pr, User u, Workflow wf) {
		return chooseResponse(pr, u, 5, wf);
	}

	private static List<Response> chooseResponse(CreatePollResponse pr, User u, int answerIndex, Workflow wf) {

		List<Response> responses = new ArrayList<>();
		History h = wf.getHistoryApi();

		List<Poll> polls = h.getFromHistory(Poll.class, null, null);
		Optional<Poll> poll = polls.stream().filter(p -> (p.getClass().equals(Poll.class) && pr.getId().equalsIgnoreCase(p.getId()))).findFirst();

		if (!poll.isPresent()) {
			log.info("Invalid vote cast by {} on room id {}", u.getName(), u.getId());
			responses.add(new MessageResponse(wf, u, null, "Reject Vote", "",
					"You have submitted a vote for an invalid poll"));
			return responses;
		}
		final String streamId = poll.get().getPollStreamId();
		String answer = poll.get().getAnswers().get(answerIndex);

		Optional<Room> r = wf.getRoomsApi().getAllRooms().stream()
				.filter(room -> room.getId().equalsIgnoreCase(streamId)).findFirst();
		
		List<PollResultsData> pollResultDataList = h.getFromHistory(PollResultsData.class, null, null);
		Optional<PollResultsData> pollResultFound = pollResultDataList.stream()
				.filter(pd -> (pd.getClass().equals(PollResultsData.class) && poll.get().getId().equalsIgnoreCase(pd.getId()))).findFirst();

		if (pollResultFound.isPresent()) {
			String pollEndedMsg = String.format("This poll has ended and no longer accepts votes: <i>%s</i>",
					poll.get().getQuestion());
			log.info("Rejected vote [{}] cast by {} in room {} on expired poll: {}", answer, u.getName(),
					u, poll.get().getQuestion());
			responses.add(new MessageResponse(wf, u, null, "Reject Vote", "", pollEndedMsg));
			return responses;
		}
		
		List<PollVote> results = h.getFromHistory(PollVote.class, null, null);
		Optional<PollVote> pollVote = results.stream().filter(v -> (pr.getId().equalsIgnoreCase(v.getPollId()) && u.getAddress().equalsIgnoreCase(v.getUserId().getAddress()))).findFirst();
		
		String response, creatorNotification;
		if (pollVote.isPresent()) {
			pollVote.get().setAnswer(answer);
			response = String.format("Your vote has been updated to <b>%s</b> for the poll: <i>%s</i>", answer,
					poll.get().getQuestion());
			creatorNotification = String.format("has changed their vote to: <b>%s</b>", answer);
			log.info("Vote updated to [{}] on poll {} by {}", answer, poll.get().getId(), u.getName());
		} else {
			pollVote = Optional.of(new PollVote(poll.get().getId(), answer, u));
			response = String.format("Thanks for voting <b>%s</b> for the poll: <i>%s</i>", answer,
					poll.get().getQuestion());
			creatorNotification = String.format("<mention uid=\"%s\"/> has voted <b>%s</b> for the poll: <i>%s</i> ",
					u.getId(), answer, poll.get().getQuestion());
			log.info("New vote [{}] cast on poll {} by {}", answer, poll.get().getId(), u.getName());
		}

		responses.add(new MessageResponse(wf, u, null, "Vote Cast", "", response));
		responses.add(new MessageResponse(wf, r.get(), pollVote.get(), "Vote Cast", "", creatorNotification));

		return responses;
	}
}
