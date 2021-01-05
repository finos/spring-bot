/**
 * 
 */
package example.symphony.demoworkflow;

import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.java.workflow.ClassBasedWorkflow;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import example.symphony.demoworkflow.expenses.Claim;
import example.symphony.demoworkflow.expenses.StartClaim;

/**
 * @author rupnsur
 *
 */
@Configuration
public class WorkflowConfig {

	@Bean
	public Workflow appWorkflow() {
		ClassBasedWorkflow wf = new ClassBasedWorkflow(this.getClass().getPackageName());
		wf.addClass(StartClaim.class);
		wf.addClass(Claim.class);
		return wf;
	}
}
