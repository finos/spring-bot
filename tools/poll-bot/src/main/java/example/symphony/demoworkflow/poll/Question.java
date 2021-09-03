package example.symphony.demoworkflow.poll;

import java.util.List;

import org.finos.symphony.toolkit.workflow.annotations.Template;
import org.finos.symphony.toolkit.workflow.annotations.Work;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.HashTag;

@Work
@Template(view = "question")
public class Question {

	public String question;
	public List<String> options;
	public HashTag id;
	public User poller;

	public Question(String question, List<String> options, HashTag id, User poller) {
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

	public HashTag getId() {
		return id;
	}

	public void setId(HashTag id) {
		this.id = id;
	}

	public User getPoller() {
		return poller;
	}

	public void setPoller(User poller) {
		this.poller = poller;
	}
}
