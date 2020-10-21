package com.github.deutschebank.symphony.workflow;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.deutschebank.symphony.json.EntityJson;
import com.github.deutschebank.symphony.workflow.content.RoomDef;
import com.github.deutschebank.symphony.workflow.content.UserDef;
import com.github.deutschebank.symphony.workflow.fixture.TestOb3;
import com.github.deutschebank.symphony.workflow.fixture.TestObject;
import com.github.deutschebank.symphony.workflow.fixture.TestObjects;
import com.github.deutschebank.symphony.workflow.form.Button;
import com.github.deutschebank.symphony.workflow.form.Button.Type;
import com.github.deutschebank.symphony.workflow.sources.symphony.handlers.EntityJsonConverter;
import com.github.deutschebank.symphony.workflow.validation.ErrorHelp;

public class TestEntityJsonConversion extends AbstractMockSymphonyTest {

	@Autowired
	EntityJsonConverter converter;
	
	@Autowired
	Validator validator;
		
	ObjectMapper om = new ObjectMapper();

	@Test
	public void testObject() throws Exception {

		TestObject a = new TestObject("83274239874", true, true, "rob@example.com", 234786, 2138);

		// can we convert to messageML? (something populated)
		String out = converter.toWorkflowJson(a);
	
		compare(out, "{\"workflow_001\":{\"type\":\"com.github.deutschebank.symphony.workflow.fixture.testObject\",\"version\":\"1.0\",\"isin\":\"83274239874\",\"bidAxed\":true,\"askAxed\":true,\"creator\":\"rob@example.com\",\"bidQty\":234786,\"askQty\":2138}}");
		
		TestObject b = (TestObject) converter.readWorkflowValue(out);
		Assert.assertEquals(a, b);
	}

	private void compare(String out, String expected) throws JsonProcessingException, JsonMappingException {
		System.out.println("expected: "+expected);
		System.out.println("actual  : "+out);
		
		JsonNode joOut = om.readTree(out);
		JsonNode joExpected = om.readTree(expected);

		Assert.assertEquals(joOut, joExpected);
	}

	@Test
	public void testObjects() throws Exception {

		TestObject a1 = new TestObject("83274239874", true, true, "rob@example.com", 234786, 2138);
		TestObject a2 = new TestObject("AUD274239874", true, false, "gregb@example.com", 2386, new BigDecimal("234823498.573"));

		TestObjects a = new TestObjects(Arrays.asList(a1, a2));

		String out = converter.toWorkflowJson(a);
		compare("{\"workflow_001\":{\"type\":\"com.github.deutschebank.symphony.workflow.fixture.testObjects\",\"version\":\"1.0\",\"items\":[{\"type\":\"com.github.deutschebank.symphony.workflow.fixture.testObject\",\"version\":\"1.0\",\"isin\":\"83274239874\",\"bidAxed\":true,\"askAxed\":true,\"creator\":\"rob@example.com\",\"bidQty\":234786,\"askQty\":2138},{\"type\":\"com.github.deutschebank.symphony.workflow.fixture.testObject\",\"version\":\"1.0\",\"isin\":\"AUD274239874\",\"bidAxed\":true,\"askAxed\":false,\"creator\":\"gregb@example.com\",\"bidQty\":2386,\"askQty\":234823498.573}]}}",
				out);

		TestObjects b = (TestObjects) converter.readWorkflowValue(out);
		Assert.assertEquals(a, b);
	}

	@Test
	public void testOb3() throws Exception {

		TestOb3 a1 = new TestOb3(new RoomDef("abc", "123", true, null), new UserDef(null, "Robert Moffat", "rbo@kjite9.com"), "SOme message");
		String out = converter.toWorkflowJson(a1);

		compare(out, "{\"workflow_001\":{\"type\":\"com.github.deutschebank.symphony.workflow.fixture.testOb3\",\"version\":\"1.0\",\"r\":{\"type\":\"com.github.deutschebank.symphony.workflow.content.roomDef\",\"version\":\"1.0\",\"roomName\":\"abc\",\"roomDescription\":\"123\",\"pub\":true,\"id\":null},\"u\":{\"type\":\"com.github.deutschebank.symphony.workflow.content.userDef\",\"version\":\"1.0\",\"name\":\"Robert Moffat\",\"id\":null,\"tagType\":\"USER\",\"address\":\"rbo@kjite9.com\"},\"someText\":\"SOme message\"}}");

		TestOb3 b = (TestOb3) converter.readWorkflowValue(out);
		Assert.assertEquals(a1, b);
	}

}
