package org.finos.symphony.toolkit.workflow;

import static org.finos.symphony.toolkit.workflow.fixture.TestWorkflowConfig.room;
import static org.finos.symphony.toolkit.workflow.fixture.TestWorkflowConfig.u;

import java.util.Collections;
import java.util.Map;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.fixture.TestObject;
import org.finos.symphony.toolkit.workflow.fixture.TestObjects;
import org.finos.symphony.toolkit.workflow.fixture.TestWorkflowConfig;
import org.finos.symphony.toolkit.workflow.response.FormResponse;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.ElementsAction;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.FormConverter.UnconvertedContent;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.edit.TableAddRow;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.edit.TableDeleteRows;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.edit.TableEditRow;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.EntityJsonConverter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class TestTableEdit extends AbstractMockSymphonyTest {

	@Autowired
	Workflow wf;
	
	private TestObjects to;
	private EntityJson toWrapper;
	
	@Autowired
	TableEditRow editRow;
	
	@Autowired
	TableDeleteRows deleteRows;
	
	@Autowired
	TableAddRow addRows;
	
	EntityJsonConverter ejc; 
	
	
	@Before
	public void setup() {
		to = TestWorkflowConfig.createTestObjects();
		toWrapper = EntityJsonConverter.newWorkflow(to);
		ejc = new EntityJsonConverter(wf);
	}
	
	@Test
	public void testAddRow() {
		ElementsAction ea = new ElementsAction(wf, room, u, null, "items."+TableAddRow.ACTION_SUFFIX, toWrapper);
		FormResponse fr = (FormResponse) addRows.apply(ea).get(0);
		Assert.assertEquals(TestObject.class, fr.getFormClass());
		Assert.assertEquals("New Test Object", fr.getName());
		
		// ok, make changes and post back
		TestObject newTo = (TestObject) fr.getFormObject();
		newTo.setCreator("newb@thing.com");
		newTo.setIsin("isiny");
		newTo.setBidAxed(true);
		newTo.setBidQty(324);
		ea = new ElementsAction(wf, room, u, newTo, "items."+TableAddRow.DO_SUFFIX, toWrapper);
		fr = (FormResponse) addRows.apply(ea).get(0);
		TestObjects to = (TestObjects) ejc.readWorkflow(fr.getData());
		
		Assert.assertEquals(3, to.getItems().size()); 
		Assert.assertEquals(newTo, to.getItems().get(2)); 
	}
	
	@Test
	public void testEditRow() {
		ElementsAction ea = new ElementsAction(wf, room, u, null, "items.[0]."+TableEditRow.EDIT_SUFFIX, toWrapper);
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
		
		ea = new ElementsAction(wf, room, u, newTo, "items.[0]."+TableEditRow.UPDATE_SUFFIX, toWrapper);
		fr = (FormResponse) editRow.apply(ea).get(0);
		Assert.assertEquals(TestObjects.class, fr.getFormClass());
		TestObjects out = (TestObjects) fr.getFormObject();
		Assert.assertEquals(out.getItems().get(0), newTo);
	}
	
	@Test
	public void testDeleteRows() {
		Map<String, Object> selects = Collections.singletonMap("items", Collections.singletonList(Collections.singletonMap("selected", "true")));
		UnconvertedContent uc = new UnconvertedContent(TestObjects.class, selects);
		ElementsAction ea = new ElementsAction(wf, room, u, uc, "items."+TableDeleteRows.ACTION_SUFFIX, toWrapper);
		FormResponse fr = (FormResponse) deleteRows.apply(ea).get(0);
		Assert.assertEquals(TestObjects.class, fr.getFormClass());
		TestObjects formObject2 = (TestObjects) fr.getFormObject();
		Assert.assertEquals(1, formObject2.getItems().size());
	}
}
