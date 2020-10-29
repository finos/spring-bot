package org.finos.symphony.toolkit.workflow.content;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = CashTagDef.class)
public interface CashTag extends Tag {

}
