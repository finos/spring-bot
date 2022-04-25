package org.finos.springbot.teams.conversations;

import static org.finos.springbot.teams.state.TeamsStateStorage.ADDRESSABLE_KEY;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.finos.springbot.entityjson.EntityJson;
import org.finos.springbot.teams.content.TeamsAddressable;
import org.finos.springbot.teams.content.TeamsChat;
import org.finos.springbot.teams.history.StateStorageBasedTeamsHistory;
import org.finos.springbot.teams.state.TeamsStateStorage;
import org.finos.springbot.teams.state.TeamsStateStorage.Filter;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.content.Chat;

import com.microsoft.bot.builder.BotFrameworkAdapter;
import com.microsoft.bot.connector.authentication.MicrosoftAppCredentials;
import com.microsoft.bot.schema.ChannelAccount;

public class StateStorageBasedTeamsConversations extends AbstractTeamsConversations {
	
	public static final String ADDRESSABLE_INFO = "addressable-info";
	public static final String ADDRESSABLE_TYPE = "addressable-type";
	public static final String CHAT = "chat";
	public static final String USER = "user";
	
	
	protected final TeamsStateStorage tss;
	
	public StateStorageBasedTeamsConversations(BotFrameworkAdapter bfa, MicrosoftAppCredentials mac,
			ChannelAccount botAccount, TeamsStateStorage tss) {
		super(bfa, mac, botAccount);
		this.tss = tss;
	}

	@Override
	public Set<Addressable> getAllAddressables() {
		List<Filter> filters = new ArrayList<>();
		filters.add(new Filter(ADDRESSABLE_INFO));
		Iterable<Map<String, Object>> it = tss.retrieve(filters, false);
		return new HashSet<>(StateStorageBasedTeamsHistory.findObjectsFromItems(Addressable.class, it));
	}

	@Override
	public Set<TeamsChat> getAllChats() {
		List<Filter> filters = new ArrayList<>();
		filters.add(new Filter(ADDRESSABLE_INFO));
		filters.add(new Filter(ADDRESSABLE_TYPE, CHAT, "="));
		Iterable<Map<String, Object>> it = tss.retrieve(filters, false);
		return new HashSet<>(StateStorageBasedTeamsHistory.findObjectsFromItems(TeamsChat.class, it));
	}

	@Override
	public TeamsChat getExistingChat(String name) {
		return getAllChats().stream()
			.filter(tc -> name.equals(tc.getName()))
			.findFirst()
			.orElse(null);
	}

	@Override
	protected void ensureRoomRecorded(TeamsAddressable to) {
		String file = to.getKey()+"/addressable";
		
		Optional<Map<String, Object>> data = tss.retrieve(file);
		
		if (!data.isPresent()) {
			Map<String, String> tags = new HashMap<>();
			tags.put(ADDRESSABLE_INFO, TeamsStateStorage.PRESENT);
			tags.put(ADDRESSABLE_TYPE, to instanceof Chat ? CHAT : USER);
			tags.put(ADDRESSABLE_KEY, to.getKey());
			EntityJson ej = new EntityJson();
			ej.put(ADDRESSABLE_INFO, to);
			tss.store(file, tags, ej);
		}
	}

}
