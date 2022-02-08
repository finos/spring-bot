package org.finos.springbot.tests.controller;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

import org.finos.springbot.workflow.annotations.Work;

@Work()
public class TestObject {

	@Size(min=12)
	private String isin;
	private boolean bidAxed;
	private boolean askAxed;
	
	@Email
	private String creator;
	private Number bidQty;
	private Number askQty;
	
	public TestObject() {
		super();
	}

	public TestObject(String isin, boolean bidAxed, boolean askAxed, String creator, Number bidAmount, Number askAmount) {
		super();
		this.isin = isin;
		this.bidAxed = bidAxed;
		this.askAxed = askAxed;
		this.creator = creator;
		this.bidQty= bidAmount;
		this.askQty = askAmount;
	}

	public String getIsin() {
		return isin;
	}

	public void setIsin(String isin) {
		this.isin = isin;
	}

	public boolean isBidAxed() {
		return bidAxed;
	}

	public void setBidAxed(boolean bidAxed) {
		this.bidAxed = bidAxed;
	}

	public boolean isAskAxed() {
		return askAxed;
	}

	public void setAskAxed(boolean askAxed) {
		this.askAxed = askAxed;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Number getBidQty() {
		return bidQty;
	}

	public void setBidQty(Number bidQty) {
		this.bidQty = bidQty;
	}

	public Number getAskQty() {
		return askQty;
	}

	public void setAskQty(Number askQty) {
		this.askQty = askQty;
	}

	@Override
	public String toString() {
		return "TestObject [isin=" + isin + ", bidAxed=" + bidAxed + ", askAxed=" + askAxed + ", creator=" + creator
				+ ", bidQty=" + bidQty + ", askQty=" + askQty + "]";
	}

	
}
