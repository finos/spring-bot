package org.finos.springbot.tool.llm;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.finos.springbot.testing.content.TestRoom;
import org.finos.springbot.workflow.content.Chat;
import org.finos.springbot.workflow.content.Message;
import org.finos.springbot.workflow.content.User;
import org.finos.springbot.workflow.response.Response;
import org.finos.springbot.workflow.response.WorkResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class LLMBotControllerTest {

    @Mock
    private LLMService llmService;

    @Mock
    private User author;

    @Mock
    private Message message;

    private LLMBotController controller;

    @Test
    public void testRespondInRoom() {
        // Setup
        controller = new LLMBotController();
        controller.llmService = llmService;

        Chat room = new TestRoom("test-room", "1234");
        String expectedResponse = "Test response from LLM";

        when(message.getText()).thenReturn("Test message");
        when(llmService.getResponse("Test message")).thenReturn(expectedResponse);

        // Execute
        Response response = controller.respondInRoom(author, message, room);

        // Verify
        Assertions.assertTrue(response instanceof WorkResponse);
        WorkResponse workResponse = (WorkResponse) response;

        // Verify the response data structure
        Map<String, Object> data = workResponse.getData();
        Assertions.assertEquals(expectedResponse, data.get(WorkResponse.OBJECT_KEY));
        Assertions.assertEquals(room, workResponse.getAddress());
        Assertions.assertEquals(expectedResponse, workResponse.getData().get(WorkResponse.OBJECT_KEY));
        
    }
}
