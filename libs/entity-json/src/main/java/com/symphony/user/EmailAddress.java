package com.symphony.user;

import org.symphonyoss.TaxonomyElement;

public class EmailAddress extends TaxonomyElement {

	public EmailAddress() {
		super();
	}

	public EmailAddress(String value) {
		super(value);
	}

	@Override
	public String getSymbolPrefix() {
		return "@";
	}

}
