package org.finos.springbot.teams.templating;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.finos.springbot.teams.AbstractMockTeamsTest;
import org.finos.springbot.teams.content.TeamsChat;
import org.finos.springbot.teams.content.TeamsUser;
import org.finos.springbot.workflow.annotations.WorkMode;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.content.Chat;
import org.finos.springbot.workflow.content.User;
import org.finos.springbot.workflow.response.WorkResponse;
import org.finos.springbot.workflow.templating.AbstractTemplatingTest;
import org.finos.springbot.workflow.templating.Mode;
import org.finos.springbot.workflow.templating.TypeConverter;
import org.finos.springbot.workflow.templating.WorkTemplater;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@SpringBootTest(classes = { 
		AdaptiveCardConverterConfig.class
})
public class TemplatingTest extends AbstractTemplatingTest{

	@Autowired
	List<TypeConverter<JsonNode>> converters;
	
	WorkTemplater<JsonNode> templater;
	
	ObjectMapper om;
	
	@Override
	protected Addressable getTo() {
		return new TeamsUser("abc1234", "Geoff Summersby");
	}
	
	@Override
	protected Chat getChat() {
		return new TeamsChat("chatID123", "Some Chat Channel");
	}

	@Override
	protected User getUser() {
		return (User) getTo();
	}

	@BeforeEach
	public void doSetup() {
		templater = new AdaptiveCardTemplater(converters);
		om = new ObjectMapper();
		om.registerModule(new JavaTimeModule());
	}

	@Override
	protected void testTemplating(WorkResponse workResponse, String testName) {
	    try {			    
			// actual template
			new File("target/tests").mkdirs();
			JsonNode actualNode = templater.convert(workResponse.getFormClass(), translateMode(workResponse));
			String actualJson = om.writerWithDefaultPrettyPrinter().writeValueAsString(actualNode);
			System.out.println("ACTUAL  : " + actualJson);
			
	    	// expected template
			String expectedJson = AbstractMockTeamsTest.loadJson(testName+".json");
			JsonNode expectedNode = om.readTree(expectedJson);
			System.out.println("EXPECTED: " + expectedJson);
			 
			// write expected/actual
			FileOutputStream out1 = new FileOutputStream("target/tests/"+testName+".json");
			StreamUtils.copy(actualJson, StandardCharsets.UTF_8, out1);
			FileOutputStream out1ex = new FileOutputStream("target/tests/"+testName+".expected.json");
			StreamUtils.copy(expectedJson, StandardCharsets.UTF_8, out1ex);
			
			// write data
			JsonNode _$root = om.valueToTree(workResponse.getData());
			String dataJson = om.writerWithDefaultPrettyPrinter().writeValueAsString(_$root);
			FileOutputStream out1data = new FileOutputStream("target/tests/"+testName+".data.json");
			StreamUtils.copy(dataJson, StandardCharsets.UTF_8, out1data);
			
			// do comparison
			Assertions.assertEquals(expectedNode, actualNode);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Mode translateMode(WorkResponse workResponse) {
		return workResponse.getMode() == WorkMode.EDIT ? Mode.FORM : Mode.DISPLAY;
	}

	@Override
	protected List<User> createSomeUsers(int count) {
		return IntStream.range(0, count)
				.mapToObj(i -> i == 0 ? getUser() : new TeamsUser("idu"+i, "Name of "+i))
				.collect(Collectors.toList());
	}

	@Override
	protected List<Chat> createSomeChats(int count) {
		return IntStream.range(0, count)
				.mapToObj(i -> i == 0 ? getChat() : new TeamsChat("idc"+i, "Chat name of "+i))
				.collect(Collectors.toList());
	}

}
