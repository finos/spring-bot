package org.finos.springbot.tool.poll.poll;

import java.util.List;

import org.finos.springbot.workflow.annotations.Display;
import org.finos.springbot.workflow.annotations.Work;
import org.finos.springbot.workflow.content.User;

@Work
public class Results {
	
	public static class Count {
		
		private String answer;
		private Integer votes;
		
		public Count() {
			super();
		}

		public Count(String answer, Integer votes) {
			super();
			this.answer = answer;
			this.votes = votes;
		}
		
		public String getAnswer() {
			return answer;
		}
		public void setAnswer(String answer) {
			this.answer = answer;
		}
		public Integer getVotes() {
			return votes;
		}
		public void setVotes(Integer votes) {
			this.votes = votes;
		}
		
		
	}

	private String question;
	private List<Count> counts;
	private User poller;
	
	@Display(visible = false)
	private List<User> voted;
	
	public List<User> getVoted() {
		return voted;
	}

	public void setVoted(List<User> voted) {
		this.voted = voted;
	}

	public List<Count> getCounts() {
		return counts;
	}

	public void setCounts(List<Count> counts) {
		this.counts = counts;
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
