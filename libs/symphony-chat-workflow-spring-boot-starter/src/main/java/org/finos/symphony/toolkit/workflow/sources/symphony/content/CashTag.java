package org.finos.symphony.toolkit.workflow.sources.symphony.content;

import org.finos.symphony.toolkit.workflow.content.Tag;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = CashTagDef.class)
public interface CashTag extends Tag, SymphonyContent {

}
