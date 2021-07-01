package org.finos.symphony.toolkit.workflow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.content.HashTagDef;
import org.finos.symphony.toolkit.workflow.fixture.TestCollections;
import org.finos.symphony.toolkit.workflow.fixture.TestCollections.Choice;
import org.finos.symphony.toolkit.workflow.fixture.TestCollections.MiniBean;
import org.finos.symphony.toolkit.workflow.form.Button;
import org.finos.symphony.toolkit.workflow.form.Button.Type;
import org.finos.symphony.toolkit.workflow.form.ButtonList;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.EntityJsonConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.FormMessageMLConverter;
import org.finos.symphony.toolkit.workflow.validation.ErrorHelp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;
import org.springframework.validation.Validator;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

public class TestFormCollectionsMessageML extends AbstractMockSymphonyTest {

	@Autowired
	FormMessageMLConverter messageMlConverter;
	
	@Autowired
	Validator validator;

	@Autowired
	EntityJsonConverter ejc;

	
	
	private String loadML(String string) throws IOException {
		return StreamUtils.copyToString(TestFormCollectionsMessageML.class.getResourceAsStream(string), Charset.forName("UTF-8")).replace("\r\n", "\n");
	}

	private String loadJson(String string) throws IOException {
		return StreamUtils.copyToString(TestFormCollectionsMessageML.class.getResourceAsStream(string), Charset.forName("UTF-8"));
	}

	private TestCollections createTestCollections() {
		MiniBean mb1 = new MiniBean("A String", 4973, Arrays.asList("Amsidh", "Rob"));
		MiniBean mb2 = new MiniBean("Another String", 45, Arrays.asList("Terry", "James"));
		MiniBean mb3 = new MiniBean("Thing 3", 8787, null);
		
		TestCollections out = new TestCollections(
				Arrays.asList("a", "b", "c"), 
				Arrays.asList(Choice.A, Choice.B), 
				Arrays.asList(mb1, mb2, mb3),
				Arrays.asList(new HashTagDef("abc"), new HashTagDef("def")));
		return out;
	}
	

	@Test
	public void testCollectionsEditMessageML() throws Exception {

		TestCollections c = createTestCollections();

		Button submit = new Button("submit", Type.ACTION, "GO");

		// can we convert to messageML? (something populated)
		EntityJson empty = new EntityJson();
		String out = messageMlConverter.convert(TestCollections.class, c, ButtonList.of(submit), true,
				ErrorHelp.createErrorHolder(), empty);
		String json = ejc.writeValue(empty);
		System.out.println("<messageML>" + out + "</messageML>\n"+json);
		Assertions.assertEquals(loadML("testCollectionsEditMessageML.ml"), out);
		compareJson(loadJson("testCollectionsEditMessageML.json"), json); 
	}

	@Test
	public void testCollectionsViewMessageML() throws Exception {

		TestCollections c = createTestCollections();

		Button submit = new Button("submit", Type.ACTION, "GO");

		// can we convert to messageML? (something populated)
		EntityJson empty = new EntityJson();
		String out = messageMlConverter.convert(TestCollections.class, c, ButtonList.of(submit), false,
				ErrorHelp.createErrorHolder(), empty);
		String json = ejc.writeValue(empty);
		System.out.println("<messageML>" + out + "</messageML>\n"+json);
		Assertions.assertEquals(loadML("testCollectionsViewMessageML.ml"), out);
		compareJson(loadJson("testCollectionsViewMessageML.json"), json); 
	}
	
	private void compareJson(String loadJson, String json) throws JsonMappingException, JsonProcessingException {
		ObjectMapper om = new ObjectMapper();
		Assertions.assertEquals(om.readTree(loadJson), om.readTree(json));
	}
}
