package org.finos.springbot.sources.teams.handlers;

import org.finos.symphony.toolkit.workflow.response.DataResponse;

/**
 * Formats data for wire transfer
 * 
 * @author rob@kite9.com
 *
 */
public interface DataHandler {

	public String formatData(DataResponse dr);
}
