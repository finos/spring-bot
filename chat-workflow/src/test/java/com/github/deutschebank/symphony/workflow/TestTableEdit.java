package com.github.deutschebank.symphony.workflow;

import static com.github.deutschebank.symphony.workflow.fixture.TestWorkflowConfig.room;
import static com.github.deutschebank.symphony.workflow.fixture.TestWorkflowConfig.u;

import java.util.Collections;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.github.deutschebank.symphony.workflow.Workflow;
import com.github.deutschebank.symphony.workflow.fixture.TestObject;
import com.github.deutschebank.symphony.workflow.fixture.TestObjects;
import com.github.deutschebank.symphony.workflow.fixture.TestWorkflowConfig;
import com.github.deutschebank.symphony.workflow.response.FormResponse;
import com.github.deutschebank.symphony.workflow.sources.symphony.elements.ElementsAction;
import com.github.deutschebank.symphony.workflow.sources.symphony.elements.FormConverter.UnconvertedContent;
import com.github.deutschebank.symphony.workflow.sources.symphony.elements.edit.TableAddRow;
import com.github.deutschebank.symphony.workflow.sources.symphony.elements.edit.TableDeleteRows;
import com.github.deutschebank.symphony.workflow.sources.symphony.elements.edit.TableEditRow;

public class TestTableEdit extends AbstractMockSymphonyTest {

	@Autowired
	Workflow wf;
	
	private TestObjects to;
	
	@Autowired
	TableEditRow editRow;
	
	@Autowired
	TableDeleteRows deleteRows;
	
	@Autowired
	TableAddRow addRows;
	
	
	
	@Before
	public void setup() {
		to = TestWorkflowConfig.createTestObjects();
	}
	
	@Test
	public void testAddRow() {
		ElementsAction ea = new ElementsAction(wf, room, u, null, "items."+TableAddRow.ACTION_SUFFIX, to);
		FormResponse fr = (FormResponse) addRows.apply(ea).get(0);
		Assert.assertEquals(TestObject.class, fr.getFormClass());
		Assert.assertEquals("New Test Object", fr.getName());
		
		// ok, make changes and post back
		TestObject newTo = (TestObject) fr.getFormObject();
		newTo.setCreator("newb@thing.com");
		newTo.setIsin("isiny");
		newTo.setBidAxed(true);
		newTo.setBidQty(324);
		ea = new ElementsAction(wf, room, u, newTo, "items."+TableAddRow.DO_SUFFIX, to);
		fr = (FormResponse) addRows.apply(ea).get(0);
		
		Assert.assertEquals(3, ((TestObjects) fr.getData()).getItems().size()); 
		Assert.assertEquals(newTo, ((TestObjects) fr.getData()).getItems().get(2)); 
	}
	
	@Test
	public void testEditRow() {
		ElementsAction ea = new ElementsAction(wf, room, u, null, "items.[0]."+TableEditRow.EDIT_SUFFIX, to);
		FormResponse fr = (FormResponse) editRow.apply(ea).get(0);
		Assert.assertEquals(TestObject.class, fr.getFormClass());
		Assert.assertEquals("Edit Test Object", fr.getName());
		TestObject formObject2 = (TestObject) fr.getFormObject();
		Assert.assertEquals(to.getItems().get(0), formObject2);
		
		TestObject newTo = new TestObject();
		newTo.setCreator("newb@thing.com");
		newTo.setIsin("isiny");
		newTo.setBidAxed(true);
		newTo.setBidQty(324);
		
		ea = new ElementsAction(wf, room, u, newTo, "items.[0]."+TableEditRow.UPDATE_SUFFIX, to);
		fr = (FormResponse) editRow.apply(ea).get(0);
		Assert.assertEquals(TestObjects.class, fr.getFormClass());
		TestObjects out = (TestObjects) fr.getFormObject();
		Assert.assertEquals(out.getItems().get(0), newTo);
	}
	
	@Test
	public void testDeleteRows() {
		Map<String, Object> selects = Collections.singletonMap("items", Collections.singletonList(Collections.singletonMap("selected", "true")));
		UnconvertedContent uc = new UnconvertedContent(TestObjects.class, selects);
		ElementsAction ea = new ElementsAction(wf, room, u, uc, "items."+TableDeleteRows.ACTION_SUFFIX, to);
		FormResponse fr = (FormResponse) deleteRows.apply(ea).get(0);
		Assert.assertEquals(TestObjects.class, fr.getFormClass());
		TestObjects formObject2 = (TestObjects) fr.getFormObject();
		Assert.assertEquals(1, formObject2.getItems().size());
	}
}
