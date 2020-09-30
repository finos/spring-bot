package example.symphony.demoworkflow.poll;

import java.time.Instant;

import com.github.deutschebank.symphony.workflow.content.User;
import com.github.deutschebank.symphony.workflow.content.ID;
import com.github.deutschebank.symphony.workflow.java.Work;

@Work(editable = false, name = "Answer Chosen!")
public class PollResponse {

	private ID pollID;
	private User User;
	private Instant time;
	private Integer choice;
	private String choiceText;

	public PollResponse() {
		super();
	}

	public PollResponse(ID pollID, User User, Instant time, Integer choice, String choiceText) {
		super();
		this.pollID = pollID;
		this.User = User;
		this.time = time;
		this.choice = choice;
		this.choiceText = choiceText;
	}

	public ID getPollID() {
		return pollID;
	}

	public void setPollID(ID pollID) {
		this.pollID = pollID;
	}

	public User getUser() {
		return User;
	}

	public void setUser(User User) {
		this.User = User;
	}

	public Instant getTime() {
		return time;
	}

	public void setTime(Instant time) {
		this.time = time;
	}

	public Integer getChoice() {
		return choice;
	}

	public void setChoice(Integer choice) {
		this.choice = choice;
	}

	public String getChoiceText() {
		return choiceText;
	}

	public void setChoiceText(String choiceText) {
		this.choiceText = choiceText;
	}

}
