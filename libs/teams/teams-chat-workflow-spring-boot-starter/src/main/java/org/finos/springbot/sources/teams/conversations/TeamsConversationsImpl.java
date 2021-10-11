package org.finos.springbot.sources.teams.conversations;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.finos.springbot.sources.teams.content.TeamsChat;
import org.finos.springbot.sources.teams.content.TeamsUser;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.Chat;
import org.finos.symphony.toolkit.workflow.content.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.microsoft.bot.schema.teams.TeamsChannelData;

public class TeamsConversationsImpl implements TeamsConversations, InitializingBean {
	
	private static final Logger LOG = LoggerFactory.getLogger(TeamsConversationsImpl.class);

	@Override
	public boolean isSupported(Chat r) {
		return r instanceof TeamsChat;
	}

	@Override
	public boolean isSupported(User u) {
		return u instanceof TeamsUser;
	}

	@Override
	public Set<Addressable> getAllAddressables() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<TeamsChat> getAllChats() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TeamsChat getExistingChat(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TeamsChat ensureChat(TeamsChat r, List<TeamsUser> users, Map<String, Object> meta) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TeamsUser> getChatMembers(TeamsChat r) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TeamsUser> getChatAdmins(TeamsChat r) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Addressable getTeamsChat(TeamsChannelData tcd) {
		// TODO Auto-generated method stub
		return null;
	}

}
