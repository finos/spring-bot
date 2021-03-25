package org.symphonyoss.taxonomy;

import org.symphonyoss.TaxonomyElement;

public class Hashtag extends TaxonomyElement {

	public Hashtag() {
		super();
	}

	public Hashtag(String value) {
		super(value);
	}

	@Override
	public String getSymbolPrefix() {
		return "#";
	}

}
