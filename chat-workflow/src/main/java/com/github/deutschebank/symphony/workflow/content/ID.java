package com.github.deutschebank.symphony.workflow.content;

import java.util.UUID;

/**
 * This is a special class of tag which is a globally unique ID for the workflow element,
 * which can be used to track the evolution of a workflow.
 * 
 * @author Rob Moffat
 *
 */
public class ID extends TagDef {

	public ID() {
		this(UUID.randomUUID());
	}

	public ID(UUID uuid) {
		super(uuid.toString(), uuid.toString(), Type.HASH);
	}
}
