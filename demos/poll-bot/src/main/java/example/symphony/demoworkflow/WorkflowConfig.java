/**
 * 
 */
package example.symphony.demoworkflow;

import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.java.workflow.ClassBasedWorkflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.pod.RoomMembershipApi;
import com.symphony.api.pod.UsersApi;

import example.symphony.demoworkflow.poll.Answer;
import example.symphony.demoworkflow.poll.Poll;
import example.symphony.demoworkflow.poll.PollCreateForm;
import example.symphony.demoworkflow.poll.Question;
import example.symphony.demoworkflow.poll.Result;

/**
 * @author rupnsur
 *
 */
@Configuration
public class WorkflowConfig {

	@Autowired
	RoomMembershipApi membershipApi;

	@Autowired
	UsersApi usersApi;

	@Autowired
	SymphonyIdentity botIdentity;

	@Bean
	public Workflow appWorkflow() {
		ClassBasedWorkflow wf = new ClassBasedWorkflow(WorkflowConfig.class.getCanonicalName());
		wf.addClass(Poll.class);
		wf.addClass(PollCreateForm.class);
		wf.addClass(Result.class);
		wf.addClass(Answer.class);
		wf.addClass(Question.class);
		return wf;
	}
}
