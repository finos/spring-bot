package org.finos.symphony.webhookbot;

import java.io.IOException;
import java.nio.charset.Charset;

import javax.ws.rs.core.HttpHeaders;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.StreamUtils;
import org.springframework.web.reactive.function.client.WebClient;

import com.symphony.api.bindings.StreamIDHelp;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@TestPropertySource(properties = {
	"server.port=9000",
	"symphony.api"
})
public class PostIT {
	
	@Value("${webhook.room}")
	String testRoom;
	
	@Value("${webhook.hook}")
	String hookId;

	@Test
	public void postHook() throws IOException {
		String safeRoom = StreamIDHelp.safeStreamId(testRoom);
		
		WebClient wc = WebClient.builder()
				.baseUrl("http://localhost:9000/hook/"+safeRoom+"/"+hookId)
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.build();
		
		String gh = StreamUtils.copyToString(PostIT.class.getResourceAsStream("/github_webhook_payload.json"), Charset.forName("UTF-8"));
		
		
		HttpStatus hs = wc.post()
			.bodyValue(gh)
			.retrieve()
			.toBodilessEntity()
			.block()
			.getStatusCode();
		
		
		System.out.println(hs);
	}
	
}
