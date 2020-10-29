package com.github.deutschebank.symphony.workflow.fixture;

import com.github.deutschebank.symphony.workflow.content.Author;
import com.github.deutschebank.symphony.workflow.content.User;
import com.github.deutschebank.symphony.workflow.java.Work;

@Work(editable = false, instructions = "Expenses Claim")
public class Claim {
	
	enum Status { OPEN, APPROVED, PAID };
	
	String description;
	
	Author author;
	
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

	public Author getAuthor() {
		return author;
	}

	public void setAuthor(Author author) {
		this.author = author;
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
