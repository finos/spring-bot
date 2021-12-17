package org.finos.springbot.teams.history;

import java.util.Map;

import org.finos.springbot.workflow.response.Response;
import org.finos.springbot.workflow.response.WorkResponse;
import org.finos.springbot.workflow.response.handlers.ResponseHandler;

/**
 * This class is responsible for generating a unique storage ID number and adding it 
 * to the {@link WorkResponse}.  
 * 
 * This can then be used in parameter replacement (added to forms) and also used to
 * tell {@link TeamsHistory} where to store data.
 * 
 * @author rob@kite9.com
 *
 */
public class StorageIDResponseHandler implements ResponseHandler {
	
	public static final String STORAGE_ID_KEY = "storageId";
	
	private final TeamsHistory th;
	
	public StorageIDResponseHandler(TeamsHistory th) {
		this.th = th;
	}

	@Override
	public void accept(Response t) {
		if (t instanceof WorkResponse) {
			Map<String, Object> data = ((WorkResponse) t).getData();
			if ((data != null) && (!data.containsKey(STORAGE_ID_KEY))) {
				data.put(STORAGE_ID_KEY, th.createStorageId());
			}
		}
	}

	@Override
	public int getOrder() {
		return ResponseHandler.HIGHEST_PRECEDENCE;
	}

}
