package com.github.deutschebank.symphony.json;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.symphonyoss.fin.Security;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.detuschebank.symphony.json.EntityJson;
import com.github.detuschebank.symphony.json.EntityJsonTypeResolverBuilder.VersionSpace;
import com.github.detuschebank.symphony.json.ObjectMapperFactory;
import com.symphony.integration.jira.event.Created;
import com.symphony.integration.jira.event.v2.State;
import com.symphony.user.Mention;


public class TestSerialization {

	static ObjectMapper om;
	
	@BeforeClass
	public static void setupMapper() {
		VersionSpace[] vs = ObjectMapperFactory.extendedSymphonyVersionSpace(
				new VersionSpace("com.symphony", "1.0"));
		om = ObjectMapperFactory.initialize(vs);
		
		// specific to these crazy beans
		om.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		
		// indent output
		om.enable(SerializationFeature.INDENT_OUTPUT);
		
		System.out.print(Arrays.asList(vs));
	}
	
	@Test
	public void testJiraExample1() throws Exception {
		String json = getExpected("jira-example-1.json");
		EntityJson ej = om.readValue(json, EntityJson.class);
		Assert.assertEquals("test@symphony.com", ((State) ej.get("jiraIssue")).issue.assignee.emailAddress);
		Assert.assertEquals("production", ((State) ej.get("jiraIssue")).issue.labels.get(0).text);
		EntityJson ej2 = om.readValue(json, EntityJson.class);
		Assert.assertEquals(ej, ej2);
		Assert.assertEquals(ej.hashCode(), ej2.hashCode());
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
		
		Assert.assertEquals(t1, t2);
	}
	
	@Test
	public void testJiraExample2() throws Exception {
		String json = getExpected("jira-example-2.json");
		EntityJson ej = om.readValue(json, EntityJson.class);
		Assert.assertEquals("Issue Test", ((Created) ej.get("jiraIssueCreated")).issue.subject);
		Assert.assertEquals("123456", ((Mention) ej.get("mention123")).getId().get(0).getValue());
		
		EntityJson ej2 = om.readValue(json, EntityJson.class);
		Assert.assertEquals(ej, ej2);
		Assert.assertEquals(ej.hashCode(), ej2.hashCode());

		convertBackAndCompare(json, ej, "target/testJiraExample2.json");
	}
	
	@Test
	public void testJiraExample3() throws Exception {
		String json = getExpected("jira-example-3.json");
		EntityJson ej = om.readValue(json, EntityJson.class);
		Assert.assertEquals("production", ((com.symphony.integration.jira.event.v2.Created) ej.get("jiraIssueCreated")).issue.labels.get(0).text);
		Assert.assertEquals("bot.user2", ((com.symphony.integration.jira.event.v2.Created) ej.get("jiraIssueCreated")).issue.assignee.username);
		EntityJson ej2 = om.readValue(json, EntityJson.class);
		Assert.assertEquals(ej, ej2);
		Assert.assertEquals(ej.hashCode(), ej2.hashCode());

		convertBackAndCompare(json, ej, "target/testJiraExample3.json");
	}
	
	@Test
	public void testSecuritiesExample() throws Exception {
		String jsonIn = getExpected("securities-in.json");
		String jsonOut = getExpected("securities-out.json");
		EntityJson ej = om.readValue(jsonIn, EntityJson.class);
		Assert.assertEquals("US0378331005", ((Security) ej.get("123")).getId().get(0).getValue());
		Assert.assertEquals("BBG00CSTXNX6", ((Security) ej.get("321")).getId().get(0).getValue());
		EntityJson ej2 = om.readValue(jsonIn, EntityJson.class);
		Assert.assertEquals(ej, ej2);
		Assert.assertEquals(ej.hashCode(), ej2.hashCode());

		convertBackAndCompare(jsonOut, ej, "target/testSecuritiesExample.json");
	}
	
	@Test(expected=JsonMappingException.class)
	public void testSecuritiesBrokenExample() throws Exception {
		// bad version numbers
		String json = getExpected("securities-wrong.json");
		om.readValue(json, EntityJson.class);
	}
	
	@Test
	public void testVersionMatching() {
		VersionSpace vs = new VersionSpace("", "2.0", "1.*");
		Assert.assertTrue(vs.matches("1.1"));
		Assert.assertTrue(vs.matches("1.5"));
		Assert.assertTrue(vs.matches("2.0"));
	
		VersionSpace vs2 = new VersionSpace("", "2.0", "1.[0-4]");
		Assert.assertTrue(vs2.matches("1.1"));
		Assert.assertFalse(vs2.matches("1.5"));
		Assert.assertTrue(vs2.matches("2.0"));
	}
	
	private String getExpected(String name) {
		InputStream io = getClass().getResourceAsStream(name);
		String result = new BufferedReader(new InputStreamReader(io))
				  .lines().collect(Collectors.joining("\n"));
		return result;
	}
}
