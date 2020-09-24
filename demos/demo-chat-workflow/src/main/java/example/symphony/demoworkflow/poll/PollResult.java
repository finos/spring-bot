package example.symphony.demoworkflow.poll;

import java.util.ArrayList;
import java.util.List;

import com.github.deutschebank.symphony.workflow.java.Work;

@Work(editable = false)
public class PollResult {

	private List<Integer> counts = new ArrayList<>();
	
	private Integer totalResponses = 0;

	public Integer getTotalResponses() {
		return totalResponses;
	}

	public void setTotalResponses(Integer totalResponses) {
		this.totalResponses = totalResponses;
	}

	public List<Integer> getCounts() {
		return counts;
	}

	public void setCounts(List<Integer> counts) {
		this.counts = counts;
	}
	
	
}
