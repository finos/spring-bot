package org.finos.symphony.toolkit.workflow.sources.symphony.content;

import java.util.UUID;

/**
 * This is a special class of tag which is a globally unique ID for the workflow element,
 * which can be used to track the evolution of a workflow.
 * 
 * @author Rob Moffat
 *
 */
public final class ID extends TagDef {

	public ID() {
		this(UUID.randomUUID());
	}

	public ID(UUID uuid) {
		super(uuid.toString(), uuid.toString(), Type.HASH);
	}
}
