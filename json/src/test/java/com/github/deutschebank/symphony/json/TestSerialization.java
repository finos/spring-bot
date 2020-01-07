package com.github.deutschebank.symphony.json;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.Module.SetupContext;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.detuschebank.symphony.json.EntityJson;
import com.github.detuschebank.symphony.json.EntityJsonTypeResolverBuilder;
import com.github.detuschebank.symphony.json.EntityJsonTypeResolverBuilder.VersionSpace;
import com.symphony.integration.jira.Issue;
import com.symphony.integration.jira.event.Created;
import com.symphony.integration.jira.event.v2.State;
import com.symphony.user.Mention;


public class TestSerialization {

	static ObjectMapper om;
	
	@BeforeClass
	public static void setupMapper() {
		om = new ObjectMapper();
		EntityJsonTypeResolverBuilder trb = new EntityJsonTypeResolverBuilder(om.getTypeFactory(), 
				new VersionSpace("com.symphony", "1.0"), new VersionSpace("org.symphonyoss", "1.0"));
		om.setDefaultTyping(trb);
		om.addHandler(trb.getVersionHandler());
		
		// specific to these crazy beans
		om.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		
		// indent output
		om.enable(SerializationFeature.INDENT_OUTPUT);
	}
	
	@Test
	public void testJiraExample1() throws Exception {
		String json = getExpected("jira-example-1.json");
		EntityJson ej = om.readValue(json, EntityJson.class);
		Assert.assertEquals("test@symphony.com", ((State) ej.get("jiraIssue")).issue.assignee.emailAddress);
		Assert.assertEquals("production", ((State) ej.get("jiraIssue")).issue.labels.get(0).text);

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
		Assert.assertEquals("123456", ((Mention) ej.get("mention123")).id.get(0).value);

		convertBackAndCompare(json, ej, "target/testJiraExample2.json");
	}
	
	@Test
	public void testJiraExample3() throws Exception {
		String json = getExpected("jira-example-3.json");
		EntityJson ej = om.readValue(json, EntityJson.class);
		Assert.assertEquals("production", ((com.symphony.integration.jira.event.v2.Created) ej.get("jiraIssueCreated")).issue.labels.get(0).text);
		Assert.assertEquals("bot.user2", ((com.symphony.integration.jira.event.v2.Created) ej.get("jiraIssueCreated")).issue.assignee.username);

		convertBackAndCompare(json, ej, "target/testJiraExample3.json");
	}
	
	
	private String getExpected(String name) {
		InputStream io = getClass().getResourceAsStream(name);
		String result = new BufferedReader(new InputStreamReader(io))
				  .lines().collect(Collectors.joining("\n"));
		return result;
	}
}
