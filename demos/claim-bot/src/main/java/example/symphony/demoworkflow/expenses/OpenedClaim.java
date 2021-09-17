package example.symphony.demoworkflow.expenses;

import org.finos.symphony.toolkit.workflow.actions.Action;
import org.finos.symphony.toolkit.workflow.annotations.Work;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.annotations.Display;

@Work
public class OpenedClaim extends NewClaim {
	
	enum Status { OPEN, APPROVED, PAID };
	
	User author = Action.CURRENT_ACTION.get().getUser();

	User approvedBy;
	
	User paidBy;

	@Display(name = "Claim Status")
	Status status = Status.OPEN;


	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public Number getAmount() {
		return amount;
	}

	public void setApprovedBy(User approvedBy) {
		this.approvedBy = approvedBy;
	}

	public User getPaidBy() {
		return paidBy;
	}

	public void setPaidBy(User paidBy) {
		this.paidBy = paidBy;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
}
