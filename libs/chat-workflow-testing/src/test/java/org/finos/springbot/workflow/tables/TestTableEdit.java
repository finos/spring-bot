package org.finos.springbot.workflow.tables;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.finos.springbot.ChatWorkflowConfig;
import org.finos.springbot.entityjson.EntityJson;
import org.finos.springbot.tests.controller.TestObject;
import org.finos.springbot.tests.controller.TestObjects;
import org.finos.springbot.workflow.actions.FormAction;
import org.finos.springbot.workflow.actions.consumers.ActionConsumer;
import org.finos.springbot.workflow.actions.form.TableAddRow;
import org.finos.springbot.workflow.actions.form.TableDeleteRows;
import org.finos.springbot.workflow.actions.form.TableEditRow;
import org.finos.springbot.workflow.content.Chat;
import org.finos.springbot.workflow.content.User;
import org.finos.springbot.workflow.form.FormSubmission;
import org.finos.springbot.workflow.response.WorkResponse;
import org.finos.springbot.workflow.response.handlers.ResponseHandlers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.Validator;

/**
 * Although this lives inside symphony, it basically tests table editing for teams too.
 * @author rob@kite9.com
 *
 */
@SpringBootTest(classes = {
		ChatWorkflowConfig.class,
})
@ActiveProfiles(value = "symphony")
public class TestTableEdit {

	
	private TestObjects to;
	private EntityJson toWrapper;
	
	@MockBean
	Validator v;
	
	@Autowired
	TableEditRow editRow;
	
	@Autowired
	TableDeleteRows deleteRows;
	
	@Autowired
	TableAddRow addRows;

	@MockBean
	ResponseHandlers rh;
	
	Chat room = null;
	User u = null;

	public static TestObjects createTestObjects() {
		return new TestObjects(
			new ArrayList<>(Arrays.asList(
				new TestObject("83274239874", true, true, "rob@example.com", 234786, 2138),
				new TestObject("AUD274239874", true, false, "gregb@example.com", 2386, new BigDecimal("234823498.573")))));
	}

	
	@BeforeEach
	public void setup() {
		to = createTestObjects();
		toWrapper = new EntityJson();
		toWrapper.put(WorkResponse.OBJECT_KEY, to);
	}
	
	@Test
	public void testAddRow() {
		FormAction ea = new FormAction(room, u, null, "items."+TableAddRow.ACTION_SUFFIX, toWrapper);
		WorkResponse fr = postAndGetResponse(ea, addRows);
		Map<String, Object> returnedData = fr.getData();
		
		Assertions.assertEquals(TestObject.class, fr.getFormClass());
		
		// ok, make changes and post back
		TestObject newTo = (TestObject) fr.getFormObject();
		newTo.setCreator("newb@thing.com");
		newTo.setIsin("isiny");
		newTo.setBidAxed(true);
		newTo.setBidQty(324);
		ea = new FormAction(room, u, newTo, "items."+TableAddRow.DO_SUFFIX, returnedData);
		
		fr = postAndGetResponse(ea, addRows);

		TestObjects to = (TestObjects) fr.getData().get(WorkResponse.OBJECT_KEY);
		Assertions.assertEquals(3, to.getItems().size()); 
		Assertions.assertEquals(newTo, to.getItems().get(2)); 
	}


	private WorkResponse postAndGetResponse(FormAction ea, ActionConsumer ac) {
		ArgumentCaptor<WorkResponse> wr = ArgumentCaptor.forClass(WorkResponse.class);
		ac.accept(ea);
		Mockito.verify(rh).accept(wr.capture());
		WorkResponse fr = wr.getValue();
		Mockito.reset(rh);
		return fr;
	}
	
	@Test
	public void testEditRow() {
		FormAction ea = new FormAction(room, u, null, "items.[0]."+TableEditRow.EDIT_SUFFIX, toWrapper);
		WorkResponse fr = postAndGetResponse(ea, editRow);
		Assertions.assertEquals(TestObject.class, fr.getFormClass());
		TestObject formObject2 = (TestObject) fr.getFormObject();
		Assertions.assertEquals(to.getItems().get(0), formObject2);
		Map<String, Object> returnedData = fr.getData();

		
		TestObject newTo = new TestObject();
		newTo.setCreator("newb@thing.com");
		newTo.setIsin("isiny");
		newTo.setBidAxed(true);
		newTo.setBidQty(324);
		
		ea = new FormAction(room, u, newTo, "items.[0]."+TableEditRow.UPDATE_SUFFIX, returnedData);
		fr = postAndGetResponse(ea, editRow);
		Assertions.assertEquals(TestObjects.class, fr.getFormClass());
		TestObjects out = (TestObjects) fr.getFormObject();
		Assertions.assertEquals(out.getItems().get(0), newTo);
	}
	
	@Test
	public void testDeleteRows() {
		Map<String, Object> selects = Collections.singletonMap("items", Collections.singletonList(Collections.singletonMap("selected", "true")));
		FormSubmission uc = new FormSubmission("?", selects);
		FormAction ea = new FormAction(room, u, uc, "items."+TableDeleteRows.ACTION_SUFFIX, toWrapper);
		WorkResponse fr = postAndGetResponse(ea, deleteRows);
		Assertions.assertEquals(TestObjects.class, fr.getFormClass());
		TestObjects formObject2 = (TestObjects) fr.getFormObject();
		Assertions.assertEquals(1, formObject2.getItems().size());
	}
}