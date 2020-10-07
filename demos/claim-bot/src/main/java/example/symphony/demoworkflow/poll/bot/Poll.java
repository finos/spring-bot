package example.symphony.demoworkflow.poll.bot;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.github.deutschebank.symphony.workflow.content.User;
import com.github.deutschebank.symphony.workflow.java.Work;

@Work
@JsonIgnoreProperties(ignoreUnknown = true)
public class Poll {
	public String id;
	public String pollStreamId;
	public Instant created;

	public User creator;
	public String creatorDisplayName;
	public String question;
	public List<User> participants;
	public List<String> answers;

	public Integer timeLimit;

	public int count;
	public List<Integer> timeLimits;

	public Poll() {
		super();
	}
	
	public Poll(String id) {
		super();
		this.id = id;
	}
	
	public Poll(String id, String creatorDisplayName, String question) {
		super();
		this.id = id;
		this.creatorDisplayName = creatorDisplayName;
		this.question = question;
	}

	public Poll(String id, String question, Integer timeLimit) {
		super();
		this.id = id;
		this.timeLimit = timeLimit;
		this.question = question;
	}

	public Poll(User creator, String creatorDisplayName, int count, List<Integer> timeLimits, String id,
			String pollStreamId, List<User> participants, Instant created) {
		super();
		this.count = count;
		this.timeLimits = timeLimits;
		this.id = id;
		this.creator = creator;
		this.creatorDisplayName = creatorDisplayName;
		this.pollStreamId = pollStreamId;
		this.participants = participants;
		this.created = created;
	}

	

	public Instant getCreated() {
		return created;
	}

	public void setCreated(Instant created) {
		this.created = created;
	}

	public Integer getTimeLimit() {
		return timeLimit;
	}

	public void setTimeLimit(Integer timeLimit) {
		this.timeLimit = timeLimit;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public String getCreatorDisplayName() {
		return creatorDisplayName;
	}

	public void setCreatorDisplayName(String creatorDisplayName) {
		this.creatorDisplayName = creatorDisplayName;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public List<User> getParticipants() {
		return participants;
	}

	public void setParticipants(List<User> participants) {
		this.participants = participants;
	}

	public List<String> getAnswers() {
		return answers;
	}

	public void setAnswers(List<String> answers) {
		this.answers = answers;
	}

	public String getPollStreamId() {
		return pollStreamId;
	}

	public void setPollStreamId(String pollStreamId) {
		this.pollStreamId = pollStreamId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<Integer> getTimeLimits() {
		return timeLimits;
	}

	public void setTimeLimits(List<Integer> timeLimits) {
		this.timeLimits = timeLimits;
	}

}
