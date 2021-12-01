package org.finos.springbot.teams.conversations;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.finos.springbot.teams.TeamsException;
import org.finos.springbot.teams.content.TeamsConversation;
import org.finos.springbot.teams.content.TeamsAddressable;
import org.finos.springbot.teams.content.TeamsChat;
import org.finos.springbot.teams.content.TeamsUser;
import org.finos.springbot.teams.turns.CurrentTurnContext;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.content.Chat;
import org.finos.springbot.workflow.content.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.bot.builder.BotFrameworkAdapter;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.builder.TurnContextImpl;
import com.microsoft.bot.builder.teams.TeamsInfo;
import com.microsoft.bot.connector.ConnectorClient;
import com.microsoft.bot.connector.Conversations;
import com.microsoft.bot.schema.Activity;
import com.microsoft.bot.schema.ChannelAccount;
import com.microsoft.bot.schema.ConversationAccount;
import com.microsoft.bot.schema.ConversationsResult;
import com.microsoft.bot.schema.RoleTypes;
import com.microsoft.bot.schema.teams.TeamDetails;
import com.microsoft.bot.schema.teams.TeamsChannelData;

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
					.map(c -> new TeamsChat(c.getId(), ""))
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
	public TeamsAddressable getTeamsChat(TurnContext tc) {
		ConversationAccount tcd = tc.getActivity().getConversation();
		if ("groupChat".equals(tcd.getConversationType())) {
			return new TeamsChat(tcd.getId(), "Group Chat");
		} else if ("channel".equals(tcd.getConversationType())){
			try {
				TeamDetails td = TeamsInfo.getTeamDetails(tc, tc.getActivity().teamsGetTeamId()).get();
				return new TeamsConversation(td.getAadGroupId(), tcd.getId(), tcd.getName());
			} catch (Exception e) {
				throw new TeamsException("Couldn't identify channel details", e);
			}
		} else {
			return null;
		}
	}

	@Override
	public User getUser(ChannelAccount from) {
		return new TeamsUser(from.getId(), from.getName(), from.getAadObjectId());
	}

}
