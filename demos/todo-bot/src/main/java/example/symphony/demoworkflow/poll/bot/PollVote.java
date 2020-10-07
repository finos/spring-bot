package example.symphony.demoworkflow.poll.bot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.deutschebank.symphony.workflow.content.User;
import com.github.deutschebank.symphony.workflow.java.Work;

@Work
@JsonIgnoreProperties(ignoreUnknown = true)
public class PollVote {
	private String pollId;
	private String answer;
	private User userId;

	public PollVote() {
		super();
	}

	public PollVote(String pollId, String answer, User userId) {
		super();
		this.pollId = pollId;
		this.answer = answer;
		this.userId = userId;
	}

	public String getPollId() {
		return pollId;
	}

	public void setPollId(String pollId) {
		this.pollId = pollId;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public User getUserId() {
		return userId;
	}

	public void setUserId(User userId) {
		this.userId = userId;
	}

}
