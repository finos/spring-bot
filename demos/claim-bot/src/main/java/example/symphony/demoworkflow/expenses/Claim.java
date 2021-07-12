package example.symphony.demoworkflow.expenses;

import javax.validation.constraints.Min;

import org.finos.symphony.toolkit.workflow.actions.Action;
import org.finos.symphony.toolkit.workflow.annotations.Work;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.annotations.Display;

@Work()
public class Claim {
	
	enum Status { OPEN, APPROVED, PAID };

	@Display(name = "Description")
	String description;
	
	User author = Action.CURRENT_ACTION.get().getUser();

	@Display(name = "Amount")
	@Min(0)
	Number amount;

	@Display(name = "Approved By", visible = true)
	User approvedBy;
	
	User paidBy;

	@Display(name = "Claim Status")
	Status status = Status.OPEN;


	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public Number getAmount() {
		return amount;
	}

	public void setAmount(Number amount) {
		this.amount = amount;
	}

	public User getApprovedBy() {
		return approvedBy;
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
