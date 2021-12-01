package org.finos.springbot.workflow.data;

import org.finos.springbot.workflow.response.DataResponse;

/**
 * Formats data for wire transfer to a backing store.
 * 
 * @author rob@kite9.com
 *
 */
public interface DataHandler {

	public String formatData(DataResponse dr);
}
