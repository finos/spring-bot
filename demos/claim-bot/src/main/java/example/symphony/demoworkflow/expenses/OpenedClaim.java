package example.symphony.demoworkflow.expenses;

import javax.validation.constraints.Min;

import org.finos.springbot.workflow.actions.Action;
import org.finos.springbot.workflow.annotations.Display;
import org.finos.springbot.workflow.annotations.Work;
import org.finos.springbot.workflow.content.User;

@Work
public class OpenedClaim {
	
	enum Status { OPEN, APPROVED, PAID };
	
	User author = Action.CURRENT_ACTION.get().getUser();

	User approvedBy;
	
	User paidBy;

	@Display(name = "Claim Status")
	Status status = Status.OPEN;
	
	String description;

	@Min(0)
	Number amount;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Number getAmount() {
		return amount;
	}

	public void setAmount(Number amount) {
		this.amount = amount;
	}
	
	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
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
