package org.finos.springbot.symphony.templating;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.finos.springbot.symphony.content.SymphonyRoom;
import org.finos.springbot.symphony.content.SymphonyUser;
import org.finos.springbot.tests.templating.AbstractTemplatingTest;
import org.finos.springbot.workflow.annotations.WorkMode;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.content.Chat;
import org.finos.springbot.workflow.content.User;
import org.finos.springbot.workflow.response.WorkResponse;
import org.finos.springbot.workflow.templating.Mode;
import org.finos.springbot.workflow.templating.WorkTemplater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = { 
		FreemarkerTypeConverterConfig.class
})
public class SymphonyTemplatingTest extends AbstractTemplatingTest {

	@Autowired
	WorkTemplater<String> templater;
	
	@Override
	protected Addressable getTo() {
		return new SymphonyUser("abc1234", "Geoff Summersby");
	}

	@Override
	protected Chat getChat() {
		return new SymphonyRoom("Some Chat Channel", "chatID123");
	}

	@Override
	protected User getUser() {
		return (User) getTo();
	}

	protected List<User> createSomeUsers(int count) {
		return IntStream.range(0, count)
				.mapToObj(i -> i == 0 ? getUser() : new SymphonyUser(i, "Name of "+i, i+"@example.com"))
				.collect(Collectors.toList());
	}

	@Override
	protected List<Chat> createSomeChats(int count) {
		return IntStream.range(0, count)
				.mapToObj(i -> i == 0 ? getChat() : new SymphonyRoom("Chat name of "+i, "idc"+i))
				.collect(Collectors.toList());
	}

	@Override
	protected void testTemplating(WorkResponse workResponse, String testName) {
		
		templater.convert(workResponse.getFormClass(), workResponse.getMode() == WorkMode.EDIT ? Mode.FORM : Mode.DISPLAY);

//	        String expectedJson = loadJson(testStemJson);
//	        String expectedML = loadML(testStemML);
//				
//		        
//		    new File("target/tests").mkdirs();
//		        
//	        FileOutputStream out1 = new FileOutputStream("target/tests/"+testStemML);
//	        StreamUtils.copy(msg.getValue(), StandardCharsets.UTF_8, out1);
//	        
//	        FileOutputStream out1ex = new FileOutputStream("target/tests/"+testStemML+".expected");
//	        StreamUtils.copy(expectedML, StandardCharsets.UTF_8, out1ex);
//		        
//	        FileOutputStream out2 = new FileOutputStream("target/tests/"+testStemJson);
//	        StreamUtils.copy(data.getValue(), StandardCharsets.UTF_8, out2);
//
//	        FileOutputStream out2ex = new FileOutputStream("target/tests/"+testStemJson+".expected");
//	        StreamUtils.copy(expectedJson, StandardCharsets.UTF_8, out2ex);
//
//	        Assertions.assertTrue(expectedML.contentEquals(msg.getValue()));
//			compareJson(expectedJson, data.getValue());
//	        
//	        return data.getValue();
//		}	

//	    private String loadML(String string) throws IOException {
//	        return StreamUtils.copyToString(AbstractMockSymphonyTest.class.getResourceAsStream(string), Charset.forName("UTF-8")).replace("\r\n", "\n");
//	    }
//
//	    private String loadJson(String string) throws IOException {
//	        return StreamUtils.copyToString(AbstractMockSymphonyTest.class.getResourceAsStream(string), Charset.forName("UTF-8"));
//	    }

		
	}

}
