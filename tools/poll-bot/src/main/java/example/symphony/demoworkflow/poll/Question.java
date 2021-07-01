package example.symphony.demoworkflow.poll;

import java.time.Instant;
import java.util.List;

import org.finos.symphony.toolkit.workflow.annotations.Exposed;
import org.finos.symphony.toolkit.workflow.annotations.Work;
import org.finos.symphony.toolkit.workflow.content.ID;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.sources.symphony.Template;

@Work
@Template(view = "classpath:/template/question.ftl")
public class Question {

	public String question;
	public List<String> options;
	public ID id;
	public User poller;

	public Question(String question, List<String> options, ID id, User poller) {
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

	public ID getId() {
		return id;
	}

	public void setId(ID id) {
		this.id = id;
	}

	@Exposed(addToHelp = false)
	public Answer poll0(User u) {
		return chooseResponse(u, 0);
	}

	private Answer chooseResponse(User u, int r) {
		return new Answer(id, u, Instant.now(), r, question, options.get(r));
	}

	@Exposed(addToHelp = false)
	public Answer poll1(User u) {
		return chooseResponse(u, 1);
	}

	@Exposed(addToHelp = false)
	public Answer poll2(User u) {
		return chooseResponse(u, 2);
	}

	@Exposed(addToHelp = false)
	public Answer poll3(User u) {
		return chooseResponse(u, 3);
	}

	@Exposed(addToHelp = false)
	public Answer poll4(User u) {
		return chooseResponse(u, 4);
	}

	@Exposed(addToHelp = false)
	public Answer poll5(User u) {
		return chooseResponse(u, 5);
	}

	public User getPoller() {
		return poller;
	}

	public void setPoller(User poller) {
		this.poller = poller;
	}
}
