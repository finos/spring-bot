package com.symphony.user;

import org.symphonyoss.TaxonomyElement;

public class UserId extends TaxonomyElement {

	public UserId() {
		super();
	}

	public UserId(String value) {
		super(value);
	}

	@Override
	public String getSymbolPrefix() {
		return "@";
	}

}
