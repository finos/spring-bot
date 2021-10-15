package org.finos.symphony.toolkit.workflow.fixture;

import org.finos.springbot.workflow.annotations.Work;
import org.finos.springbot.workflow.content.User;

@Work()
public class Claim {
	
	enum Status { OPEN, APPROVED, PAID };
	
	String description;
		
	float amount;
	
	User approvedBy;
	
	User paidBy;
	
	Status status;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
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
