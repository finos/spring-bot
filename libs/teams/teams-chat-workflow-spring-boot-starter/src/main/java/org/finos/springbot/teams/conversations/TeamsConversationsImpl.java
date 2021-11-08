package org.finos.springbot.teams.conversations;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.finos.springbot.teams.TeamsException;
import org.finos.springbot.teams.content.TeamsChat;
import org.finos.springbot.teams.content.TeamsUser;
import org.finos.springbot.teams.turns.CurrentTurnContext;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.content.Chat;
import org.finos.springbot.workflow.content.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import com.microsoft.bot.builder.BotFrameworkAdapter;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.builder.TurnContextImpl;
import com.microsoft.bot.connector.ConnectorClient;
import com.microsoft.bot.connector.rest.RestConnectorClient;
import com.microsoft.bot.schema.ChannelAccount;
import com.microsoft.bot.schema.ConversationAccount;
import com.microsoft.bot.schema.ConversationsResult;

public class TeamsConversationsImpl implements TeamsConversations, InitializingBean {
	
	private static final Logger LOG = LoggerFactory.getLogger(TeamsConversationsImpl.class);
	
	private BotFrameworkAdapter bfa;

	public TeamsConversationsImpl(BotFrameworkAdapter bfa, RestConnectorClient rcc) {
		super();
		this.bfa = bfa;
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
			TurnContext turnContext = CurrentTurnContext.CURRENT_CONTEXT.get();
			CompletableFuture<ConversationsResult> convos = bfa.getConversations((TurnContextImpl) turnContext);
			return convos.get().getConversations().stream()
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
			TurnContext ctx = CurrentTurnContext.CURRENT_CONTEXT.get();
			ConnectorClient connectorClient = ctx.getTurnState().get(BotFrameworkAdapter.CONNECTOR_CLIENT_KEY);
			return connectorClient.getConversations().getConversationMembers(r.getKey()).get().stream()
				.map(cm -> new TeamsUser(cm.getId(), cm.getName()))
				.collect(Collectors.toList());
		} catch (Exception e) {
			throw new TeamsException("Couldn't do getChatMembers", e);
		}
	}

	@Override
	public List<TeamsUser> getChatAdmins(TeamsChat r) {
		return null;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
	//	rcc = new RestConnectorClient(null)	
	}

	@Override
	public Addressable getTeamsChat(ConversationAccount tcd) {
		return new TeamsChat(tcd.getId(), tcd.getName());
	}

	@Override
	public User getUser(ChannelAccount from) {
		return new TeamsUser(from.getId(), from.getName());
	}

}
