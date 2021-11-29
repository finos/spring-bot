package org.finos.springbot.symphony.json;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.finos.springbot.entities.VersionSpaceHelp;
import org.finos.springbot.entityjson.EntityJson;
import org.finos.springbot.entityjson.ObjectMapperFactory;
import org.finos.springbot.entityjson.VersionSpace;
import org.finos.springbot.symphony.json.ClassWithArray.SubClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.symphonyoss.fin.Security;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.symphony.integration.Icon;
import com.symphony.integration.User;
import com.symphony.integration.jira.Issue;
import com.symphony.integration.jira.IssueType;
import com.symphony.integration.jira.Label;
import com.symphony.integration.jira.Priority;
import com.symphony.integration.jira.event.Created;
import com.symphony.integration.jira.event.V2Created;
import com.symphony.integration.jira.event.v2.State;
import com.symphony.user.Mention;


public class TestSerialization {

	static ObjectMapper om;
	
	@BeforeAll
	public static void setupMapper() {
		VersionSpace[] vs = VersionSpaceHelp.extendedSymphonyVersionSpace(
				new VersionSpace(ClassWithEnum.class, "1.0"),
				new VersionSpace(ClassWithArray.class, "1.0"),
				new VersionSpace(ClassWithArray.SubClass.class, "1.0"),
				new VersionSpace(Created.class, "1.0"),
				new VersionSpace("com.symphony.integration.jira.event.v2.created", V2Created.class, "1.0"),
				new VersionSpace(Issue.class, "1.0"),
				new VersionSpace(State.class, "1.0"),
				new VersionSpace(IssueType.class, "1.0"),
				new VersionSpace(Label.class, "1.0"),
				new VersionSpace(Priority.class, "1.0"),
				new VersionSpace(Icon.class, "1.0"),
				new VersionSpace(User.class, "1.0")
				);
		om = ObjectMapperFactory.initialize(vs);
		
		// specific to these crazy beans
		om.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		
		// indent output
		om.enable(SerializationFeature.INDENT_OUTPUT);
		
		System.out.print(Arrays.asList(vs));
	}
	
	@Test
	public void testClassWithEnum() throws Exception {
		ClassWithEnum cwe = new ClassWithEnum();
		cwe.c = ClassWithEnum.Choice.B;
		cwe.name = "Fred";
		EntityJson ej = new EntityJson();
		ej.put("cwe", cwe);
		String json = om.writeValueAsString(ej);
		System.out.println(json);
		EntityJson out = om.readValue(json, EntityJson.class);
		Assertions.assertEquals(ej, out);
	
	}
	
	@Test
	public void testClassWithArray() throws Exception {
		ClassWithArray cwe = new ClassWithArray();
		
		ArrayList<SubClass> vals1 = new ArrayList<SubClass>();
		vals1.add(new SubClass("la1", "lb1"));
		vals1.add(new SubClass("la2", "lb2"));
		vals1.add(new SubClass("la3", "lb3"));
		cwe.setL(vals1);
		
		ArrayList<SubClass> vals2 = new ArrayList<SubClass>();
		vals2.add(new SubClass("ma1", "mb1"));
		vals2.add(new SubClass("ma2", "mb2"));
		vals2.add(new SubClass("ma3", "mb3"));
		cwe.setM(vals2);
		
		
		EntityJson ej = new EntityJson();
		ej.put("cwe", cwe);
		String json = om.writeValueAsString(ej);
		System.out.println(json);
		EntityJson out = om.readValue(json, EntityJson.class);
		Assertions.assertEquals(ej, out);
	
	}
	
	
	@Test
	public void testJiraExample1() throws Exception {
		String json = getExpected("jira-example-1.json");
		EntityJson ej = om.readValue(json, EntityJson.class);
		Assertions.assertEquals("test@symphony.com", ((State) ej.get("jiraIssue")).issue.assignee.emailAddress);
		Assertions.assertEquals("production", ((State) ej.get("jiraIssue")).issue.labels.get(0).text);
		EntityJson ej2 = om.readValue(json, EntityJson.class);
		Assertions.assertEquals(ej, ej2);
		Assertions.assertEquals(ej.hashCode(), ej2.hashCode());
		// ok, convert back into json
		convertBackAndCompare(json, ej, "target/testJiraExample1.json");
		
	}

	private void convertBackAndCompare(String json, EntityJson ej, String file) throws JsonProcessingException, IOException, JsonMappingException {
		String done = om.writeValueAsString(ej);
		System.out.println(done);
		FileWriter fw = new FileWriter(file);
		fw.write(done);
		fw.close();
		
		JsonNode t1 = om.readTree(json);
		JsonNode t2 = om.readTree(done);
		
		Assertions.assertEquals(t1, t2);
	}
	
	@Test
	public void testJiraExample2() throws Exception {
		String json = getExpected("jira-example-2.json");
		EntityJson ej = om.readValue(json, EntityJson.class);
		Assertions.assertEquals("Issue Test", ((Created) ej.get("jiraIssueCreated")).issue.subject);
		Assertions.assertEquals("123456", ((Mention) ej.get("mention123")).getId().get(0).getValue());
		
		EntityJson ej2 = om.readValue(json, EntityJson.class);
		Assertions.assertEquals(ej, ej2);
		Assertions.assertEquals(ej.hashCode(), ej2.hashCode());

		convertBackAndCompare(json, ej, "target/testJiraExample2.json");
	}
	
	@Test
	public void testJiraExample3() throws Exception {
		String json = getExpected("jira-example-3.json");
		EntityJson ej = om.readValue(json, EntityJson.class);
		Assertions.assertEquals("production", ((V2Created) ej.get("jiraIssueCreated")).issue.labels.get(0).text);
		Assertions.assertEquals("bot.user2", ((V2Created) ej.get("jiraIssueCreated")).issue.assignee.username);
		EntityJson ej2 = om.readValue(json, EntityJson.class);
		Assertions.assertEquals(ej, ej2);
		Assertions.assertEquals(ej.hashCode(), ej2.hashCode());

		convertBackAndCompare(json, ej, "target/testJiraExample3.json");
	}
	
	@Test
	public void testSecuritiesExample() throws Exception {
		String jsonIn = getExpected("securities-in.json");
		String jsonOut = getExpected("securities-out.json");
		EntityJson ej = om.readValue(jsonIn, EntityJson.class);
		Assertions.assertEquals("US0378331005", ((Security) ej.get("123")).getId().get(0).getValue());
		Assertions.assertEquals("BBG00CSTXNX6", ((Security) ej.get("321")).getId().get(0).getValue());
		EntityJson ej2 = om.readValue(jsonIn, EntityJson.class);
		Assertions.assertEquals(ej, ej2);
		Assertions.assertEquals(ej.hashCode(), ej2.hashCode());

		convertBackAndCompare(jsonOut, ej, "target/testSecuritiesExample.json");
	}
	
	@Test
	public void testSecuritiesBrokenExample() throws Exception {
		Assertions.assertThrows(JsonMappingException.class, () -> {
			// bad version numbers
		
			String json = getExpected("securities-wrong.json");
			om.readValue(json, EntityJson.class);
	
		});
	}
	
	@Test
	public void testVersionMatching() {
		VersionSpace vs = new VersionSpace(Object.class, "2.0", "1.*");
		Assertions.assertTrue(vs.versionMatches("1.1"));
		Assertions.assertTrue(vs.versionMatches("1.5"));
		Assertions.assertTrue(vs.versionMatches("2.0"));
	
		VersionSpace vs2 = new VersionSpace(Object.class, "2.0", "1.[0-4]");
		Assertions.assertTrue(vs2.versionMatches("1.1"));
		Assertions.assertFalse(vs2.versionMatches("1.5"));
		Assertions.assertTrue(vs2.versionMatches("2.0"));
	}
	
	private String getExpected(String name) {
		InputStream io = getClass().getResourceAsStream(name);
		String result = new BufferedReader(new InputStreamReader(io))
				  .lines().collect(Collectors.joining("\n"));
		return result;
	}
}
