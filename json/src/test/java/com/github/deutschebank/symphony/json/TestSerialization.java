package com.github.deutschebank.symphony.json;

import java.io.BufferedReader;
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
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.Module.SetupContext;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.detuschebank.symphony.json.EntityJson;
import com.github.detuschebank.symphony.json.EntityJsonTypeResolverBuilder;
import com.github.detuschebank.symphony.json.EntityJsonTypeResolverBuilder.VersionSpace;
import com.symphony.integration.jira.Issue;
import com.symphony.integration.jira.event.v2.State;


public class TestSerialization {

	static ObjectMapper om;
	
	@BeforeClass
	public static void setupMapper() {
		om = new ObjectMapper();
		EntityJsonTypeResolverBuilder trb = new EntityJsonTypeResolverBuilder(om.getTypeFactory(), 
				new VersionSpace("com.symphony", "1.0"), new VersionSpace("org.symphonyoss", "0.1"));
		om.setDefaultTyping(trb);
		om.addHandler(trb.getVersionHandler());
		
		// specific to these crazy beans
		om.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
	}
	
	@Test
	public void testJiraExample1() throws Exception {
		String json = getExpected("jira-example-1.json");
		EntityJson ej = om.readValue(json, EntityJson.class);
		Assert.assertEquals("test@symphony.com", ((State) ej.get("jiraIssue")).issue.assignee.emailAddress);
		Assert.assertEquals("production", ((State) ej.get("jiraIssue")).issue.labels.get(0).text);

	}
	
	private String getExpected(String name) {
		InputStream io = getClass().getResourceAsStream(name);
		String result = new BufferedReader(new InputStreamReader(io))
				  .lines().collect(Collectors.joining("\n"));
		return result;
	}
}
