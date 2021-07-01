package org.finos.symphony.toolkit.workflow.sources.symphony.elements;

import java.util.List;
import java.util.function.Function;

import org.finos.symphony.toolkit.workflow.actions.ElementsAction;
import org.finos.symphony.toolkit.workflow.response.Response;

/**
 * Deals with symphony elements, to start workflows etc.  
 * First argument is the stream, second is the converted contents of the elements action.
 * @author Rob Moffat
 *
 */
public interface ElementsConsumer extends Function<ElementsAction, List<Response>> {

}
