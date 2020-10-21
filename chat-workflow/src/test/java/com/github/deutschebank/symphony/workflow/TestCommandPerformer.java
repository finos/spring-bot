package com.github.deutschebank.symphony.workflow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.deutschebank.symphony.json.EntityJson;
import com.github.deutschebank.symphony.workflow.fixture.TestObject;
import com.github.deutschebank.symphony.workflow.fixture.TestObjects;
import com.github.deutschebank.symphony.workflow.fixture.TestWorkflowConfig;
import com.github.deutschebank.symphony.workflow.response.FormResponse;
import com.github.deutschebank.symphony.workflow.response.Response;
import com.github.deutschebank.symphony.workflow.sources.symphony.elements.ElementsAction;
import com.github.deutschebank.symphony.workflow.sources.symphony.room.SymphonyRooms;

@RunWith(SpringRunner.class)
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
		Assert.assertEquals(FormResponse.class, r.get(0).getClass());
		TestObjects expected = new TestObjects(Collections.singletonList(TestWorkflowConfig.INITIAL_TEST_OBJECTS.getItems().get(1)));
		Assert.assertEquals(expected, ((FormResponse) r.get(0)).getFormObject());
		Assert.assertEquals(false, ((FormResponse) r.get(0)).isEditable());	
	}
	
	@Test
	public void testStaticMethodCall() {
		ElementsAction sma = new ElementsAction(wf, TestWorkflowConfig.room, TestWorkflowConfig.u, null,  "testObjects", new EntityJson());
		List<Response> r = cp.applyCommand("testObjects", sma);
		Assert.assertEquals(FormResponse.class, r.get(0).getClass());
		Assert.assertEquals(TestWorkflowConfig.INITIAL_TEST_OBJECTS, ((FormResponse) r.get(0)).getFormObject());
		Assert.assertEquals(false, ((FormResponse) r.get(0)).isEditable());	
	}
	
	@Test
	public void testParameterizedMethodCall() {
		ElementsAction sma = new ElementsAction(wf, TestWorkflowConfig.room, TestWorkflowConfig.u, null,  "add", new EntityJson());
		List<Response> r = cp.applyCommand("add", sma);
		
		Assert.assertEquals(FormResponse.class, r.get(0).getClass());
		Assert.assertNotNull(((FormResponse) r.get(0)).getFormObject());
		Assert.assertEquals(TestObject.class, ((FormResponse) r.get(0)).getFormClass());
		Assert.assertEquals(true, ((FormResponse) r.get(0)).isEditable());	
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

		
		Assert.assertEquals(FormResponse.class, r.get(0).getClass());
		Assert.assertEquals(expected, ((FormResponse) r.get(0)).getFormObject());
		Assert.assertEquals(TestObjects.class, ((FormResponse) r.get(0)).getFormClass());
		Assert.assertEquals(false, ((FormResponse) r.get(0)).isEditable());	
	}
	
}
