package org.finos.springbot.example.demo.controller;

import org.finos.springbot.workflow.annotations.Work;

@Work
public class Ticket {

	int number;
	
	String text;
	

	public Ticket() {
		super();
	}
	
	public Ticket(int number, String text) {
		super();
		this.number = number;
		this.text = text;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	
}
