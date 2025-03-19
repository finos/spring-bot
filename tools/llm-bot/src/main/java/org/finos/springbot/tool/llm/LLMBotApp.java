package org.finos.springbot.tool.llm;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.OpenAiStreamClient;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;
import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgpt.sse.ConsoleEventSourceListener;

@SpringBootApplication
@EnableScheduling
public class LLMBotApp
{
//    public static void main( String[] args )
//    {
//    	SpringApplication.run(new Class[] {LLMBotApp.class}, args);
//    	
//    	
//    	
//    }
    
// the key
	
	
	 public static void main(String[] args) {
		    OpenAiClient openAiClient = OpenAiClient.builder()
		    	       .apiKey(Arrays.asList(THE_KEY))
		                .build();
	        // chat model: gpt-3.5
	        Message message = Message.builder().role(Message.Role.USER).content( "tell me a joke").build();
	        ChatCompletion chatCompletion = ChatCompletion.builder().messages(Arrays.asList(message)).build();
	        ChatCompletionResponse chatCompletionResponse = openAiClient.chatCompletion(chatCompletion);
	        chatCompletionResponse.getChoices().forEach(e -> {
	            System.out.println(e.getMessage().getContent());
	        });
	    }
}
