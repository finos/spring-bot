package org.finos.springbot.symphony.content;

import java.util.UUID;

import org.finos.springbot.workflow.annotations.Work;
import org.finos.springbot.workflow.content.Tag;
import org.symphonyoss.taxonomy.Hashtag;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Work(jsonTypeName = "org.symphonyoss.taxonomy.hashtag", index = false)
public final class HashTag extends Hashtag implements Tag {
		
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

	@JsonIgnore
	@Override
	public String getName() {
		if (getValue().startsWith(""+getTagType().getPrefix())) {
			return getValue().substring(1);
		} else {
			return getValue();
		}
	}

	public static final HashTag createID() {
		return createID(UUID.randomUUID());
	}
	
	public static final HashTag createID(UUID id) {
		return new HashTag(id.toString());
	}
}
