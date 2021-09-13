package org.finos.symphony.toolkit.workflow.actions.consumers;

import org.finos.symphony.toolkit.workflow.actions.Action;

public interface AddressingChecker {

	public Action filter(Action a);
}
