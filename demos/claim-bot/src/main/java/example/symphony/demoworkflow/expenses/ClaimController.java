package example.symphony.demoworkflow.expenses;

import org.finos.symphony.toolkit.workflow.annotations.Exposed;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.response.FormResponse;
import org.springframework.stereotype.Controller;

import example.symphony.demoworkflow.expenses.Claim.Status;

@Controller
public class ClaimController {


	@Exposed(value = "open", description="Begin New Expense Claim")
	public FormResponse open(Addressable a) {
		return new FormResponse(a, new StartClaim(), true);
	}
	
	@Exposed(value = "add", description="Submit Expense Claim", formClass = StartClaim.class)
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
	
	@Exposed(value="new", description = "New Full Expense Form") 
	public FormResponse full(Addressable room) {
		return new FormResponse(room, new Claim(), true);
	}
	
	
}
