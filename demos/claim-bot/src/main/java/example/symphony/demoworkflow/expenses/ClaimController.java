package example.symphony.demoworkflow.expenses;

import java.util.Arrays;
import java.util.List;

import org.finos.symphony.toolkit.workflow.annotations.ChatButton;
import org.finos.symphony.toolkit.workflow.annotations.ChatRequest;
import org.finos.symphony.toolkit.workflow.annotations.ChatResponseBody;
import org.finos.symphony.toolkit.workflow.annotations.WorkMode;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.Chat;
import org.finos.symphony.toolkit.workflow.content.Message;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.conversations.Conversations;
import org.finos.symphony.toolkit.workflow.response.MessageResponse;
import org.finos.symphony.toolkit.workflow.response.Response;
import org.finos.symphony.toolkit.workflow.response.WorkResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import example.symphony.demoworkflow.expenses.OpenedClaim.Status;

@Controller
public class ClaimController {


	@ChatRequest(value = "open", description="Begin New Expense Claim")
	@ChatResponseBody(workMode = WorkMode.EDIT)
	public NewClaim open(Addressable a) {
		return new NewClaim();
	}
	
	@Autowired
	Conversations conversations;
	
	@ChatButton(value = NewClaim.class,  buttonText = "add")
	public List<Response> add(NewClaim sc, User u, Addressable from) {
		OpenedClaim c =  new OpenedClaim();
		c.amount = sc.amount;
		c.author = u;
		c.description = sc.description;
		c.status = Status.OPEN;
		
		Chat approvalRoom = conversations.getExistingChat("Claim Approval Room");
		
		return 
			Arrays.asList(
				new WorkResponse(approvalRoom, c, WorkMode.VIEW),
				new MessageResponse(from,
					Message.of("Your claim has been sent to the Approval Room for processing")));

	}

	@ChatButton(value=OpenedClaim.class, buttonText = "Approve", rooms={"Claim Approval Room"})
	public OpenedClaim approve(OpenedClaim c, User currentUser) {
		if (c.status == Status.OPEN) {
			c.approvedBy = currentUser;
			c.status = Status.APPROVED;
			return c;
		} else {
			throw new RuntimeException("Claim should be in OPEN mode");
		}
	}
	
}
