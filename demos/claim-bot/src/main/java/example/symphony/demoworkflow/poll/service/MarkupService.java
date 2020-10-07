package example.symphony.demoworkflow.poll.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import com.github.deutschebank.symphony.workflow.Workflow;
import com.github.deutschebank.symphony.workflow.content.Room;
import com.github.deutschebank.symphony.workflow.content.User;

import example.symphony.demoworkflow.WorkflowConfig.MemberQueryWorkflow;
import example.symphony.demoworkflow.poll.bot.Poll;
import example.symphony.demoworkflow.poll.bot.PollConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MarkupService {
	private String pollCreateTemplate = loadTemplate("/template/poll-create-form.ftl");
    private String pollBlastTemplate = loadTemplate("/template/poll-blast-form.ftl");
    private String pollResultsTemplate = loadTemplate("/template/poll-results.ftl");

	private PollConfig pollConfig = new PollConfig();

	private String loadTemplate(String fileName) {
		InputStream stream = MarkupService.class.getResourceAsStream(fileName);
		try (BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
			return br.lines().collect(Collectors.joining(System.lineSeparator()));
		} catch (IOException e) {
			log.error("Unable to load template for {}", fileName);
			return null;
		}
	}

	public String getCreatePollTemplate() {
		return pollCreateTemplate;
	}
	
	public String getBlastPollTemplate() {
		return pollBlastTemplate;
	}
	
	public String getResultsTemplate() {
		return pollResultsTemplate;
	}

	public Poll getPollCreateData(User u, Room r, Workflow wf, String id) {		
		List<User> participants = ((MemberQueryWorkflow) wf).getMembersInRoom(r).stream().filter(user -> !((MemberQueryWorkflow) wf).isMe(user))
				.collect(Collectors.toList());
		
		return new Poll(u, u.getName(), pollConfig.getOptions(), pollConfig.getTimeLimits(), id, r.getId(), participants, Instant.now());
	}
	
}
