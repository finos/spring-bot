package org.finos.springbot.teams.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.finos.springbot.teams.MockTeamsConfiguration;
import org.finos.springbot.teams.TeamsWorkflowConfig;
import org.finos.springbot.teams.content.TeamsChannel;
import org.finos.springbot.teams.content.TeamsChat;
import org.finos.springbot.teams.content.TeamsMultiwayChat;
import org.finos.springbot.teams.content.TeamsUser;
import org.finos.springbot.teams.conversations.TeamsConversations;
import org.finos.springbot.teams.history.TeamsHistory;
import org.finos.springbot.teams.messages.MessageActivityHandler;
import org.finos.springbot.teams.turns.CurrentTurnContext;
import org.finos.springbot.tests.controller.AbstractHandlerMappingTest;
import org.finos.springbot.tests.controller.OurController;
import org.finos.springbot.workflow.actions.Action;
import org.finos.springbot.workflow.actions.SimpleMessageAction;
import org.finos.springbot.workflow.annotations.ChatRequest;
import org.finos.springbot.workflow.annotations.WorkMode;
import org.finos.springbot.workflow.content.Message;
import org.finos.springbot.workflow.data.DataHandlerConfig;
import org.finos.springbot.workflow.data.EntityJsonConverter;
import org.finos.springbot.workflow.form.Button;
import org.finos.springbot.workflow.form.Button.Type;
import org.finos.springbot.workflow.form.ButtonList;
import org.finos.springbot.workflow.java.mapping.ChatMapping;
import org.finos.springbot.workflow.java.mapping.ChatRequestChatHandlerMapping;
import org.finos.springbot.workflow.response.WorkResponse;
import org.junit.jupiter.api.Assertions;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.schema.Activity;
import com.microsoft.bot.schema.ActivityTypes;
import com.microsoft.bot.schema.Attachment;
import com.microsoft.bot.schema.ChannelAccount;
import com.microsoft.bot.schema.ConversationAccount;
import com.microsoft.bot.schema.Entity;
import com.microsoft.bot.schema.Mention;
import com.microsoft.bot.schema.teams.ChannelInfo;
import com.microsoft.bot.schema.teams.TeamsChannelData;


@SpringBootTest(classes = {
		MockTeamsConfiguration.class, 
		TeamsWorkflowConfig.class,
		DataHandlerConfig.class
})
@ActiveProfiles("teams")
public class TeamsHandlerMappingTest extends AbstractHandlerMappingTest {
	
	ArgumentCaptor<Activity> msg;
	ArgumentCaptor<Map<String, Object>> data;
	
	TurnContext tc;
	
	@Autowired
	MessageActivityHandler mah;
	
	@Autowired
	ChatRequestChatHandlerMapping hm;
	
	@MockBean
	TeamsHistory th;
	
	@MockBean
	TeamsConversations conv;
	
	@Autowired
	EntityJsonConverter ejc;
	
    public static void compareJson(String loadJson, String json) throws JsonMappingException, JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        Assertions.assertEquals(om.readTree(loadJson), om.readTree(json));
    }


	protected WorkResponse createWorkAddSubmit(WorkMode wm, Object ob5) {
		tc = Mockito.mock(TurnContext.class);
		CurrentTurnContext.CURRENT_CONTEXT.set(tc);
		
		msg = ArgumentCaptor.forClass(Activity.class);
		Mockito.when(tc.sendActivity(msg.capture())).thenReturn(CompletableFuture.completedFuture(null));	
				
		TeamsMultiwayChat theRoom = new TeamsMultiwayChat( "abc123", "tesxt room");
		WorkResponse wr = new WorkResponse(theRoom, ob5, wm);
		ButtonList bl = (ButtonList) wr.getData().get(ButtonList.KEY);
		Button submit = new Button("submit", Type.ACTION, "GO");
		bl.add(submit);
		return wr;
	}
	

	@SuppressWarnings("unchecked")
	@Override
	protected String getMessageData() {
		data = ArgumentCaptor.forClass(Map.class);
		Mockito.verify(th).store(Mockito.any(), Mockito.any(), data.capture());

		return ejc.writeValue(data.getValue());
	}


	@Override
	protected String getMessageContent() {
		Activity out = msg.getValue();
		if (out.getAttachments().size() > 0) {
			Attachment a1 = out.getAttachments().get(0);
			return (String) a1.getContent();
			
			
		} else {
			return out.getText();
		}
	}


	@Override
	protected void execute(String s) throws Exception {
		s = s.replace("@gaurav", "<span itemscope=\"\" itemtype=\"http://schema.skype.com/Mention\" itemid=\"1\">gaurav</span>");
		s = "<span itemscope=\"\" itemtype=\"http://schema.skype.com/Mention\" itemid=\"0\">"+BOT_NAME+"</span>" + s;
		
		mockConversations();
		mockTurnContext(s, null);
		mah.onTurn(tc);
	}
	
	private void mockConversations() {
		Mockito.when(conv.getUser(Mockito.any())).thenAnswer(iom -> {
			ChannelAccount ca = (ChannelAccount) iom.getArgument(0);
			if (ca.getName().equals(ROB_NAME)) {
				return new TeamsUser("directchatid", ROB_NAME, ROB_EXAMPLE_EMAIL);
			} else if (ca.getName().equals(BOT_NAME)) {
				return new TeamsUser(""+BOT_ID, BOT_NAME, BOT_EMAIL);
			}
			
			return null;
		});
		
		Mockito.when(conv.isSupported(Mockito.any(TeamsChat.class))).thenReturn(true);
		Mockito.when(conv.isSupported(Mockito.any(TeamsUser.class))).thenReturn(true);

		
		Mockito.when(conv.getTeamsAddressable(Mockito.any()))
			.thenReturn(new TeamsChannel(CHAT_ID, OurController.SOME_ROOM));
	
		Mockito.when(conv.getChatAdmins(Mockito.any()))
			.thenAnswer(iom -> Arrays.asList( new TeamsUser("directchatid", ROB_NAME, ROB_EXAMPLE_EMAIL)));
	}


	private void mockTurnContext(String s, Map<String, Object> formData) {
		tc = Mockito.mock(TurnContext.class);
		CurrentTurnContext.CURRENT_CONTEXT.set(tc);

		msg = ArgumentCaptor.forClass(Activity.class);
		Mockito.when(tc.sendActivity(msg.capture())).thenReturn(CompletableFuture.completedFuture(null));
		
		Activity out = createActivity(s, formData);
		Mockito.when(tc.getActivity()).thenReturn(out);
	}


	private Activity createActivity(String s, Map<String, Object> formData) {
		Activity out = new Activity(ActivityTypes.MESSAGE);
		
		ConversationAccount conv = new ConversationAccount(CHAT_ID);
		out.setConversation(conv);
		conv.setConversationType("channel");
		conv.setName(OurController.SOME_ROOM);
		
		TeamsChannelData tcd = new TeamsChannelData();
		ChannelInfo ci = new ChannelInfo(CHAT_ID, OurController.SOME_ROOM);
		tcd.setChannel(ci);
		out.setChannelData(tcd);
		
		ChannelAccount ca = new ChannelAccount(""+ROB_EXAMPLE_ID, ROB_NAME);
		out.setFrom(ca);
		
		ChannelAccount to = botChannelAccount();
		out.setRecipient(to);
		
		out.setEntities(Arrays.asList(botEntity(), gauravEntity()));
		
		if (formData != null) {
			formData.put("action", s);
			out.setValue(formData);
		} else {
			Attachment a = new Attachment();
			a.setContentType(MediaType.TEXT_HTML_VALUE);
			a.setContent("<div>"+s+"</div>");
			out.setAttachment(a);
		}

		
		return out;
	}


	private ChannelAccount botChannelAccount() {
		return new ChannelAccount(""+BOT_ID, BOT_NAME);
	}


	private Entity gauravEntity() {
		Mention out = new Mention();
		out.setText("<at>gaurav</at>");
		ChannelAccount ca = new ChannelAccount();
		ca.setName("gaurav");
		ca.setId("3276423876");
		out.setMentioned(ca);
		return new Entity().setAs(out);
	}
	
	private Entity botEntity() {
		Mention out = new Mention();
		out.setText("<at>"+BOT_NAME+"</at>");
		ChannelAccount ca = new ChannelAccount();
		ca.setName(BOT_NAME);
		ca.setId(""+BOT_ID);
		out.setMentioned(ca);
		return new Entity().setAs(out);
	}


	@Override
	protected void pressButton(String s, Map<String, Object> formData) {
		mockConversations();
		mockTurnContext(s, formData);
		mah.onTurn(tc);
	}


	@Override
	protected List<ChatMapping<ChatRequest>> getMappingsFor(Message s) throws Exception {
		Map<String, Object> map = new HashMap<>();
 		Action a = new SimpleMessageAction(null, null, s, map);
		return hm.getHandlers(a);
	}


	@Override
	protected void assertHelpResponse() throws Exception {
		String data = getMessageData();
		System.out.println(data);
		Assertions.assertTrue(data.contains("\"examples\" : [ \"optionals {thing} {user} {lastword}\" ]"));
	}


	@Override
	protected void assertNoButtons() {
		String data = getMessageData();
		Assertions.assertFalse(data.contains("ActionSet"));
	}

}
