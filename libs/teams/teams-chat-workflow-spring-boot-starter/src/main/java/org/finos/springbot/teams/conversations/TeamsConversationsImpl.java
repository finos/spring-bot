package org.finos.springbot.teams.conversations;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.finos.springbot.teams.TeamsException;
import org.finos.springbot.teams.content.TeamsAddressable;
import org.finos.springbot.teams.content.TeamsChannel;
import org.finos.springbot.teams.content.TeamsChat;
import org.finos.springbot.teams.content.TeamsMultiwayChat;
import org.finos.springbot.teams.content.TeamsUser;
import org.finos.springbot.teams.turns.CurrentTurnContext;
import org.finos.springbot.workflow.actions.Action;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.content.Chat;
import org.finos.springbot.workflow.content.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.bot.builder.BotFrameworkAdapter;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.builder.teams.TeamsInfo;
import com.microsoft.bot.connector.ConnectorClient;
import com.microsoft.bot.connector.Conversations;
import com.microsoft.bot.schema.ChannelAccount;
import com.microsoft.bot.schema.ConversationAccount;

public class TeamsConversationsImpl implements TeamsConversations {
	
	private static final Logger LOG = LoggerFactory.getLogger(TeamsConversationsImpl.class);

	public TeamsConversationsImpl() {
		super();
	}

	private Conversations getConversations() {
		TurnContext ctx = CurrentTurnContext.CURRENT_CONTEXT.get();
		ConnectorClient connectorClient = ctx.getTurnState().get(BotFrameworkAdapter.CONNECTOR_CLIENT_KEY);
		return connectorClient.getConversations();
	}
	
	
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
		try {
			return getConversations().getConversations().get().getConversations().stream()
					.map(c -> new TeamsMultiwayChat(c.getId(), ""))
					.collect(Collectors.toSet());
		} catch (Exception e) {
			throw new TeamsException("Couldn't do getAllAddressables", e);
		}
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
		try {
			return getConversations().getConversationMembers(r.getKey()).get().stream()
				.map(cm -> new TeamsUser(cm.getId(), cm.getName(), cm.getAadObjectId()))
				.collect(Collectors.toList());
		} catch (Exception e) {
			throw new TeamsException("Couldn't do getChatMembers", e);
		}
	}

	@Override
	public List<TeamsUser> getChatAdmins(TeamsChat r) {
		try {
			return getConversations().getConversationMembers(r.getKey()).get().stream()
				.map(cm -> new TeamsUser(cm.getId(), cm.getName(), cm.getAadObjectId()))
				.collect(Collectors.toList());
		} catch (Exception e) {
			throw new TeamsException("Couldn't do getChatAdmins", e);
		}
	}

	@Override
	public TeamsAddressable getTeamsAddressable(TurnContext tc) {
		ConversationAccount tcd = tc.getActivity().getConversation();
		if ("groupChat".equals(tcd.getConversationType())) {
			return new TeamsMultiwayChat(tcd.getId(), "Group Chat");
		} else if ("channel".equals(tcd.getConversationType())){
			return new TeamsChannel(tcd.getId(), tcd.getName());
		} else {
			// one-to-one chat.
			return null;
		}
	}

	@Override
	public TeamsAddressable getAddressable(ChannelAccount from) {
		if (isChannel(from)) {
			return new TeamsChannel(from.getId(), from.getName());
		} else {
			return new TeamsUser(from.getId(), from.getName(), from.getAadObjectId());
		}
	}

	@Override
	public List<TeamsChannel> getTeamsChannels(TurnContext tc) {
		try {
			return TeamsInfo.getTeamChannels(tc, null).get().stream()
				.map(ci -> new TeamsChannel(ci.getId(), ci.getName()))
				.collect(Collectors.toList());
		} catch (Exception e) {
			throw new TeamsException("Couldn't get channel list ", e);
		}
	}

	@Override
	public boolean isChannel(ChannelAccount ca) {
		TurnContext tc = CurrentTurnContext.CURRENT_CONTEXT.get();
		List<TeamsChannel> channels = getTeamsChannels(tc);
		return channels.stream()
			.filter(x -> x.getKey().equals(ca.getId()))
			.findFirst()
			.isPresent();
	}

	@Override
	public TeamsUser getUser(ChannelAccount from) {
		return new TeamsUser(from.getId(), from.getName(), from.getAadObjectId());
	}

	@Override
	public TeamsUser lookupUser(String userId) {
		try {
			TurnContext tc = CurrentTurnContext.CURRENT_CONTEXT.get();
			TeamsAddressable ta = getTeamsAddressable(tc);
			return getUser(getConversations().getConversationMember(userId, ta.getKey()).get());
		} catch (Exception e) {
			throw new TeamsException("Couldn't lookup user", e);
		}
	}
	
	

}
