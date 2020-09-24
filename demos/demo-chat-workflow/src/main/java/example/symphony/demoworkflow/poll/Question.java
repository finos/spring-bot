package example.symphony.demoworkflow.poll;

import java.time.Instant;
import java.util.List;

import com.github.deutschebank.symphony.workflow.content.ID;
import com.github.deutschebank.symphony.workflow.content.User;
import com.github.deutschebank.symphony.workflow.java.Exposed;
import com.github.deutschebank.symphony.workflow.java.Work;

@Work
public class Question {

	public String question;
	public List<String> options;
	public ID id;
	
	
	
	public Question(String question, List<String> options, ID id) {
		super();
		this.question = question;
		this.options = options;
		this.id = id;
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

	@Exposed
	public PollResponse poll0(User u) {
		return chooseResponse(u, 0);
	}

	private PollResponse chooseResponse(User u, int r) {
		System.out.println("You chose option "+r+" which was "+options.get(r));
		return new PollResponse(id, u, Instant.now(), r, options.get(r));
	}
	
	@Exposed
	public PollResponse poll1(User u) {
		return chooseResponse(u, 1);
	}
	
	@Exposed
	public PollResponse poll2(User u) {
		return chooseResponse(u, 2);
	}
	
	@Exposed
	public PollResponse poll3(User u) {
		return chooseResponse(u, 3);
	}
	
	@Exposed
	public PollResponse poll4(User u) {
		return chooseResponse(u, 4);
	}
	
	@Exposed
	public PollResponse poll5(User u) {
		return chooseResponse(u, 5);
	}
}
