package com.github.deutschebank.symphony.workflow.content;

public class CashTagDef extends TagDef implements CashTag {
		
	public CashTagDef() {
		super();
	}

	public CashTagDef(String id) {
		super(id, id, Type.CASH);
	}

}
