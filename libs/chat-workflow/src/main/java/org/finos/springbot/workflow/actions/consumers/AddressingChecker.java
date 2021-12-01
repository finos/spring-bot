package org.finos.springbot.workflow.actions.consumers;

import org.finos.springbot.workflow.actions.Action;

public interface AddressingChecker {

	public Action filter(Action a);
}
