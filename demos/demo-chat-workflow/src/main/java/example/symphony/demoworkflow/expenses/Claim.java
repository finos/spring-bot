package example.symphony.demoworkflow.expenses;

import com.github.deutschebank.symphony.workflow.content.Author;
import com.github.deutschebank.symphony.workflow.content.User;
import com.github.deutschebank.symphony.workflow.java.Exposed;
import com.github.deutschebank.symphony.workflow.java.Work;

@Work(editable = true, instructions = "Sales Expense Claim Form", name = "Expense Claim")
public class Claim {
	
	enum Status { OPEN, APPROVED, PAID };
	
	String description;
	
	Author author;
	
	Float amount;
	
	User approvedBy;
	
	User paidBy;
	
	Status status;
	
	@Exposed
	public static Claim open() {
		Claim c = new Claim();
		return c;
	}

	@Exposed
	public Claim approve() {
		return this;
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

	public Float getAmount() {
		return amount;
	}

	public void setAmount(Float amount) {
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
