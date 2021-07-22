package org.finos.symphony.toolkit.workflow;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.Arrays;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.annotations.WorkMode;
import org.finos.symphony.toolkit.workflow.fixture.WeirdObject;
import org.finos.symphony.toolkit.workflow.fixture.WeirdObject.Choice;
import org.finos.symphony.toolkit.workflow.fixture.WeirdObjectCollection;
import org.finos.symphony.toolkit.workflow.fixture.TestObject;
import org.finos.symphony.toolkit.workflow.fixture.TestObjects;
import org.finos.symphony.toolkit.workflow.fixture.TestTemplatedObject;
import org.finos.symphony.toolkit.workflow.form.Button;
import org.finos.symphony.toolkit.workflow.form.Button.Type;
import org.finos.symphony.toolkit.workflow.form.ButtonList;
import org.finos.symphony.toolkit.workflow.response.WorkResponse;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.HashTag;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyRoom;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyUser;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.ElementsHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.ErrorHelp;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.FormMessageMLConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.json.EntityJsonConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

public class TestFormMessageML extends AbstractMockSymphonyTest {

	@Autowired
	FormMessageMLConverter messageMlConverter;
	
	@Autowired
	Validator validator;

	@Autowired
	EntityJsonConverter ejc;

	@Test
	public void testFreemarkerView() throws Exception {
		TestTemplatedObject to4 = new TestTemplatedObject();
		to4.setSomeText("howdy");
		SymphonyRoom theRoom = new SymphonyRoom("tesxt room", "abc123");
		to4.setR(theRoom);
		
		WorkResponse wr = createWorkAddSubmit(WorkMode.VIEW, to4);
		testTemplating(wr, "abc123", "testFreemarkerView.ml", "testFreemarkerView.json");
	}
	
	
	private String loadML(String string) throws IOException {
		return StreamUtils.copyToString(TestFormMessageML.class.getResourceAsStream(string), Charset.forName("UTF-8")).replace("\r\n", "\n");
	}

	private String loadJson(String string) throws IOException {
		return StreamUtils.copyToString(TestFormMessageML.class.getResourceAsStream(string), Charset.forName("UTF-8"));
	}

	@Test
	public void testNewWeirdFieldsEdit() throws Exception {
		WorkResponse wr = createWeirdFieldsWorkResponse(WorkMode.EDIT);
		testTemplating(wr, "abc123", "testNewWeirdFieldsEdit.ml", "testNewWeirdFieldsEdit.json");
	}


	protected WorkResponse createWeirdFieldsWorkResponse(WorkMode wm) {
		SymphonyRoom theRoom = new SymphonyRoom("tesxt room", "abc123");

		WeirdObject to4 = new WeirdObject();
		to4.setTheId(new HashTag("adf360dd-06fe-43a4-9a62-2c17fe2deefa"));
		to4.setC(Choice.C);
		
		Button submit = new Button("submit", Type.ACTION, "GO");
		
		WorkResponse wr = new WorkResponse(theRoom, to4, wm);
		ButtonList bl = (ButtonList) wr.getData().get(ButtonList.KEY);
		bl.add(submit);
		return wr;
	}
	
	
	@Test
	public void testNewWeirdFieldsView() throws Exception {
		WorkResponse wr = createWeirdFieldsWorkResponse(WorkMode.VIEW);
		testTemplating(wr, "abc123", "testNewWeirdFieldsView.ml", "testNewWeirdFieldsView.json");
	}
	
	
	protected WorkResponse createNestedWeirdFieldsWorkResponse(WorkMode wm) {
		SymphonyUser someUser = new SymphonyUser(2678l, "bob", "bob@example.com");
		
		WeirdObject to4 = new WeirdObject();
		to4.setB(true);
		to4.setC(Choice.B);
		to4.setSomeUser(someUser);
		to4.setTheId(new HashTag("adf360dd-06fe-43a4-9a62-2c17fe2deefa"));
		
		WeirdObjectCollection ob5 = new WeirdObjectCollection();
		ob5.setOb4(to4);
				
		WorkResponse wr = createWorkAddSubmit(wm, ob5);
		return wr;
	}


	@Test
	public void testNestedWeirdFieldsEdit() throws Exception {
		WorkResponse wr = createWeirdFieldsWorkResponse(WorkMode.EDIT);
		testTemplating(wr, "abc123", "testNestedWeirdFieldsEdit.ml", "testNestedWeirdFieldsEdit.json");
	}
	
	@Test
	public void testNestedWeirdFieldsView() throws Exception {
		WorkResponse wr = createWeirdFieldsWorkResponse(WorkMode.VIEW);
		testTemplating(wr, "abc123", "testNestedWeirdFieldsView.ml", "testNestedWeirdFieldsView.json");
	}
	
	protected WorkResponse createAxeForm(WorkMode wm) {
		TestObject a = new TestObject("83274239874", true, true, "rob@example.com", 234786, 2138);
		return createWorkAddSubmit(wm, a);
	}
	
	@Test
	public void testAxeFormEditMessageML() throws Exception {
		WorkResponse wr = createAxeForm(WorkMode.EDIT);
		testTemplating(wr, "abc123", "testAxeFormEditMessageML.ml", "testAxeFormEditMessageML.json");
	}

	@Test
	public void testAxeFormViewMessageML() throws Exception {
		WorkResponse wr = createAxeForm(WorkMode.VIEW);
		testTemplating(wr, "abc123", "testAxeFormViewMessageML.ml", "testAxeFormViewMessageML.json");
	}
	
	protected WorkResponse createAxesWorkResponse(WorkMode wm) {
		TestObject a1 = new TestObject("83274239874", true, true, "rob@example.com", 234786, 2138);
		TestObject a2 = new TestObject("AUD274239874", true, false, "gregb@example.com", 2386, new BigDecimal("234823498.573"));
		TestObjects a = new TestObjects(Arrays.asList(a1, a2));
		WorkResponse wr = createWorkAddSubmit(wm, a);
		return wr;
	}

	@Test
	public void testAxesTableEditMessageML() throws Exception {
		WorkResponse wr = createAxesWorkResponse(WorkMode.EDIT);
		testTemplating(wr, "abc123", "testAxesTableEditMessageML.ml", "testAxesTableEditMessageML.json");
	}

	@Test
	public void testAxesTableViewMessageML() throws Exception {
		WorkResponse wr = createAxesWorkResponse(WorkMode.VIEW);
		testTemplating(wr, "abc123", "testAxesTableViewMessageML.ml", "testAxesTableViewMessageML.json");

	}

	@Test
	public void testValidation() throws Exception {
		TestObject a = new TestObject("83274239874", true, true, "rob", 234786, 2138);
		SymphonyRoom theRoom = new SymphonyRoom("tesxt room", "abc123");
		
		ButtonList bl = new ButtonList();
		Button submit = new Button("submit", Type.ACTION, "GO");
		bl.add(submit);

		Errors eh = ErrorHelp.createErrorHolder();
		validator.validate(a, eh);
		
		WorkResponse wr = new WorkResponse(theRoom, a, WorkMode.EDIT, bl, ElementsHandler.convertErrorsToMap(eh));

		testTemplating(wr, "abc123", "testValidation.ml", "testValidation.json");

	}


}
