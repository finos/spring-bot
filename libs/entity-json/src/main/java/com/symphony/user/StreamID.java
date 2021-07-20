package com.symphony.user;

import org.symphonyoss.TaxonomyElement;

public class StreamID extends TaxonomyElement {

	public StreamID() {
		super();
	}

	public StreamID(String value) {
		super(value);
	}

	@Override
	public String getSymbolPrefix() {
		return "@";
	}

}
