package example.symphony.demoworkflow.poll;

import java.time.Instant;
import java.util.List;

import org.finos.springbot.symphony.content.HashTag;
import org.finos.springbot.workflow.annotations.Template;
import org.finos.springbot.workflow.annotations.Work;
import org.finos.springbot.workflow.content.User;

@Work
@Template(view = "poll")
public class Poll {

	private HashTag id;
	private Instant endTime;
	private List<String> options;
	private String question;
	private User poller;

	public Poll(List<String> options) {
		super();
		this.options = options;
	}

	public Poll() {
		super();
	}
	
	public List<String> getOptions() {
		return options;
	}

	public void setOptions(List<String> options) {
		this.options = options;
	}

	public Instant getEndTime() {
		return endTime;
	}

	public void setEndTime(Instant t) {
		this.endTime = t;
	}

	public HashTag getId() {
		return id;
	}

	public void setId(HashTag id) {
		this.id = id;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public User getPoller() {
		return poller;
	}

	public void setPoller(User poller) {
		this.poller = poller;
	}

}
