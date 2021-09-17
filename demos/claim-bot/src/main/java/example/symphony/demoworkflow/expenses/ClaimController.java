package example.symphony.demoworkflow.expenses;

import org.finos.symphony.toolkit.workflow.annotations.ChatButton;
import org.finos.symphony.toolkit.workflow.annotations.ChatRequest;
import org.finos.symphony.toolkit.workflow.annotations.ChatResponseBody;
import org.finos.symphony.toolkit.workflow.annotations.WorkMode;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.User;
import org.springframework.stereotype.Controller;

import example.symphony.demoworkflow.expenses.OpenedClaim.Status;

@Controller
public class ClaimController {


	@ChatRequest(value = "open", description="Begin New Expense Claim")
	@ChatResponseBody(workMode = WorkMode.EDIT)
	public NewClaim open(Addressable a) {
		return new NewClaim();
	}
	
	@ChatButton(value = NewClaim.class,  buttonText = "add")
	public OpenedClaim add(NewClaim sc, User u) {
		OpenedClaim c =  new OpenedClaim();
		c.amount = sc.amount;
		c.author = u;
		c.description = sc.description;
		c.status = Status.OPEN;
		return c;
	}

	@ChatRequest(value="approve", description = "Approve Latest Claim")
	public OpenedClaim approve(OpenedClaim c, User currentUser) {
		if (c.status == Status.OPEN) {
			c.approvedBy = currentUser;
			c.status = Status.APPROVED;
		}
		return c;
	}
	
}
