package org.finos.symphony.toolkit.workflow.sources.symphony.content;

import org.symphonyoss.TaxonomyElement;

public class RoomName extends TaxonomyElement {

	public RoomName() {
		super();
	}

	public RoomName(String value) {
		super(value);
	}

	@Override
	public String getSymbolPrefix() {
		return "@";
	}

}
