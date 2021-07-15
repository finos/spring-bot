package org.finos.symphony.toolkit.workflow.sources.symphony.content;

import java.util.List;

import org.finos.symphony.toolkit.workflow.content.Tag;
import org.symphonyoss.TaxonomyElement;
import org.symphonyoss.fin.Security;

public class CashTag extends Security implements Tag {
	
	public CashTag() {
		super();
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
