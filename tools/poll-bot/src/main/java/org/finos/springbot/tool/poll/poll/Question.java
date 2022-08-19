package org.finos.springbot.tool.poll.poll;

import java.time.Instant;
import java.util.List;

import org.finos.springbot.symphony.content.HashTag;
import org.finos.springbot.workflow.annotations.Display;
import org.finos.springbot.workflow.annotations.Work;
import org.finos.springbot.workflow.content.User;

@Work
//@Template(view = "question")
public class Question {

	public String question;
	
	@Display(visible = false)
	public List<String> options;
	
	@Display(visible = false)
	public String id;
	public User poller;
	
	@Display(visible = false)
	private Instant endTime;

	public Question(String question, List<String> options, String id, User poller) {
		super();
		this.question = question;
		this.options = options;
		this.id = id;
		this.poller = poller;
	}

	public Question() {
		super();
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public List<String> getOptions() {
		return options;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public User getPoller() {
		return poller;
	}

	public void setPoller(User poller) {
		this.poller = poller;
	}
	

	public Instant getEndTime() {
		return endTime;
	}

	public void setEndTime(Instant t) {
		this.endTime = t;
	}

}
