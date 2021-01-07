package example.symphony.demoworkflow.expenses;

import javax.validation.constraints.Min;

import org.finos.symphony.toolkit.workflow.content.Author;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.java.Exposed;
import org.finos.symphony.toolkit.workflow.java.Work;

@Work(editable = true, instructions = "Sales Expense Claim Form", name = "Expense Claim")
public class Claim {
	
	enum Status { OPEN, APPROVED, PAID };
	
	String description;
	
	Author author = Author.CURRENT_AUTHOR.get();
	
	@Min(0)
	Number amount;
	
	User approvedBy;
	
	User paidBy;
	
	Status status = Status.OPEN;
	
	@Exposed(description="Begin New Expense Claim")
	public static Claim open(StartClaim c) {
		Claim out = new Claim();
		out.description = c.description;
		out.amount = c.amount;
		return out;
	}

	@Exposed(description = "Approve an expense claim")
	public Claim approve() {
		if (this.status == Status.OPEN) {
			this.approvedBy = Author.CURRENT_AUTHOR.get();
			this.status = Status.APPROVED;
		}
		return this;
	}
	
	@Exposed(description = "New Full Expense Form") 
	public static Claim full(Claim c) {
		return c;
	}
	
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
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
