package org.finos.symphony.toolkit.workflow.fixture;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

import org.finos.symphony.toolkit.workflow.annotations.Exposed;
import org.finos.symphony.toolkit.workflow.annotations.Work;

@Work(name="Test Object", instructions="blah")
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (askAxed ? 1231 : 1237);
		result = prime * result + ((askQty == null) ? 0 : askQty.hashCode());
		result = prime * result + (bidAxed ? 1231 : 1237);
		result = prime * result + ((bidQty == null) ? 0 : bidQty.hashCode());
		result = prime * result + ((creator == null) ? 0 : creator.hashCode());
		result = prime * result + ((isin == null) ? 0 : isin.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TestObject other = (TestObject) obj;
		if (askAxed != other.askAxed)
			return false;
		if (askQty == null) {
			if (other.askQty != null)
				return false;
		} else if (!askQty.equals(other.askQty))
			return false;
		if (bidAxed != other.bidAxed)
			return false;
		if (bidQty == null) {
			if (other.bidQty != null)
				return false;
		} else if (!bidQty.equals(other.bidQty))
			return false;
		if (creator == null) {
			if (other.creator != null)
				return false;
		} else if (!creator.equals(other.creator))
			return false;
		if (isin == null) {
			if (other.isin != null)
				return false;
		} else if (!isin.equals(other.isin))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TestObject [isin=" + isin + ", bidAxed=" + bidAxed + ", askAxed=" + askAxed + ", creator=" + creator
				+ ", bidQty=" + bidQty + ", askQty=" + askQty + "]";
	}

	
}
