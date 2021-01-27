package org.finos.symphony.toolkit.workflow;

import java.util.List;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.content.RoomDef;
import org.finos.symphony.toolkit.workflow.content.UserDef;
import org.finos.symphony.toolkit.workflow.fixture.TestOb3;
import org.finos.symphony.toolkit.workflow.fixture.TestObject;
import org.finos.symphony.toolkit.workflow.fixture.TestWorkflowConfig;
import org.finos.symphony.toolkit.workflow.form.Button;
import org.finos.symphony.toolkit.workflow.form.Button.Type;
import org.finos.symphony.toolkit.workflow.form.ButtonList;
import org.finos.symphony.toolkit.workflow.response.FormResponse;
import org.finos.symphony.toolkit.workflow.response.MessageResponse;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.AttachmentHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.EntityJsonConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.SymphonyResponseHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.FieldConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.FreemarkerFormMessageMLConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.room.SymphonyRooms;
import org.finos.symphony.toolkit.workflow.validation.ErrorHelp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.validation.Errors;

public class TestSymphonyResponseHandler extends AbstractMockSymphonyTest {
	
	@MockBean
	SymphonyRooms rooms;
	
	@Autowired
	SymphonyResponseHandler responseHandler;
	
	@Autowired
	EntityJsonConverter entityJsonConverter;
	
	@Autowired
	Workflow wf;
	
	@Autowired
	AttachmentHandler ah;
	
	@Autowired
	ResourceLoader rl;
	
	@Autowired
	List<FieldConverter> fieldConverters;
	
	@BeforeEach
	public void setup() {
		responseHandler = new SymphonyResponseHandler(messagesApi, new FreemarkerFormMessageMLConverter(rl, fieldConverters), entityJsonConverter, rooms, ah);
		responseHandler.setOutputTemplates(true);
	}
	
	@Test
	public void testSendMessage() {
		Mockito.when(messagesApi.v4StreamSidMessageCreatePost(Mockito.isNull(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.isNull(), Mockito.isNull(),Mockito.isNull(), Mockito.isNull()))
		.then(a -> {
			Assertions.assertEquals(TestWorkflowConfig.room.getId(), a.getArgument(1));
			Assertions.assertEquals("<messageML> - <hash tag=\"testobjects-workflow\" />  - <hash tag=\"symphony-workflow\" />  - <hash tag=\"com-db-symphonyp-workflow-testobject\" /> testing</messageML>", a.getArgument(2));
			Assertions.assertEquals("{\"workflow_001\":{\"type\":\"com.db.symphonyp.workflow.testObject\",\"version\":\"1.0\",\"isin\":\"213\",\"bidAxed\":true,\"askAxed\":false,\"creator\":\"rob@here.com\",\"bidQty\":55,\"askQty\":22}}", a.getArgument(3));
			return null;
		});
		
		MessageResponse mr = new MessageResponse(wf, TestWorkflowConfig.room, EntityJsonConverter.newWorkflow(new TestObject("213", true, false, "rob@here.com", 55, 22)), "test name", "test instruction", "testing");
		responseHandler.accept(mr);
	}

	@Test
	public void testSendEmptyForm() {
		Mockito.when(messagesApi.v4StreamSidMessageCreatePost(Mockito.isNull(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.isNull(), Mockito.isNull(),Mockito.isNull(), Mockito.isNull()))
		.then(a -> {
			Assertions.assertEquals(TestWorkflowConfig.room, a.getArgument(1));
			Assertions.assertEquals("<messageML> - <hash tag=\"axes-workflow\" />  - <hash tag=\"symphony-workflow\" />"+
			" <form id=\"com.db.symphonyp.workflow.TestObject\" ><text-field name=\"isin.\" placeholder=\"isin\" ></text-field><checkbox name=\"bidAxed.\" value=\"true\" >bidAxed</checkbox><checkbox name=\"askAxed.\" value=\"true\" >askAxed</checkbox>"+
			"<text-field name=\"creator.\" placeholder=\"creator\" ></text-field><text-field name=\"bidQty.\" placeholder=\"bidQty\" ></text-field><text-field name=\"askQty.\" placeholder=\"askQty\" ></text-field>"
			+"<button name=\"OK\" type=\"action\" >Click me</button></form></messageML>", a.getArgument(2));
			Assertions.assertEquals("{\"workflow_001\":{\"type\":\"com.db.symphonyp.workflow.testObject\",\"version\":\"1.0\",\"isin\":\"213\",\"bidAxed\":true,\"askAxed\":false,\"creator\":\"rob@here.com\",\"bidQty\":55,\"askQty\":22}}", a.getArgument(3));
			return null;
		});
		
		FormResponse fr = new FormResponse(wf, TestWorkflowConfig.room, new EntityJson(),  "test name", "test instruction", TestObject.class, true, ButtonList.of(new Button("OK", Type.ACTION, "Click me")));
		responseHandler.accept(fr);
	}
	
	@Test
	public void testSendFormWithError() {
		Mockito.when(messagesApi.v4StreamSidMessageCreatePost(Mockito.isNull(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.isNull(), Mockito.isNull(),Mockito.isNull(), Mockito.isNull()))
		.then(a -> {
			Assertions.assertEquals(TestWorkflowConfig.room, a.getArgument(1));
			Assertions.assertEquals("<messageML> - <hash tag=\"axes-workflow\" />  - <hash tag=\"symphony-workflow\" />  - <hash tag=\"com-db-axes-axe\" /> testing</messageML>", a.getArgument(2));
			Assertions.assertEquals("{\"workflow_001\":{\"type\":\"com.db.symphonyp.workflow.testObject\",\"version\":\"1.0\",\"isin\":\"213\",\"bidAxed\":true,\"askAxed\":false,\"creator\":\"rob@here.com\",\"bidQty\":55,\"askQty\":22}}", a.getArgument(3));
			return null;
		});
		
		TestObject a = new TestObject("213", true, false, "rob@here.com", 55, 22);
		Errors e= ErrorHelp.createErrorHolder();
		e.rejectValue("isin.", "32432");
		FormResponse fr = new FormResponse(wf, TestWorkflowConfig.room, new EntityJson(),  "test name", "test instruction", a, true, ButtonList.of(new Button("OK", Type.ACTION, "Click me")), e);
		responseHandler.accept(fr);
	}
	
	@Test
	public void testSendWithNestedWorflowObjects() {
		Mockito.when(messagesApi.v4StreamSidMessageCreatePost(Mockito.isNull(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.isNull(), Mockito.isNull(),Mockito.isNull(), Mockito.isNull()))
		.then(a -> {
			Assertions.assertEquals(TestWorkflowConfig.room, a.getArgument(1));
			Assertions.assertEquals("<messageML> - <hash tag=\"axes-workflow\" />  - <hash tag=\"symphony-workflow\" />  - <hash tag=\"com-db-axes-axe\" /> testing</messageML>", a.getArgument(2));
			Assertions.assertEquals("{\"workflow_001\":{\"type\":\"com.db.symphonyp.workflow.testObject\",\"version\":\"1.0\",\"isin\":\"213\",\"bidAxed\":true,\"askAxed\":false,\"creator\":\"rob@here.com\",\"bidQty\":55,\"askQty\":22}}", a.getArgument(3));
			return null;
		});
		
		TestOb3 a = new TestOb3(new RoomDef("abc", "asds", true, null), new UserDef(null, "Graham Bobki", "graham@goodle.com"), "some text");
		Errors e= ErrorHelp.createErrorHolder();
		e.rejectValue("isin.", "32432");
		FormResponse fr = new FormResponse(wf, TestWorkflowConfig.room, new EntityJson(),  "test name", "test instruction", a, true, ButtonList.of(new Button("OK", Type.ACTION, "Click me")), e);
		responseHandler.accept(fr);
	}
}
