package org.finos.springbot.symphony.content;

import org.finos.springbot.workflow.annotations.Work;
import org.symphonyoss.TaxonomyElement;

@Work(jsonTypeName = {"org.finos.symphony.toolkit.workflow.sources.symphony.content.roomName"}, index = false)
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
