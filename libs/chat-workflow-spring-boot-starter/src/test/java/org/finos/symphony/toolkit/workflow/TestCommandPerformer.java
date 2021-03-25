package org.finos.symphony.toolkit.workflow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.fixture.TestObject;
import org.finos.symphony.toolkit.workflow.fixture.TestObjects;
import org.finos.symphony.toolkit.workflow.fixture.TestWorkflowConfig;
import org.finos.symphony.toolkit.workflow.response.FormResponse;
import org.finos.symphony.toolkit.workflow.response.Response;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.ElementsAction;
import org.finos.symphony.toolkit.workflow.sources.symphony.room.SymphonyRooms;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)

public class TestCommandPerformer extends AbstractMockSymphonyTest {

	
	@Autowired
	Workflow wf;
	
	@MockBean
	SymphonyRooms r;
	
	@Autowired
	CommandPerformer cp;
	
	@Test
	public void testHistoricMethodCall() {
		ElementsAction sma = new ElementsAction(wf, TestWorkflowConfig.room, TestWorkflowConfig.u, null,  "wrap", new EntityJson());
		List<Response> r = cp.applyCommand("wrap", sma);
		Assertions.assertEquals(FormResponse.class, r.get(0).getClass());
		TestObjects expected = new TestObjects(Collections.singletonList(TestWorkflowConfig.INITIAL_TEST_OBJECT));
		Assertions.assertEquals(expected, ((FormResponse) r.get(0)).getFormObject());
		Assertions.assertEquals(false, ((FormResponse) r.get(0)).isEditable());	
	}
	
	@Test
	public void testStaticMethodCall() {
		ElementsAction sma = new ElementsAction(wf, TestWorkflowConfig.room, TestWorkflowConfig.u, null,  "testObjects", new EntityJson());
		List<Response> r = cp.applyCommand("testObjects", sma);
		Assertions.assertEquals(FormResponse.class, r.get(0).getClass());
		Assertions.assertEquals(TestWorkflowConfig.INITIAL_TEST_OBJECTS, ((FormResponse) r.get(0)).getFormObject());
		Assertions.assertEquals(false, ((FormResponse) r.get(0)).isEditable());	
	}
	
	@Test
	public void testParameterizedMethodCall() {
		ElementsAction sma = new ElementsAction(wf, TestWorkflowConfig.room, TestWorkflowConfig.u, null,  "add", new EntityJson());
		List<Response> r = cp.applyCommand("add", sma);
		
		Assertions.assertEquals(FormResponse.class, r.get(0).getClass());
		Assertions.assertNotNull(((FormResponse) r.get(0)).getFormObject());
		Assertions.assertEquals(TestObject.class, ((FormResponse) r.get(0)).getFormClass());
		Assertions.assertEquals(true, ((FormResponse) r.get(0)).isEditable());	
	}
	
	@Test
	public void testParameterizedMethodCallWithArgument() {
		TestObject argument = new TestObject("dj", true, false, "me@rob.com", 23324323, 0);
		ElementsAction sma = new ElementsAction(wf, TestWorkflowConfig.room, TestWorkflowConfig.u, argument,  "add", new EntityJson());

		List<Response> r = cp.applyCommand("add", sma);
		
		List<TestObject> all = new ArrayList<TestObject>();
		all.addAll(TestWorkflowConfig.INITIAL_TEST_OBJECTS.getItems());
		all.add(argument);
		TestObjects expected = new TestObjects(all);

		
		Assertions.assertEquals(FormResponse.class, r.get(0).getClass());
		Assertions.assertEquals(expected, ((FormResponse) r.get(0)).getFormObject());
		Assertions.assertEquals(TestObjects.class, ((FormResponse) r.get(0)).getFormClass());
		Assertions.assertEquals(false, ((FormResponse) r.get(0)).isEditable());	
	}
	
}
