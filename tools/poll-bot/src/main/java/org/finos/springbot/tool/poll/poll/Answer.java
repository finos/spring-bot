package org.finos.springbot.tool.poll.poll;

import java.time.Instant;

import org.finos.springbot.symphony.content.HashTag;
import org.finos.springbot.workflow.annotations.Display;
import org.finos.springbot.workflow.annotations.Template;
import org.finos.springbot.workflow.annotations.Work;
import org.finos.springbot.workflow.content.User;

@Work
@Template(view="answer")
public class Answer {
	
	private User user;

	@Display(visible = false)
	private Instant time;
	
	@Display(visible = false)
	private Integer choice;

	@Display(visible = false)
	private String questionText;
	
	@Display(visible = false)
	private String choiceText;

	public Answer() {
		super();
	}

	public Answer(User User, Instant time, Integer choice, String questionText, String choiceText) {
		super();
		this.user = User;
		this.time = time;
		this.choice = choice;
		this.choiceText = choiceText;
		this.questionText = questionText;
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User User) {
		this.user = User;
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
