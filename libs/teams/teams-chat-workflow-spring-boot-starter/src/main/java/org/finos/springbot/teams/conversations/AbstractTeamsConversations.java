package org.finos.springbot.teams.conversations;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.finos.springbot.teams.TeamsException;
import org.finos.springbot.teams.content.TeamsAddressable;
import org.finos.springbot.teams.content.TeamsChannel;
import org.finos.springbot.teams.content.TeamsChat;
import org.finos.springbot.teams.content.TeamsMultiwayChat;
import org.finos.springbot.teams.content.TeamsUser;
import org.finos.springbot.teams.turns.CurrentTurnContext;
import org.finos.springbot.workflow.content.Chat;
import org.finos.springbot.workflow.content.User;

import com.microsoft.bot.builder.BotFrameworkAdapter;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.builder.teams.TeamsInfo;
import com.microsoft.bot.connector.ConnectorClient;
import com.microsoft.bot.connector.Conversations;
import com.microsoft.bot.connector.authentication.MicrosoftAppCredentials;
import com.microsoft.bot.schema.Activity;
import com.microsoft.bot.schema.ChannelAccount;
import com.microsoft.bot.schema.ConversationAccount;
import com.microsoft.bot.schema.ConversationParameters;
import com.microsoft.bot.schema.ConversationReference;
import com.microsoft.bot.schema.ResourceResponse;

/**
 * Teams doesn't seem to support lookup of the list of conversations a bot is 
 * involved in, which makes it impossible to write getAllAddressables().
 * 
 * This is left as a problem for the subclass ;)
 * 
 * @author rob@kite9.com
 *
 */
public abstract class AbstractTeamsConversations implements TeamsConversations {
	
	private MicrosoftAppCredentials mac;
	private BotFrameworkAdapter bfa;
	private ChannelAccount botAccount;
	
	public AbstractTeamsConversations(BotFrameworkAdapter bfa, MicrosoftAppCredentials mac, ChannelAccount botAccount) {
		super();
		this.mac = mac;
		this.bfa = bfa;
		this.botAccount = botAccount;
	}

	private Conversations getConversations() {
		TurnContext ctx = getWorkingTurnContext(null);
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
		TurnContext tc = getWorkingTurnContext(null);
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
			TurnContext tc = getWorkingTurnContext(null);
			TeamsAddressable ta = getTeamsAddressable(tc.getActivity().getConversation());
			return getUser(getConversations().getConversationMember(userId, ta.getKey()).get());
		} catch (Exception e) {
			throw new TeamsException("Couldn't lookup user", e);
		}
	}

	protected String getOneToOneConversationId(TeamsUser tu) {
		try {
			ConversationParameters cp = new ConversationParameters();
			cp.setIsGroup(false);
			cp.setTenantId(mac.getChannelAuthTenant());
			cp.setMembers(Collections.singletonList(new ChannelAccount(tu.getKey())));
			
			return getConversations().createConversation(cp).get().getId();
		} catch (Exception e) {
			throw new TeamsException("Couldn't create one-to-one chat", e);
		}		
	}

	@Override
	public ConversationAccount getConversationAccount(TeamsAddressable address) {
		if (address instanceof TeamsUser) {
			String chatForUser = getOneToOneConversationId((TeamsUser) address);
			ConversationAccount ca = new ConversationAccount(chatForUser);
			ca.setTenantId(mac.getChannelAuthTenant());
			ca.setConversationType("personal");
			return ca;
		} else if (address instanceof TeamsChannel) {
			ConversationAccount ca = new ConversationAccount(address.getKey());
			ca.setTenantId(mac.getChannelAuthTenant());
			ca.setConversationType("channel");
			return ca;
		} else if (address instanceof TeamsMultiwayChat) {
			ConversationAccount ca = new ConversationAccount(address.getKey());
			ca.setTenantId(mac.getChannelAuthTenant());
			ca.setConversationType("groupChat");
			return ca;
		} else {
			return null;
		}
	}
	
	@Override
	public TeamsAddressable getTeamsAddressable(ConversationAccount tcd) {
		if ("groupChat".equals(tcd.getConversationType())) {
			return new TeamsMultiwayChat(tcd.getId(), "Group Chat");
		} else if ("channel".equals(tcd.getConversationType())){
			return new TeamsChannel(tcd.getId(), tcd.getName());
		} else if ("personal".equals(tcd.getConversationType())) {
			return new TeamsUser(tcd.getId(), tcd.getName(), tcd.getAadObjectId());
		} else {
			return null;
		}
	}
	
	

	private TurnContext getWorkingTurnContext(TeamsAddressable ta) {
		try {
			TurnContext out = CurrentTurnContext.CURRENT_CONTEXT.get();
			
			if (out != null) {
				return out;
			}
			
			TurnContext[] holder = new TurnContext[1];
			
			bfa.continueConversation(mac.getAppId(), createConversationReference(ta), tc -> {
				holder[0] = tc;
				return CompletableFuture.completedFuture(null);
			}).get();
			
			return holder[0];
		} catch (Exception e) {
			throw new TeamsException("Coulnd't create turn context", e);
		}
	}
	

	public CompletableFuture<ResourceResponse> handleActivity(Activity activity, TeamsAddressable to) {
		TurnContext ctx = getWorkingTurnContext(to);
		ensureRoomRecorded(to);
		return ctx.sendActivity(activity);
	}

	protected abstract void ensureRoomRecorded(TeamsAddressable to);

	private ConversationReference createConversationReference(TeamsAddressable address) {
		ConversationAccount ca = address == null ? null : getConversationAccount(address);

		ConversationReference cr = new ConversationReference();
		cr.setBot(botAccount);
		cr.setConversation(ca);
		cr.setServiceUrl("https://smba.trafficmanager.net/uk/");
		cr.setLocale("en-GB");
		if (address != null) {
			cr.setUser(new ChannelAccount(address.getKey()));
		}
		cr.setChannelId("msteams");
		return cr;
	}

}
