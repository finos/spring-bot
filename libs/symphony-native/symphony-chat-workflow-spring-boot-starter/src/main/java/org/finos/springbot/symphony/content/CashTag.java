package org.finos.springbot.symphony.content;

import java.util.Collections;
import java.util.List;

import org.finos.springbot.workflow.annotations.Work;
import org.finos.springbot.workflow.content.Tag;
import org.symphonyoss.TaxonomyElement;
import org.symphonyoss.fin.Security;
import org.symphonyoss.fin.security.id.Ticker;

@Work(jsonTypeName = "org.symphonyoss.fin.security", index = false, readVersions = {"0.*"} )
public final class CashTag extends Security implements Tag {
	
	public CashTag() {
		super();
	}
	
	public CashTag(String id) {
		this(Collections.singletonList(new Ticker(id)));
	}

	public CashTag(List<TaxonomyElement> id) {
		super(id);
	}

	@Override
	public Type getTagType() {
		return CASH;
	}

	@Override
	public String getName() {
		TaxonomyElement firstId = getId().get(0);
		return firstId.getValue();
	}
	
}
