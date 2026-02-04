package org.finos.springbot.teams.messages;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.finos.springbot.teams.MockTeamsConfiguration;
import org.finos.springbot.teams.TeamsWorkflowConfig;
import org.finos.springbot.workflow.data.DataHandlerConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.schema.teams.FileConsentCardResponse;
import com.microsoft.bot.schema.teams.FileUploadInfo;

@SpringBootTest(classes = { MockTeamsConfiguration.class, TeamsWorkflowConfig.class, DataHandlerConfig.class })
@ActiveProfiles("teams")
@ExtendWith(SpringExtension.class)
public class FileActivityHandlerTest {


	// Use the Spring context to wire the handler and its dependencies instead of Mockito @InjectMocks
	@Autowired
	FileActivityHandler handler;

	TurnContext tc;

	@Test
	public void testOnTeamsFileConsentAccept() throws IOException, InterruptedException, ExecutionException {
		tc = Mockito.mock(TurnContext.class);

		FileConsentCardResponse f = getFileConsent();
		handler.onTeamsFileConsentAccept(tc, f);
	}

	@Test
	public void testOnTeamsFileConsentDecline() throws IOException, InterruptedException, ExecutionException {
		tc = Mockito.mock(TurnContext.class);

		FileConsentCardResponse f = getFileConsent();
		handler.onTeamsFileConsentDecline(tc, f);
	}

	private FileConsentCardResponse getFileConsent() throws IOException {
		FileConsentCardResponse fileConsentCardResponse = new FileConsentCardResponse();
		Path file = Files.createTempFile("temp-", "sample.json");

		Map<String, String> map = new HashMap<>();
		String filePath = "file://"+ file.toAbsolutePath().toString();
		map.put("filepath", filePath);
		map.put("filename", "sample.json");

		fileConsentCardResponse.setContext(map);
		FileUploadInfo uploadInfo = new FileUploadInfo();

		uploadInfo.setUploadUrl(filePath);
		fileConsentCardResponse.setUploadInfo(uploadInfo);
		return fileConsentCardResponse;
	}

}
