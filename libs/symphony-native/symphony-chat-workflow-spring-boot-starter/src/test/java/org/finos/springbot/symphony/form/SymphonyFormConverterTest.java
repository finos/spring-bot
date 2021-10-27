package org.finos.springbot.symphony.form;

import java.util.Map;

import org.finos.springbot.symphony.content.CashTag;
import org.finos.springbot.symphony.content.HashTag;
import org.finos.springbot.symphony.content.SymphonyUser;
import org.finos.springbot.tests.form.AbstractFormConverterTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class SymphonyFormConverterTest extends AbstractFormConverterTest {

	@Override
	protected void before() {
		ObjectMapper om = new ObjectMapper();
		om.registerModule(new SymphonyModule());
		om.registerModule(new JavaTimeModule());
		fc = new SymphonyFormConverter(om);

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPlatform() throws Exception {
		before();
		Object o = new ObjectMapper().readValue("{\"action\": \"ob4+0\", \"hashTag.\": \"some-hashtag-value\", \"cashTag.\": \"tsla\", \"someUser.\": 345315370602462}", Map.class);
		Platform to = (Platform) fc.convert((Map<String, Object>) o, Platform.class.getCanonicalName());
		Assertions.assertEquals("tsla", ((CashTag) to.getCashTag()).getName());
		Assertions.assertEquals("345315370602462", ((SymphonyUser) to.getSomeUser()).getUserId());
		Assertions.assertEquals("some-hashtag-value", ((HashTag) to.getHashTag()).getName());
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
