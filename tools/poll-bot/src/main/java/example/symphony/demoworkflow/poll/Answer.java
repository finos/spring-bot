package example.symphony.demoworkflow.poll;

import java.time.Instant;

import org.finos.symphony.toolkit.workflow.annotations.Work;
import org.finos.symphony.toolkit.workflow.content.ID;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.sources.symphony.Template;

@Work(editable = false, name = "Answer Chosen!")
@Template(view="classpath:/template/answer.ftl")
public class Answer {

	private ID pollID;
	private User User;
	private Instant time;
	private Integer choice;
	private String questionText;
	private String choiceText;

	public Answer() {
		super();
	}

	public Answer(ID pollID, User User, Instant time, Integer choice, String questionText, String choiceText) {
		super();
		this.pollID = pollID;
		this.User = User;
		this.time = time;
		this.choice = choice;
		this.choiceText = choiceText;
		this.questionText = questionText;
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

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

}
