package org.finos.springbot.teams.handlers.retry;

import java.time.LocalDateTime;

import org.finos.springbot.teams.content.TeamsAddressable;
import org.finos.springbot.teams.content.TeamsChannel;
import org.finos.springbot.teams.content.TeamsUser;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.microsoft.bot.schema.Activity;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageRetry {

	private Activity activity;
	private String teamsAddressableId;
	private int retryCount;
	private LocalDateTime retryAfterTime;
	private Class<? extends TeamsAddressable> teamsAddressableClass;

	public MessageRetry() {

	}

	public MessageRetry(Activity activity, TeamsAddressable teamsAddressable, int retryCount, LocalDateTime retryAfterTime) {
		this.activity = activity;
		this.teamsAddressableId = teamsAddressable.getKey();
		this.retryCount = retryCount;
		this.retryAfterTime = retryAfterTime;
		this.teamsAddressableClass = teamsAddressable.getClass();
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	public LocalDateTime getRetryAfterTime() {
		return retryAfterTime;
	}

	public void setRetryAfterTime(LocalDateTime retryAfterTime) {
		this.retryAfterTime = retryAfterTime;
	}

	public String getTeamsAddressableId() {
		return teamsAddressableId;
	}

	public void setTeamsAddressableId(String teamsAddressableId) {
		this.teamsAddressableId = teamsAddressableId;
	}
	
	public Class<? extends TeamsAddressable> getTeamsAddressableClass() {
		return teamsAddressableClass;
	}

	public void setTeamsAddressableClass(Class<TeamsAddressable> teamsAddressableClass) {
		this.teamsAddressableClass = teamsAddressableClass;
	}

	public TeamsAddressable getTeamsAddressable()  {
		try {
		TeamsAddressable instance = teamsAddressableClass.newInstance();
		
		if (instance instanceof TeamsUser || instance instanceof TeamsChannel) {
			((TeamsChannel)instance).setKey(teamsAddressableId);			
		}
		
		return instance;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Can't create TeamsAddressable", e);
		}
	}
	
	@Override
	public String toString() {
		return "MessageRetry [activity=" + activity + ", teamsAddressableId=" + teamsAddressableId + ", retryCount="
				+ retryCount + ", retryAfterTime=" + retryAfterTime + "]";
	}

}