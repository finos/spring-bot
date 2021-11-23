package org.finos.springbot.teams.history;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.finos.springbot.teams.TeamsException;
import org.finos.springbot.teams.content.TeamsAddressable;
import org.finos.springbot.teams.content.TeamsChat;
import org.finos.springbot.teams.content.TeamsConversation;
import org.finos.springbot.teams.content.TeamsUser;
import org.finos.springbot.teams.turns.CurrentTurnContext;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.content.Tag;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.builder.teams.TeamsInfo;
import com.microsoft.bot.connector.authentication.MicrosoftAppCredentials;
import com.microsoft.bot.schema.teams.TeamDetails;
import com.microsoft.graph.models.ChatMessage;
import com.microsoft.graph.requests.ChatMessageCollectionPage;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.UserScopeTeamsAppInstallationCollectionPage;


public class TeamsHistoryImpl implements TeamsHistory {
	
	private MicrosoftAppCredentials mac;
	private ObjectMapper om;
	
	public TeamsHistoryImpl(MicrosoftAppCredentials mac, ObjectMapper attachmentDataHandler) {
		this.mac = mac;
		this.om = attachmentDataHandler;
	}
	
	public static String getTagName(Class<?> x) {
		return x.getName();
	}

	@Override
	public <X> Optional<X> getLastFromHistory(Class<X> type, TeamsAddressable address) {
		try {		
			String requiredTagName = getTagName(type);
			TurnContext ctx = CurrentTurnContext.CURRENT_CONTEXT.get();
			//System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(ctx.getActivity()));
			
			GraphServiceClient<?> gsc = GraphServiceClient.builder()
				.authenticationProvider(u -> mac.getToken())
				.buildClient();
			
			//List<ChannelInfo> chans = TeamsInfo.getTeamChannels(ctx, ctx.getActivity().teamsGetTeamId()).get();
			
			
			if (address instanceof TeamsChat) {
				System.out.println(om.writerWithDefaultPrettyPrinter().writeValueAsString(
						gsc.chats(address.getKey()).messages()
						.buildRequest().get()));
				
				return Optional.empty();
				
				
			} else if (address instanceof TeamsConversation) {	
				TeamsConversation tc = (TeamsConversation) address;
				ChatMessageCollectionPage cmcp = gsc.teams(tc.getAadGroupId())
					.channels(tc.getChannelId())
					.messages(tc.getMessageId())
					.replies()
					.buildRequest()
//					.filter("contains(summary, '"+requiredTagName+"')") 
					.get();
				
				if (cmcp.getCurrentPage().size() > 0) {
					for (ChatMessage cm : cmcp.getCurrentPage()) {
						if ((cm.summary != null) && (cm.summary.contains(requiredTagName))) {
							// pull back the data from the first match
							
						}
					}
				}
					
					
				return Optional.empty();
			} else if (address instanceof TeamsUser) {
				TeamDetails td = TeamsInfo.getTeamDetails(ctx, ctx.getActivity().teamsGetTeamId()).get();
				UserScopeTeamsAppInstallationCollectionPage c = gsc.users(((TeamsUser)address).getAadObjectId()).teamwork().installedApps()
					.buildRequest()
					.get();
				
				return Optional.empty();
			} else {
				return Optional.empty();
			}
			
			
			

			
			
			
//			String channel = address.getKey().substring(0, address.getKey().indexOf(";"));
//			String message = address.getKey().substring(address.getKey().indexOf("=")+1);
//			
////			Team t = gsc.teams("9588b2c3-42f2-497e-a679-1c85c2980cf5").channels("asd").messages(Collections.emptyList()).get();
//			
//			gsc.teams("9588b2c3-42f2-497e-a679-1c85c2980cf5");
//			
//			
//			gsc.chats("29:13qY8hmfkJinH9-v7rYKjCNFHYFJXKbjqR-NyzyKzL694npelHJoq5HrVtqJLRYo79OYeHGQq-bhtJM5N-yKXyQ");
//			
//			//gsc.chats("bla").p
//			
////			String channel = address.getKey();
////			ChatMessage doobie = gsc.chats(channel)
////					.messages(message)
////					.buildRequest().get();
////		
			
//			gsc.c
			
//			gsc.chats(channel).installedApps().buildRequest().get();
			
//			return Optional.empty();
		} catch (Exception e) {
			throw new TeamsException("Can't do getLastFromHistory", e);
		}
	}

	@Override
	public <X> Optional<X> getLastFromHistory(Class<X> type, Tag t, TeamsAddressable address) {
		return Optional.empty();
	}

	@Override
	public <X> List<X> getFromHistory(Class<X> type, TeamsAddressable address, Instant since) {
		return Collections.emptyList();
	}

	@Override
	public <X> List<X> getFromHistory(Class<X> type, Tag t, TeamsAddressable address, Instant since) {
		return Collections.emptyList();
	}

	@Override
	public boolean isSupported(Addressable a) {
		return a instanceof TeamsAddressable;
	}

	
}
