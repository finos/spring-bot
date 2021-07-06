package org.finos.symphony.toolkit.workflow.sources.symphony.content;

import org.finos.symphony.toolkit.workflow.content.Tag.Type;

public class CashTagDef extends TagDef implements CashTag {
		
	public CashTagDef() {
		super();
	}

	public CashTagDef(String id) {
		super(id, id, Type.CASH);
	}

}
