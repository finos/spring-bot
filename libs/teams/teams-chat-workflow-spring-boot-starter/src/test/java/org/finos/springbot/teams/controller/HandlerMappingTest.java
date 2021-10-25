package org.finos.springbot.teams.controller;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.finos.springbot.teams.MockTeamsConfiguration;
import org.finos.springbot.teams.TeamsWorkflowConfig;
import org.finos.springbot.teams.content.TeamsChat;
import org.finos.springbot.teams.messages.MessageActivityHandler;
import org.finos.springbot.teams.turns.CurrentTurnContext;
import org.finos.springbot.tests.controller.AbstractHandlerMappingTest;
import org.finos.springbot.tests.controller.OurController;
import org.finos.springbot.workflow.actions.Action;
import org.finos.springbot.workflow.actions.FormAction;
import org.finos.springbot.workflow.actions.SimpleMessageAction;
import org.finos.springbot.workflow.annotations.ChatRequest;
import org.finos.springbot.workflow.annotations.WorkMode;
import org.finos.springbot.workflow.content.Chat;
import org.finos.springbot.workflow.content.Message;
import org.finos.springbot.workflow.content.User;
import org.finos.springbot.workflow.form.Button;
import org.finos.springbot.workflow.form.Button.Type;
import org.finos.springbot.workflow.form.ButtonList;
import org.finos.springbot.workflow.java.mapping.ChatMapping;
import org.finos.springbot.workflow.response.WorkResponse;
import org.finos.springbot.workflow.response.handlers.ResponseHandlers;
import org.junit.jupiter.api.Assertions;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.builder.TurnContextImpl;
import com.microsoft.bot.schema.Activity;
import com.microsoft.bot.schema.ActivityTypes;
import com.microsoft.bot.schema.Attachment;
import com.microsoft.bot.schema.ChannelAccount;
import com.microsoft.bot.schema.teams.ChannelInfo;
import com.microsoft.bot.schema.teams.TeamsChannelData;


@SpringBootTest(classes = {
		MockTeamsConfiguration.class, 
		TeamsWorkflowConfig.class,
})
public class HandlerMappingTest extends AbstractHandlerMappingTest {
	
	ArgumentCaptor<Activity> msg;
	TurnContext tc;
	
	@Autowired
	MessageActivityHandler mah;
	
	
    public static void compareJson(String loadJson, String json) throws JsonMappingException, JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        Assertions.assertEquals(om.readTree(loadJson), om.readTree(json));
    }


	protected WorkResponse createWorkAddSubmit(WorkMode wm, Object ob5) {
		tc = Mockito.mock(TurnContext.class);
		CurrentTurnContext.CURRENT_CONTEXT.set(tc);
		msg = ArgumentCaptor.forClass(Activity.class);
		Mockito.when(tc.sendActivity(msg.capture())).thenReturn(CompletableFuture.completedFuture(null));
		TeamsChat theRoom = new TeamsChat( "abc123", "tesxt room");
		WorkResponse wr = new WorkResponse(theRoom, ob5, wm);
		ButtonList bl = (ButtonList) wr.getData().get(ButtonList.KEY);
		Button submit = new Button("submit", Type.ACTION, "GO");
		bl.add(submit);
		return wr;
	}
	
	
	

	@Override
	protected String getMessageData() {
		return msg.getValue().toString();
	}


	@Override
	protected String getMessageContent() {
		return msg.getValue().toString();
	}


	private List<ChatMapping<ChatRequest>> getMappingsFor(String s) throws Exception {
		EntityJson jsonObjects = new EntityJson();
		Message m = smp.apply("<messageML>"+s+"</messageML>", jsonObjects);
		Action a = new SimpleMessageAction(null, null, m, jsonObjects);
		return hm.getHandlers(a);
	}
	

	@Override
	protected void execute(String s) throws Exception {
		tc = Mockito.mock(TurnContext.class);
		CurrentTurnContext.CURRENT_CONTEXT.set(tc);
		msg = ArgumentCaptor.forClass(Activity.class);
		Mockito.when(tc.sendActivity(msg.capture())).thenReturn(CompletableFuture.completedFuture(null));
		
		Activity out = new Activity(ActivityTypes.MESSAGE);
		Attachment a = new Attachment();
		a.setContentType(MediaType.TEXT_HTML_VALUE);
		a.setContent("<div>"+s+"</div>");
		out.setAttachment(a);
		
		TeamsChannelData tcd = new TeamsChannelData();
		ChannelInfo ci = new ChannelInfo(CHAT_ID, OurController.SOME_ROOM);
		tcd.setChannel(ci);
		out.setChannelData(tcd);
		
		ChannelAccount ca = new ChannelAccount(""+ROB_EXAMPLE_ID, ROB_NAME);
		out.setFrom(ca);
		
		ChannelAccount to = new ChannelAccount(""+BOT_ID, BOT_NAME);
		out.setRecipient(to);
		
		Mockito.when(tc.getActivity()).thenReturn(out);
		
		mah.onTurn(tc);
	}


	private void pressButton(String s) throws Exception {
		EntityJson jsonObjects = new EntityJson();
		jsonObjects.put("1", new SymphonyUser(123l, "gaurav", "gaurav@example.com"));
		jsonObjects.put("2", new HashTag("SomeTopic"));
		Chat r = new SymphonyRoom("The Room Where It Happened", "abc123");
		User author = new SymphonyUser(ROB_EXAMPLE_ID, ROB_NAME, ROB_EXAMPLE_EMAIL);
		Object fd = new StartClaim();
		Action a = new FormAction(r, author, fd, s, jsonObjects);
		Action.CURRENT_ACTION.set(a);
		mc.accept(a);
	}
	
	
	
}
