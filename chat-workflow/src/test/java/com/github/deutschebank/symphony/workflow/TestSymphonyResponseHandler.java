package com.github.deutschebank.symphony.workflow;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.validation.Errors;

import com.github.deutschebank.symphony.workflow.Workflow;
import com.github.deutschebank.symphony.workflow.content.RoomDef;
import com.github.deutschebank.symphony.workflow.content.UserDef;
import com.github.deutschebank.symphony.workflow.fixture.TestOb3;
import com.github.deutschebank.symphony.workflow.fixture.TestObject;
import com.github.deutschebank.symphony.workflow.fixture.TestWorkflowConfig;
import com.github.deutschebank.symphony.workflow.form.Button;
import com.github.deutschebank.symphony.workflow.form.Button.Type;
import com.github.deutschebank.symphony.workflow.response.FormResponse;
import com.github.deutschebank.symphony.workflow.response.MessageResponse;
import com.github.deutschebank.symphony.workflow.sources.symphony.handlers.AttachmentHandler;
import com.github.deutschebank.symphony.workflow.sources.symphony.handlers.EntityJsonConverter;
import com.github.deutschebank.symphony.workflow.sources.symphony.handlers.FreemarkerFormMessageMLConverter;
import com.github.deutschebank.symphony.workflow.sources.symphony.handlers.SymphonyResponseHandler;
import com.github.deutschebank.symphony.workflow.sources.symphony.room.SymphonyRooms;
import com.github.deutschebank.symphony.workflow.validation.ErrorHelp;

public class TestSymphonyResponseHandler extends AbstractMockSymphonyTest {
	
	@MockBean
	SymphonyRooms rooms;
	
	SymphonyResponseHandler responseHandler;
	
	@Autowired
	EntityJsonConverter entityJsonConverter;
	
	@Autowired
	Workflow wf;
	
	@Autowired
	AttachmentHandler ah;
	
	@Autowired
	ResourceLoader rl;
	
	@Before
	public void setup() {
		responseHandler = new SymphonyResponseHandler(messagesApi, new FreemarkerFormMessageMLConverter(rooms, rl), entityJsonConverter, rooms, ah);
		responseHandler.setOutputTemplates(true);
	}
	
	@Test
	public void testSendMessage() {
		Mockito.when(messagesApi.v4StreamSidMessageCreatePost(Mockito.isNull(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.isNull(), Mockito.isNull(),Mockito.isNull(), Mockito.isNull()))
		.then(a -> {
			Assert.assertEquals(TestWorkflowConfig.room.getId(), a.getArgument(1));
			Assert.assertEquals("<messageML> - <hash tag=\"testobjects-workflow\" />  - <hash tag=\"symphony-workflow\" />  - <hash tag=\"com-db-symphonyp-workflow-testobject\" /> testing</messageML>", a.getArgument(2));
			Assert.assertEquals("{\"workflow_001\":{\"type\":\"com.db.symphonyp.workflow.testObject\",\"version\":\"1.0\",\"isin\":\"213\",\"bidAxed\":true,\"askAxed\":false,\"creator\":\"rob@here.com\",\"bidQty\":55,\"askQty\":22}}", a.getArgument(3));
			return null;
		});
		
		MessageResponse mr = new MessageResponse(wf, TestWorkflowConfig.room, new TestObject("213", true, false, "rob@here.com", 55, 22), "test name", "test instruction", "testing");
		responseHandler.accept(mr);
	}

	@Test
	public void testSendEmptyForm() {
		Mockito.when(messagesApi.v4StreamSidMessageCreatePost(Mockito.isNull(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.isNull(), Mockito.isNull(),Mockito.isNull(), Mockito.isNull()))
		.then(a -> {
			Assert.assertEquals(TestWorkflowConfig.room, a.getArgument(1));
			Assert.assertEquals("<messageML> - <hash tag=\"axes-workflow\" />  - <hash tag=\"symphony-workflow\" />"+
			" <form id=\"com.db.symphonyp.workflow.TestObject\" ><text-field name=\"isin.\" placeholder=\"isin\" ></text-field><checkbox name=\"bidAxed.\" value=\"true\" >bidAxed</checkbox><checkbox name=\"askAxed.\" value=\"true\" >askAxed</checkbox>"+
			"<text-field name=\"creator.\" placeholder=\"creator\" ></text-field><text-field name=\"bidQty.\" placeholder=\"bidQty\" ></text-field><text-field name=\"askQty.\" placeholder=\"askQty\" ></text-field>"
			+"<button name=\"OK\" type=\"action\" >Click me</button></form></messageML>", a.getArgument(2));
			Assert.assertEquals("{\"workflow_001\":{\"type\":\"com.db.symphonyp.workflow.testObject\",\"version\":\"1.0\",\"isin\":\"213\",\"bidAxed\":true,\"askAxed\":false,\"creator\":\"rob@here.com\",\"bidQty\":55,\"askQty\":22}}", a.getArgument(3));
			return null;
		});
		
		FormResponse fr = new FormResponse(wf, TestWorkflowConfig.room, null,  "test name", "test instruction", TestObject.class, true, Collections.singletonList(new Button("OK", Type.ACTION, "Click me")));
		responseHandler.accept(fr);
	}
	
	@Test
	public void testSendFormWithError() {
		Mockito.when(messagesApi.v4StreamSidMessageCreatePost(Mockito.isNull(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.isNull(), Mockito.isNull(),Mockito.isNull(), Mockito.isNull()))
		.then(a -> {
			Assert.assertEquals(TestWorkflowConfig.room, a.getArgument(1));
			Assert.assertEquals("<messageML> - <hash tag=\"axes-workflow\" />  - <hash tag=\"symphony-workflow\" />  - <hash tag=\"com-db-axes-axe\" /> testing</messageML>", a.getArgument(2));
			Assert.assertEquals("{\"workflow_001\":{\"type\":\"com.db.symphonyp.workflow.testObject\",\"version\":\"1.0\",\"isin\":\"213\",\"bidAxed\":true,\"askAxed\":false,\"creator\":\"rob@here.com\",\"bidQty\":55,\"askQty\":22}}", a.getArgument(3));
			return null;
		});
		
		TestObject a = new TestObject("213", true, false, "rob@here.com", 55, 22);
		Errors e= ErrorHelp.createErrorHolder();
		e.rejectValue("isin.", "32432");
		FormResponse fr = new FormResponse(wf, TestWorkflowConfig.room, null,  "test name", "test instruction", a, true, Collections.singletonList(new Button("OK", Type.ACTION, "Click me")), e);
		responseHandler.accept(fr);
	}
	
	@Test
	public void testSendWithNestedWorflowObjects() {
		Mockito.when(messagesApi.v4StreamSidMessageCreatePost(Mockito.isNull(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.isNull(), Mockito.isNull(),Mockito.isNull(), Mockito.isNull()))
		.then(a -> {
			Assert.assertEquals(TestWorkflowConfig.room, a.getArgument(1));
			Assert.assertEquals("<messageML> - <hash tag=\"axes-workflow\" />  - <hash tag=\"symphony-workflow\" />  - <hash tag=\"com-db-axes-axe\" /> testing</messageML>", a.getArgument(2));
			Assert.assertEquals("{\"workflow_001\":{\"type\":\"com.db.symphonyp.workflow.testObject\",\"version\":\"1.0\",\"isin\":\"213\",\"bidAxed\":true,\"askAxed\":false,\"creator\":\"rob@here.com\",\"bidQty\":55,\"askQty\":22}}", a.getArgument(3));
			return null;
		});
		
		TestOb3 a = new TestOb3(new RoomDef("abc", "asds", true, null), new UserDef(null, "Graham Bobki", "graham@goodle.com"), "some text");
		Errors e= ErrorHelp.createErrorHolder();
		e.rejectValue("isin.", "32432");
		FormResponse fr = new FormResponse(wf, TestWorkflowConfig.room, null,  "test name", "test instruction", a, true, Collections.singletonList(new Button("OK", Type.ACTION, "Click me")), e);
		responseHandler.accept(fr);
	}
}
