package org.finos.springbot.workflow.data;

import org.finos.springbot.workflow.response.DataResponse;

/**
 * Formats data for wire transfer
 * 
 * @author rob@kite9.com
 *
 */
public interface DataHandler {

	public String formatData(DataResponse dr);
}
