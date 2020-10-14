/**
 * 
 */
package example.symphony.demoworkflow;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.deutschebank.symphony.workflow.Workflow;
import com.github.deutschebank.symphony.workflow.content.Room;
import com.github.deutschebank.symphony.workflow.content.User;
import com.github.deutschebank.symphony.workflow.content.UserDef;
import com.github.deutschebank.symphony.workflow.java.workflow.ClassBasedWorkflow;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.model.MemberInfo;
import com.symphony.api.model.MembershipList;
import com.symphony.api.model.UserV2;
import com.symphony.api.pod.RoomMembershipApi;
import com.symphony.api.pod.UsersApi;

import example.symphony.demoworkflow.expenses.Claim;

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

	public interface MemberQueryWorkflow extends Workflow {

		public List<User> getMembersInRoom(Room r);

		public boolean isMe(User u);

	}

	public class MemberQueryClassBasedWorkflow extends ClassBasedWorkflow implements MemberQueryWorkflow {

		public MemberQueryClassBasedWorkflow(String namespace) {
			super(namespace);
		}

		@Override
		public List<User> getMembersInRoom(Room r) {
			MembershipList ml = membershipApi.v1RoomIdMembershipListGet(r.getId(), null);
			return ml.stream()
				.map(m -> createUser(m))
				.collect(Collectors.toList());
		}

		private User createUser(MemberInfo m) {
			UserV2 symphonyUser = usersApi.v2UserGet(null, m.getId(), null, null, true);
			return new UserDef("" + symphonyUser.getId(), symphonyUser.getDisplayName(),
					symphonyUser.getEmailAddress());
		}

		@Override
		public boolean isMe(User u) {
			return u.getAddress().equals(botIdentity.getEmail());
		}
	}

	@Bean
	public Workflow appWorkflow() {
		MemberQueryClassBasedWorkflow wf = new MemberQueryClassBasedWorkflow(WorkflowConfig.class.getCanonicalName());
		wf.addClass(Claim.class);
		return wf;
	}
}
