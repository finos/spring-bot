package org.finos.symphony.toolkit.workflow.actions;

import java.util.function.Consumer;

/**
 * Implement this interface to deal with incoming actions into the workflow, and send responses.  
 * 
 * @author Rob Moffat
 */
public interface ActionConsumer extends Consumer<Action> {

}
