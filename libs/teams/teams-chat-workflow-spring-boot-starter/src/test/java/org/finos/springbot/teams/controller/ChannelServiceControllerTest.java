package org.finos.springbot.teams.controller;


import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.finos.springbot.teams.MockTeamsConfiguration;
import org.finos.springbot.teams.TeamsWorkflowConfig;
import org.finos.springbot.teams.bot.ChannelServiceController;
import org.finos.springbot.teams.content.TeamsUser;
import org.finos.springbot.teams.handlers.TeamsResponseHandler;
import org.finos.springbot.workflow.data.DataHandlerConfig;
import org.finos.springbot.workflow.response.MessageResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.microsoft.bot.builder.ChannelServiceHandler;
import com.microsoft.bot.schema.Activity;
import com.microsoft.bot.schema.AttachmentData;
import com.microsoft.bot.schema.ChannelAccount;
import com.microsoft.bot.schema.ConversationParameters;
import com.microsoft.bot.schema.ConversationResourceResponse;
import com.microsoft.bot.schema.ConversationsResult;
import com.microsoft.bot.schema.PagedMembersResult;
import com.microsoft.bot.schema.ResourceResponse;
import com.microsoft.bot.schema.Transcript;

@SpringBootTest(classes = { MockTeamsConfiguration.class, TeamsWorkflowConfig.class, DataHandlerConfig.class })
@ActiveProfiles("teams")
@ExtendWith(MockitoExtension.class)
public class ChannelServiceControllerTest {

    @Mock
    ChannelServiceHandler handler;

    @InjectMocks
    ChannelServiceController controller = new ChannelServiceController(handler) {};;

    @Mock
    TeamsResponseHandler teamsResponseHandler;

    ResourceResponse rr;

    Activity a;

    @BeforeEach
    public void setup() {
        controller = new ChannelServiceController(handler) {};
        a = Mockito.mock(Activity.class);
        TeamsUser tu = new TeamsUser("made", "up", "thing");
        MessageResponse r = new MessageResponse(tu, "Some object");
        rr = Mockito.mock(ResourceResponse.class);

        //Mockito.when(teamsResponseHandler.apply(r)).thenReturn(rr);
    }

    @SuppressWarnings({ "deprecation" })
    @Test
    public void testSendToConversation() throws InterruptedException, ExecutionException {

        Mockito.when(handler.handleSendToConversation("any text","conversation Id", a)).thenReturn(CompletableFuture.completedFuture(rr));
        CompletableFuture<ResponseEntity<ResourceResponse>> future = controller.sendToConversation("conversation Id", a, "any text");
        ResponseEntity<ResourceResponse> entity = future.get();

        Assertions.assertEquals(200, entity.getStatusCode().value());    }

    @SuppressWarnings({ "deprecation" })
    @Test
    public void testReplyToActivity() throws InterruptedException, ExecutionException {
        Mockito.when(handler.handleReplyToActivity("any text","conversation Id", "activityId", a)).thenReturn(CompletableFuture.completedFuture(rr));
        CompletableFuture<ResponseEntity<ResourceResponse>> future = controller.replyToActivity("conversation Id", "activityId", a, "any text");
        ResponseEntity<ResourceResponse> entity = future.get();

        Assertions.assertEquals(200, entity.getStatusCode().value());    }

    @SuppressWarnings({ "deprecation" })
    @Test
    public void testUpdateActivity() throws InterruptedException, ExecutionException {
        Mockito.when(handler.handleUpdateActivity("any text","conversation Id", "activityId", a)).thenReturn(CompletableFuture.completedFuture(rr));
        CompletableFuture<ResponseEntity<ResourceResponse>> future = controller.updateActivity("conversation Id", "activityId", a, "any text");
        ResponseEntity<ResourceResponse> entity = future.get();

        Assertions.assertEquals(200, entity.getStatusCode().value());    }

    @SuppressWarnings({ "deprecation" })
    @Test
    public void testDeleteActivity() throws InterruptedException, ExecutionException {

        Void vo = Mockito.mock(Void.class);

        Mockito.when(handler.handleDeleteActivity("any text","conversation Id", "activityId")).thenReturn(CompletableFuture.completedFuture(vo));
        CompletableFuture<ResponseEntity<Void>> future = controller.deleteActivity("conversation Id", "activityId", "any text");
        ResponseEntity<Void> entity = future.get();

        Assertions.assertEquals(202, entity.getStatusCode().value());    }

    @SuppressWarnings({ "deprecation" })
    @Test
    public void testGetActivityMembers() throws InterruptedException, ExecutionException {

        List<ChannelAccount> accounts = Mockito.mock(List.class);

        Mockito.when(handler.handleGetActivityMembers("any text","conversation Id", "activityId")).thenReturn(CompletableFuture.completedFuture(accounts));
        CompletableFuture<ResponseEntity<List<ChannelAccount>>> future = controller.getActivityMembers("conversation Id", "activityId", "any text");
        ResponseEntity<List<ChannelAccount>> entity = future.get();

        Assertions.assertEquals(200, entity.getStatusCode().value());    }

    @SuppressWarnings({ "deprecation" })
    @Test
    public void testCreateConversation() throws InterruptedException, ExecutionException {

        ConversationResourceResponse r = Mockito.mock(ConversationResourceResponse.class);

        ConversationParameters parameters = new ConversationParameters();

        Mockito.when(handler.handleCreateConversation("any text", parameters)).thenReturn(CompletableFuture.completedFuture(r));

        CompletableFuture<ResponseEntity<ConversationResourceResponse>> future = controller.createConversation(parameters, "any text");
        ResponseEntity<ConversationResourceResponse> entity = future.get();

        Assertions.assertEquals(200, entity.getStatusCode().value());    }

    @SuppressWarnings({ "deprecation" })
    @Test
    public void testGetConversations() throws InterruptedException, ExecutionException {

        ConversationsResult r = Mockito.mock(ConversationsResult.class);

        Mockito.when(handler.handleGetConversations("any text", "conversation Id", "activityId")).thenReturn(CompletableFuture.completedFuture(r));

        CompletableFuture<ResponseEntity<ConversationsResult>> future = controller.getConversations("conversation Id", "activityId", "any text");
        ResponseEntity<ConversationsResult> entity = future.get();

        Assertions.assertEquals(200, entity.getStatusCode().value());    }

    @SuppressWarnings({ "deprecation" })
    @Test
    public void testGetConversationMembers() throws InterruptedException, ExecutionException {

        List<ChannelAccount> r = Mockito.mock(List.class);

        Mockito.when(handler.handleGetConversationMembers("any text", "conversation Id")).thenReturn(CompletableFuture.completedFuture(r));

        CompletableFuture<ResponseEntity<List<ChannelAccount>>> future = controller.getConversationMembers("conversation Id", "any text");
        ResponseEntity<List<ChannelAccount>> entity = future.get();

        Assertions.assertEquals(200, entity.getStatusCode().value());    }

    @SuppressWarnings({ "deprecation" })
    @Test
    public void testGetConversationPagedMembers() throws InterruptedException, ExecutionException {

        PagedMembersResult r = Mockito.mock(PagedMembersResult.class);

        Mockito.when(handler.handleGetConversationPagedMembers("any text", "conversation Id", 1, "activityId")).thenReturn(CompletableFuture.completedFuture(r));

        CompletableFuture<ResponseEntity<PagedMembersResult>> future = controller.getConversationPagedMembers("conversation Id", 1, "activityId", "any text");
        ResponseEntity<PagedMembersResult> entity = future.get();

        Assertions.assertEquals(200, entity.getStatusCode().value());    }

    @SuppressWarnings({ "deprecation" })
    @Test
    public void testDeleteConversationMember() throws InterruptedException, ExecutionException {

        Void r = Mockito.mock(Void.class);

        Mockito.when(handler.handleDeleteConversationMember("any text", "conversation Id", "Member Id")).thenReturn(CompletableFuture.completedFuture(r));

        CompletableFuture<ResponseEntity<Void>> future = controller.deleteConversationMember("conversation Id",  "Member Id", "any text");
        ResponseEntity<Void> entity = future.get();

        Assertions.assertEquals(202, entity.getStatusCode().value());    }

    @SuppressWarnings({ "deprecation" })
    @Test
    public void testSendConversationHistory() throws InterruptedException, ExecutionException {

        ResourceResponse r = Mockito.mock(ResourceResponse.class);

        Transcript history = new Transcript();

        Mockito.when(handler.handleSendConversationHistory("any text", "conversation Id", history)).thenReturn(CompletableFuture.completedFuture(r));

        CompletableFuture<ResponseEntity<ResourceResponse>> future = controller.sendConversationHistory("conversation Id",history, "any text");
        ResponseEntity<ResourceResponse> entity = future.get();

        Assertions.assertEquals(200, entity.getStatusCode().value());    }

    @SuppressWarnings({ "deprecation" })
    @Test
    public void testUploadAttachment() throws InterruptedException, ExecutionException {

        ResourceResponse r = Mockito.mock(ResourceResponse.class);

        AttachmentData attachmentUpload = new AttachmentData();

        Mockito.when(handler.handleUploadAttachment("any text", "conversation Id", attachmentUpload)).thenReturn(CompletableFuture.completedFuture(r));

        CompletableFuture<ResponseEntity<ResourceResponse>> future = controller.uploadAttachment("conversation Id",attachmentUpload, "any text");
        ResponseEntity<ResourceResponse> entity = future.get();

        Assertions.assertEquals(200, entity.getStatusCode().value());    }
}