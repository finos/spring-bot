package org.finos.springbot.workflow.response.handlers;

import java.util.function.Consumer;

import org.finos.springbot.workflow.response.Response;

/**
 * Collects together all the response handlers defined in spring.
 * @author rob@kite9.com
 *
 */
public interface ResponseHandlers extends Consumer<Response> {

}
