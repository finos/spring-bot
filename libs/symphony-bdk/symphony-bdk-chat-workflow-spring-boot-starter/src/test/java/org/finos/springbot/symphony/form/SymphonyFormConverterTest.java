package org.finos.springbot.symphony.form;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.finos.springbot.symphony.content.CashTag;
import org.finos.springbot.symphony.content.HashTag;
import org.finos.springbot.symphony.content.SymphonyRoom;
import org.finos.springbot.symphony.content.SymphonyUser;
import org.finos.springbot.symphony.conversations.SymphonyConversations;
import org.finos.springbot.tests.form.AbstractFormConverterTest;
import org.finos.springbot.workflow.content.Chat;
import org.finos.springbot.workflow.content.User;
import org.finos.springbot.workflow.conversations.AllConversations;
import org.finos.springbot.workflow.conversations.PlatformConversations;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class SymphonyFormConverterTest extends AbstractFormConverterTest {
	
	SymphonyConversations sc;
	
	AllConversations ac = new AllConversations() {

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		protected List<PlatformConversations<Chat, User>> getDelegates() {
			return Collections.singletonList((PlatformConversations) sc);
		}
		
	};
	
	@Override
	protected void before() {
		sc = Mockito.mock(SymphonyConversations.class);
		ObjectMapper om = new ObjectMapper();
		om.registerModule(new SymphonyFormDeserializerModule(ac));
		om.registerModule(new JavaTimeModule());
		fc = new SymphonyFormConverter(om);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPlatform() throws Exception {
		before();
		
		// set up the user mapping
		Mockito.when(sc.getUserById("345315370602462")).thenReturn(new SymphonyUser(345315370602462l));
		Mockito.when(sc.getChatById("abc1234")).thenReturn(new SymphonyRoom("Some room", "abc1234"));

		
		Object o = new ObjectMapper().readValue("{\"action\": \"ob4+0\", \"hashTag.\": \"some-hashtag-value\", \"cashTag.\": \"tsla\", \"someUser.\": 345315370602462, \"chat\": \"abc1234\"}", Map.class);
		Platform to = (Platform) fc.convert((Map<String, Object>) o, Platform.class.getCanonicalName());
		Assertions.assertEquals("tsla", ((CashTag) to.getCashTag()).getName());
		Assertions.assertEquals("345315370602462", ((SymphonyUser) to.getSomeUser()).getUserId());
		Assertions.assertEquals("some-hashtag-value", ((HashTag) to.getHashTag()).getName());
		Assertions.assertEquals("Some room", ((SymphonyRoom) to.getChat()).getName());
	}
	
	@Test
	public void testListOfStringAddValue() throws Exception {
		before();
		Object o = new ObjectMapper().readValue("{\"action\": \"names.table-add-done\", \"entity.formdata\": \"Amsidh\"}", Map.class);
		@SuppressWarnings("unchecked")
		String to = (String) fc.convert((Map<String, Object>) o, String.class.getCanonicalName());
		Assertions.assertEquals("Amsidh", to);
	}

	@Test
	public void testListOfIntegerAddValue() throws Exception {
		before();
		Object o = new ObjectMapper().readValue("{\"action\": \"names.table-add-done\", \"entity.formdata\": 40}", Map.class);
		@SuppressWarnings("unchecked")
		Integer to = (Integer) fc.convert((Map<String, Object>) o, Integer.class.getCanonicalName());
		Assertions.assertEquals(40, to);
	}
}
