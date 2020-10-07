package example.symphony.demoworkflow.poll.bot;

import java.time.Instant;
import java.util.List;

import com.github.deutschebank.symphony.workflow.java.Work;

@Work
public class PollResultsData extends Poll {
	List<PollResult> results;
	Instant ended;

	public PollResultsData() {
		super();
	}
	
	public PollResultsData(String id, String creatorDisplayName, String question, List<PollResult> results, Instant ended) {
		super(id, creatorDisplayName, question);
		this.results = results;
		this.ended = ended;
	}

	public List<PollResult> getResults() {
		return results;
	}

	public void setResults(List<PollResult> results) {
		this.results = results;
	}

	public Instant getEnded() {
		return ended;
	}

	public void setEnded(Instant ended) {
		this.ended = ended;
	}

	@Override
	public String toString() {
		return "PollResultsData [results=" + results + ", ended=" + ended + "]";
	}	
}
