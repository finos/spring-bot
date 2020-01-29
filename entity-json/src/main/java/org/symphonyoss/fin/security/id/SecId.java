package org.symphonyoss.fin.security.id;

import org.symphonyoss.TaxonomyElement;

public abstract class SecId extends TaxonomyElement {

	public SecId() {
		super();
	}

	public SecId(String value) {
		super(value);
	}

	@Override
	public String getSymbolPrefix() {
		return "$";
	}

	
}
