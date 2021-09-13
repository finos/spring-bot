package org.finos.symphony.toolkit.workflow.response.handlers;

import java.util.function.Consumer;

import org.finos.symphony.toolkit.workflow.response.Response;

/**
 * Collects together all the response handlers defined in spring.
 * @author rob@kite9.com
 *
 */
public interface ResponseHandlers extends Consumer<Response> {

}
