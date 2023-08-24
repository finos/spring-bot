package org.finos.springbot.teams.history;

import java.util.Map;

import org.finos.springbot.teams.state.TeamsStateStorage;
import org.finos.springbot.workflow.annotations.Work;
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
public class StorageIDResponseHandler implements ResponseHandler<String> {
	
	public static final String STORAGE_ID_KEY = "storageId";
	
	private final TeamsStateStorage th;
	
	public StorageIDResponseHandler(TeamsStateStorage th) {
		this.th = th;
	}

	@Override
	public String apply(Response t) {
		if (t instanceof WorkResponse) {
			Map<String, Object> data = ((WorkResponse) t).getData();
			if ((data != null) && (!data.containsKey(STORAGE_ID_KEY)) && (needsStoring(data))) {
				String storageId = th.createStorageId();
				data.put(STORAGE_ID_KEY, storageId);
				return storageId;
			}
		}
		
		return null;
	}

	private boolean needsStoring(Map<String, Object> data) {
		for(Object o2: data.values()) {
			Work w = o2 != null ? o2.getClass().getAnnotation(Work.class) : null;
			if ((w != null) && (w.index())) {
				return true;
			}	
		}
		
		return false;
	}

	@Override
	public int getOrder() {
		return ResponseHandler.HIGHEST_PRECEDENCE;
	}

}
