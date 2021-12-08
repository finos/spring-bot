package org.finos.springbot.teams.templating.thymeleaf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.finos.springbot.teams.content.TeamsMultiwayChat;
import org.finos.springbot.teams.content.TeamsUser;
import org.finos.springbot.teams.templating.adaptivecard.AdaptiveCardConverterConfig;
import org.finos.springbot.teams.templating.adaptivecard.JavascriptSubstitution;
import org.finos.springbot.tests.templating.AbstractTemplatingTest;
import org.finos.springbot.workflow.annotations.WorkMode;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.content.Chat;
import org.finos.springbot.workflow.content.User;
import org.finos.springbot.workflow.form.Button;
import org.finos.springbot.workflow.form.ButtonList;
import org.finos.springbot.workflow.form.DropdownList;
import org.finos.springbot.workflow.form.DropdownList.Item;
import org.finos.springbot.workflow.response.WorkResponse;
import org.finos.springbot.workflow.templating.Mode;
import org.finos.springbot.workflow.templating.WorkTemplater;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@SpringBootTest(classes = { 
		AdaptiveCardConverterConfig.class
})
public class TeamsThymeleafTemplatingTest extends AbstractTemplatingTest {

	@Autowired
	WorkTemplater<String> templater;
	
	ObjectMapper om;
	
	JavascriptSubstitution js = new JavascriptSubstitution();
	
	@Override
	protected Addressable getTo() {
		return new TeamsUser("abc1234", "Geoff Summersby", "aac123");
	}
	
	@Override
	protected Chat getChat() {
		return new TeamsMultiwayChat("chatID123", "Some Chat Channel");
	}

	@Override
	protected User getUser() {
		return (User) getTo();
	}

	@BeforeEach
	public void doSetup() {
		om = new ObjectMapper();
		om.registerModule(new JavaTimeModule());
	}

	@Override
	protected void testTemplating(WorkResponse workResponse, String testName) {
	    try {		
	    	// populate with at least one button
	    	Map<String, Object> data = workResponse.getData();
	    	ButtonList bl = (ButtonList) data.get(ButtonList.KEY);
	    	if ((bl == null) || (bl.getContents().size() == 0)) {
	    		data.put(ButtonList.KEY, new ButtonList(
	    				Arrays.asList(new Button("test", Button.Type.ACTION, "Submit"))));
	    	}

	    	
			// actual template
			new File("target/tests").mkdirs();
			JsonNode actualNode = templater.convert(workResponse.getFormClass(), translateMode(workResponse));
			String actualJson = om.writerWithDefaultPrettyPrinter().writeValueAsString(actualNode);
			System.out.println("ACTUAL  : " + actualJson);
			
	    	// expected template
			String expectedJson = loadJson(testName+".json");
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
			
			// make sure the substitution works
			ObjectNode dataOuter = om.createObjectNode();
			dataOuter.set("$root", _$root);
			String outerDataJson = om.writerWithDefaultPrettyPrinter().writeValueAsString(dataOuter);
			System.out.println("COMBINED: "+js.singleThreadedEvalLoop(outerDataJson, actualJson));
			
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
	protected DropdownList createSomeUsers(int count) {
		return new DropdownList(IntStream.range(0, count)
				.mapToObj(i -> i == 0 ? getUser() : new TeamsUser("idu"+i, "Name of "+i, "aac"+i))
				.map(tu -> new Item(tu.getKey(), tu.getName()))
				.collect(Collectors.toList()));
	}

	@Override
	protected DropdownList createSomeChats(int count) {
		return new DropdownList(IntStream.range(0, count)
				.mapToObj(i -> i == 0 ? getChat() : new TeamsMultiwayChat("idc"+i, "Chat name of "+i))
				.map(tu -> new Item(tu.getKey(), tu.getName()))
				.collect(Collectors.toList()));
	}


	public static String loadJson(String string) throws IOException {
        return StreamUtils.copyToString(TeamsThymeleafTemplatingTest.class.getResourceAsStream(string), Charset.forName("UTF-8"));
    }

}
