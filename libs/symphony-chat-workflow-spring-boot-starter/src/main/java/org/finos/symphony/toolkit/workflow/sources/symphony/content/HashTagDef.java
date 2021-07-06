package org.finos.symphony.toolkit.workflow.sources.symphony.content;

import org.finos.symphony.toolkit.workflow.content.HashTag;
import org.finos.symphony.toolkit.workflow.content.TagDef;
import org.finos.symphony.toolkit.workflow.content.Tag.Type;

public class HashTagDef extends TagDef implements HashTag {
		
	public HashTagDef() {
		super();
	}

	public HashTagDef(String id) {
		super(id, id, Type.HASH);
	}


}
