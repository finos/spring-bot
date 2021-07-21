package org.finos.symphony.toolkit.workflow.sources.symphony.content;

import java.util.UUID;

import org.finos.symphony.toolkit.workflow.content.Tag;
import org.symphonyoss.taxonomy.Hashtag;

public class HashTag extends Hashtag implements Tag {
		
	public HashTag() {
		super();
	}

	public HashTag(String id) {
		super(id);
	}

	@Override
	public Type getTagType() {
		return HASH;
	}

	@Override
	public String getName() {
		if (getValue().startsWith(""+getTagType().getPrefix())) {
			return getValue().substring(1);
		} else {
			return getValue();
		}
	}

	public static final HashTag createID() {
		return new HashTag(UUID.randomUUID().toString());
	}
}
