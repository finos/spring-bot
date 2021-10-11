package org.finos.symphony.toolkit.workflow;

import java.math.BigDecimal;
import java.util.Arrays;

import org.finos.springbot.sources.teams.content.TeamsChat;
import org.finos.springbot.sources.teams.content.TeamsUser;
import org.finos.springbot.sources.teams.json.EntityJsonConverter;
import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.fixture.EJTestObject;
import org.finos.symphony.toolkit.workflow.fixture.TestObject;
import org.finos.symphony.toolkit.workflow.fixture.TestObjects;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONCompare;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Validator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestEntityJsonConversion extends AbstractMockSymphonyTest {
	
	public static final String WORKFLOW_001 = "workflow_001";

	@Autowired
	EntityJsonConverter converter;
	
	@Autowired
	Validator validator;
		
	ObjectMapper om = new ObjectMapper();

	public Object readWorkflowValue(String json) {
		try {
			if (json == null) {
				return null;
			}

			return converter.readValue(json).get("workflow_001");
		} catch (Exception e) {
			System.out.println(json);
			throw new UnsupportedOperationException("Map Fail", e);
		}
	}

	/** 
	 * Used in tests 
	 */
	public String toWorkflowJson(Object o) {
		try {
			if (o == null) {
				return null;
			}
			EntityJson out = new EntityJson();
			out.put(WORKFLOW_001, o);
			return converter.writeValue(out);
		} catch (Exception e) {
			throw new UnsupportedOperationException("Map Fail", e);
		}
	}
	
	@Test
	public void testObject() throws Exception {

		TestObject a = new TestObject("83274239874", true, true, "rob@example.com", 234786, 2138);

		// can we convert to messageML? (something populated)
		String out = toWorkflowJson(a);
	
		compare(out, "{\"workflow_001\":{\"type\":\"org.finos.symphony.toolkit.workflow.fixture.testObject\",\"version\":\"1.0\",\"isin\":\"83274239874\",\"bidAxed\":true,\"askAxed\":true,\"creator\":\"rob@example.com\",\"bidQty\":234786,\"askQty\":2138}}");
		
		TestObject b = (TestObject) readWorkflowValue(out);
		Assertions.assertEquals(a, b);
	}

	private void compare(String out, String expected) throws JsonProcessingException, JsonMappingException {
		System.out.println("expected: "+expected);
		System.out.println("actual  : "+out);
		
		JsonNode joOut = om.readTree(out);
		JsonNode joExpected = om.readTree(expected);

		Assertions.assertEquals(joOut, joExpected);
	}

	@Test
	public void testObjects() throws Exception {

		TestObject a1 = new TestObject("83274239874", true, true, "rob@example.com", 234786, 2138);
		TestObject a2 = new TestObject("AUD274239874", true, false, "gregb@example.com", 2386, new BigDecimal("234823498.573"));

		TestObjects a = new TestObjects(Arrays.asList(a1, a2));

		String out = toWorkflowJson(a);
		compare("{\"workflow_001\":{\"type\":\"org.finos.symphony.toolkit.workflow.fixture.testObjects\",\"version\":\"1.0\",\"items\":[{\"type\":\"org.finos.symphony.toolkit.workflow.fixture.testObject\",\"version\":\"1.0\",\"isin\":\"83274239874\",\"bidAxed\":true,\"askAxed\":true,\"creator\":\"rob@example.com\",\"bidQty\":234786,\"askQty\":2138},{\"type\":\"org.finos.symphony.toolkit.workflow.fixture.testObject\",\"version\":\"1.0\",\"isin\":\"AUD274239874\",\"bidAxed\":true,\"askAxed\":false,\"creator\":\"gregb@example.com\",\"bidQty\":2386,\"askQty\":234823498.573}]}}",
				out);

		TestObjects b = (TestObjects) readWorkflowValue(out);
		Assertions.assertEquals(a, b);
	}

	@Test
	public void testOb3() throws Exception {

		EJTestObject a1 = new EJTestObject(new TeamsChat("abc", "123"), new TeamsUser("Robert Moffat", "rbo@kjite9.com"), "SOme message");
		String out =  toWorkflowJson(a1);

		compare(out, "{\n"
				+ "  \"workflow_001\" : {\n"
				+ "    \"type\" : \"org.finos.symphony.toolkit.workflow.fixture.eJTestObject\",\n"
				+ "    \"version\" : \"1.0\",\n"
				+ "    \"r\" : {\n"
				+ "      \"type\" : \"org.finos.symphony.toolkit.workflow.content.chat\",\n"
				+ "      \"version\" : \"1.0\",\n"
				+ "      \"id\" : [ {\n"
				+ "        \"type\" : \"com.symphony.user.streamID\",\n"
				+ "        \"version\" : \"1.0\",\n"
				+ "        \"value\" : \"123\"\n"
				+ "      }, {\n"
				+ "        \"type\" : \"org.finos.symphony.toolkit.workflow.sources.symphony.content.roomName\",\n"
				+ "        \"version\" : \"1.0\",\n"
				+ "        \"value\" : \"abc\"\n"
				+ "      } ]\n"
				+ "    },\n"
				+ "    \"u\" : {\n"
				+ "      \"type\" : \"com.symphony.user.mention\",\n"
				+ "      \"version\" : \"1.0\",\n"
				+ "      \"id\" : [ {\n"
				+ "        \"type\" : \"com.symphony.user.displayName\",\n"
				+ "        \"version\" : \"1.0\",\n"
				+ "        \"value\" : \"Robert Moffat\"\n"
				+ "      }, {\n"
				+ "        \"type\" : \"com.symphony.user.emailAddress\",\n"
				+ "        \"version\" : \"1.0\",\n"
				+ "        \"value\" : \"rbo@kjite9.com\"\n"
				+ "      } ]\n"
				+ "    },\n"
				+ "    \"someText\" : \"SOme message\"\n"
				+ "  }\n"
				+ "}");
		EJTestObject b = (EJTestObject) readWorkflowValue(out);
		Assertions.assertEquals(a1, b);
	}

}
