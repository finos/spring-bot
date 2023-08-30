package org.finos.springbot.teams.conversations;

import com.microsoft.bot.schema.ResourceResponse;

public class TeamsErrorResourceResponse extends ResourceResponse {

	Throwable e;
	
	public TeamsErrorResourceResponse(String id, Throwable e ) {
		super(id);
		this.e = e;
	}
	
	public Throwable getThrowable() {
		return e;
	}
}

