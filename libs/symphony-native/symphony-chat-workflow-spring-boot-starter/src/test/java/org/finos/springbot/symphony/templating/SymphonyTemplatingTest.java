package org.finos.springbot.symphony.templating;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.finos.springbot.symphony.content.SymphonyRoom;
import org.finos.springbot.symphony.content.SymphonyUser;
import org.finos.springbot.symphony.json.DataHandler;
import org.finos.springbot.symphony.json.DataHandlerCofig;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StreamUtils;

@SpringBootTest(classes = { 
		FreemarkerTypeConverterConfig.class,
		DataHandlerCofig.class,
})
public class SymphonyTemplatingTest extends AbstractTemplatingTest {

	@Autowired
	WorkTemplater<String> templater;
	
	@Autowired
	DataHandler ejc;
	
	@Override
	protected Addressable getTo() {
		return new SymphonyUser(284376, "Geoff Summersby", "geoff@example.com");
	}

	@Override
	protected Chat getChat() {
		return new SymphonyRoom("Some Chat Channel", "chatID123");
	}

	@Override
	protected User getUser() {
		return (User) getTo();
	}
	
	private Item convertToItem(User u) {
		return new Item(u.getKey(), u.getName());
	}
	
	private Item convertToItem(Chat u) {
		return new Item(u.getKey(), u.getName());
	}

	protected DropdownList createSomeUsers(int count) {
		return new DropdownList(IntStream.range(0, count)
				.mapToObj(i -> i == 0 ? getUser() : new SymphonyUser(i, "Name of "+i, i+"@example.com"))
				.map(r -> convertToItem(r))
				.collect(Collectors.toList()));
	}

	@Override
	protected DropdownList createSomeChats(int count) {
		return new DropdownList(IntStream.range(0, count)
				.mapToObj(i -> i == 0 ? getChat() : new SymphonyRoom("Chat name of "+i, "idc"+i))
				.map(r -> convertToItem(r))
				.collect(Collectors.toList()));
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
			String actualML = "<messageML>"+ templater.convert(workResponse.getFormClass(), translateMode(workResponse))+"</messageML>";
			System.out.println("ACTUAL  : " + actualML);
			
	    	// expected template
			String expectedML = load(testName+".html");
			System.out.println("EXPECTED: " + expectedML);
			 
			// write expected/actual
			FileOutputStream out1 = new FileOutputStream("target/tests/"+testName+".html");
			StreamUtils.copy(actualML, StandardCharsets.UTF_8, out1);
			FileOutputStream out1ex = new FileOutputStream("target/tests/"+testName+".expected.html");
			StreamUtils.copy(expectedML, StandardCharsets.UTF_8, out1ex);
			
			// write data
			String dataJson = ejc.formatData(workResponse);
			FileOutputStream out1data = new FileOutputStream("target/tests/"+testName+".data.json");
			StreamUtils.copy(dataJson, StandardCharsets.UTF_8, out1data);
			
			
			// do comparison
			Assertions.assertEquals(actualML, expectedML);
			
		
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}

	protected Mode translateMode(WorkResponse workResponse) {
		return workResponse.getMode() == WorkMode.EDIT ? Mode.FORM : Mode.DISPLAY;
	}

	
    private String load(String string) throws IOException {
        return StreamUtils.copyToString(SymphonyTemplatingTest.class.getResourceAsStream(string), Charset.forName("UTF-8")).replace("\r\n", "\n");
    }

}
