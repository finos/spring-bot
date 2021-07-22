package example.symphony.demoworkflow.expenses;

import org.finos.symphony.toolkit.workflow.annotations.Exposed;
import org.finos.symphony.toolkit.workflow.annotations.WorkMode;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.response.WorkResponse;
import org.springframework.stereotype.Controller;

import example.symphony.demoworkflow.expenses.Claim.Status;

@Controller
public class ClaimController {


	@Exposed(value = "open", description="Begin New Expense Claim")
	public WorkResponse open(Addressable a) {
		return new WorkResponse(a, new StartClaim(), WorkMode.EDIT);
	}
	
	@Exposed(value = "add", description="Submit Expense Claim", formClass = StartClaim.class, isButton = WorkMode.EDIT)
	public Claim add(StartClaim sc, User u) {
		Claim c =  new Claim();
		c.amount = sc.amount;
		c.author = u;
		c.description = sc.description;
		c.status = Status.OPEN;
		return c;
	}

	@Exposed(formClass = Claim.class, value="approve", description = "Approve Claim")
	public Claim approve(Claim c, User currentUser) {
		if (c.status == Status.OPEN) {
			c.approvedBy = currentUser;
			c.status = Status.APPROVED;
		}
		return c;
	}
	
}
