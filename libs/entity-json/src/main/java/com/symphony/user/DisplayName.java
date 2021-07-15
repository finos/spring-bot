package com.symphony.user;

import org.symphonyoss.TaxonomyElement;

public class DisplayName extends TaxonomyElement {

	public DisplayName() {
		super();
	}

	public DisplayName(String value) {
		super(value);
	}

	@Override
	public String getSymbolPrefix() {
		return "@";
	}

}
