package org.finos.symphony.toolkit.workflow.sources.symphony.content;

import org.finos.symphony.toolkit.workflow.content.Tag;
import org.finos.symphony.toolkit.workflow.content.Tag.Type;

public abstract class TagDef implements Tag {

	protected String name;
	protected String id;
	protected Type tagType;

	public TagDef() {
	}

	public TagDef(String id, String name, Type t) {
		super();
		this.id = id;
		this.name = name;
		this.tagType = t;
	}

	@Override
	public String getName() {
		return name;
	}


	@Override
	public String getId() {
		return id;
	}
	
	@Override
	public Type getTagType() {
		return tagType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((tagType == null) ? 0 : tagType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TagDef other = (TagDef) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (tagType != other.tagType)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "TagDef [name=" + getName() + ", id=" + getId() + ", type=" + getTagType() + "]";
	}

	@Override
	public String getText() {
		return (tagType == null ? "" : tagType.getSymbol()) + name;
	}

	
}
