package org.finos.symphony.toolkit.workflow.actions.consumers;

import java.util.function.Consumer;

import org.finos.symphony.toolkit.workflow.actions.Action;

/**
 * Implement this interface to deal with incoming actions into the workflow, and send responses.  
 * 
 * @author Rob Moffat
 */
public interface ActionConsumer extends Consumer<Action> {

}
