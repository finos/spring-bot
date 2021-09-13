package org.finos.symphony.toolkit.workflow.sources.symphony.handlers;

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
