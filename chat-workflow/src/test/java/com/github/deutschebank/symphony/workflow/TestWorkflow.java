package com.github.deutschebank.symphony.workflow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.deutschebank.symphony.workflow.Workflow;
import com.github.deutschebank.symphony.workflow.fixture.TestObject;
import com.github.deutschebank.symphony.workflow.fixture.TestObjects;
import com.github.deutschebank.symphony.workflow.fixture.TestWorkflowConfig;
import com.github.deutschebank.symphony.workflow.response.FormResponse;
import com.github.deutschebank.symphony.workflow.response.Response;
import com.github.deutschebank.symphony.workflow.room.Rooms;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {  TestWorkflowConfig.class })
public class TestWorkflow {

	
	@Autowired
	Workflow wf;
	
	@MockBean
	Rooms r;
	
	
	@Test
	public void testHistoricMethodCall() {
		List<Response> r = wf.applyCommand(TestWorkflowConfig.u, TestWorkflowConfig.room, "wrap", null, null);
		Assert.assertEquals(FormResponse.class, r.get(0).getClass());
		TestObjects expected = new TestObjects(Collections.singletonList(TestWorkflowConfig.INITIAL.getItems().get(1)));
		Assert.assertEquals(expected, ((FormResponse) r.get(0)).getFormObject());
		Assert.assertEquals(false, ((FormResponse) r.get(0)).isEditable());	
	}
	
	@Test
	public void testStaticMethodCall() {
		List<Response> r = wf.applyCommand(TestWorkflowConfig.u, TestWorkflowConfig.room, "testObjects", null, null);
		
		Assert.assertEquals(FormResponse.class, r.get(0).getClass());
		Assert.assertEquals(TestWorkflowConfig.INITIAL, ((FormResponse) r.get(0)).getFormObject());
		Assert.assertEquals(false, ((FormResponse) r.get(0)).isEditable());	
	}
	
	@Test
	public void testParameterizedMethodCall() {
		List<Response> r = wf.applyCommand(TestWorkflowConfig.u, TestWorkflowConfig.room, "add", null, null);
		
		Assert.assertEquals(FormResponse.class, r.get(0).getClass());
		Assert.assertEquals(null, ((FormResponse) r.get(0)).getFormObject());
		Assert.assertEquals(TestObject.class, ((FormResponse) r.get(0)).getFormClass());
		Assert.assertEquals(true, ((FormResponse) r.get(0)).isEditable());	
	}
	
	@Test
	public void testParameterizedMethodCallWithArgument() {
		TestObject argument = new TestObject("dj", true, false, "me@rob.com", 23324323, 0);
		List<Response> r = wf.applyCommand(TestWorkflowConfig.u, TestWorkflowConfig.room, "add", argument, null);
		
		List<TestObject> all = new ArrayList<TestObject>();
		all.addAll(TestWorkflowConfig.INITIAL.getItems());
		all.add(argument);
		TestObjects expected = new TestObjects(all);

		
		Assert.assertEquals(FormResponse.class, r.get(0).getClass());
		Assert.assertEquals(expected, ((FormResponse) r.get(0)).getFormObject());
		Assert.assertEquals(TestObjects.class, ((FormResponse) r.get(0)).getFormClass());
		Assert.assertEquals(false, ((FormResponse) r.get(0)).isEditable());	
	}
	
}
