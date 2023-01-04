package org.finos.springbot.teams.history;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import org.finos.springbot.entityjson.EntityJson;
import org.finos.springbot.teams.content.TeamsAddressable;
import org.finos.springbot.teams.content.TeamsChannel;
import org.finos.springbot.teams.content.TeamsUser;
import org.finos.springbot.teams.handlers.TeamsResponseHandler;
import org.finos.springbot.teams.state.MemoryStateStorage;
import org.finos.springbot.teams.state.TeamsStateStorage;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.data.DataHandlerConfig;
import org.finos.springbot.workflow.data.EntityJsonConverter;
import org.finos.springbot.workflow.tags.HeaderDetails;
import org.finos.springbot.workflow.tags.TagSupport;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = { 
		DataHandlerConfig.class, 
	})
public class HistoryTest {
	
	TeamsStateStorage teamsState;
	
	TeamsHistory teamsHistory;
	
	@Autowired
	EntityJsonConverter ejc;
	
	@BeforeEach
	public void setUp() {
		teamsState = new MemoryStateStorage(ejc);
		teamsHistory = new StateStorageBasedTeamsHistory(teamsState);
	}
	
	@Test
	public void testGetNothing() {
		TeamsAddressable a = new TeamsChannel("id123", "Geoff Z");
		Assertions.assertFalse(teamsHistory.getLastFromHistory(TestObject.class, a).isPresent());
	}

	private void putSomeDataInHistory(TeamsAddressable a, TeamsUser in) {
		EntityJson val = new EntityJson();
		val.put("someUser", in);
		val.put("someChannel", a);
		val.put(StorageIDResponseHandler.STORAGE_ID_KEY, ""+new Random().nextFloat());
		
		Set<String> tags = new HashSet<String>();
		tags.addAll(TagSupport.classTags(a));
		tags.addAll(TagSupport.classTags(in));
		HeaderDetails hd = new HeaderDetails();
		hd.setTags(new ArrayList<String>(tags));
		val.put(HeaderDetails.KEY, hd);
		
		TeamsResponseHandler.performStorage(a, val, teamsState);
	}
	
	@Test
	public void testPutAndGetLast() {
		TeamsAddressable a = new TeamsChannel("id123", "Geoff Z");
		TeamsUser in = new TeamsUser("id123", "Geoff Z", "aad1243");

		putSomeDataInHistory(a, in);
		
		Assertions.assertEquals(in, teamsHistory.getLastFromHistory(TeamsUser.class, a).get());
		Assertions.assertEquals(a, teamsHistory.getLastFromHistory(TeamsChannel.class, a).get());
	}

	@Test
	public void testFindInHistory() {
		TeamsAddressable a = new TeamsChannel("id123", "Geoff Z");
		TeamsUser in = new TeamsUser("id123", "Geoff A", "aad1243");
		TeamsUser in2 = new TeamsUser("id234", "Geoff B", "aad2345");
		TeamsUser in3= new TeamsUser("id345", "Geoff C", "aad4567");

		putSomeDataInHistory(a, in);
		putSomeDataInHistory(a, in2);
		putSomeDataInHistory(a, in3);
		
		
		List<TeamsUser> allUsers = teamsHistory.getFromHistory(TeamsUser.class, a, Instant.now().minus(5l, ChronoUnit.MINUTES));
		
		Assertions.assertEquals(3, allUsers.size());
		Assertions.assertTrue(allUsers.contains(in3));
		Assertions.assertTrue(allUsers.contains(in2));
		Assertions.assertTrue(allUsers.contains(in));
		
	}
		
	
}
