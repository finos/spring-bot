package org.finos.springbot.teams.messages;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import org.finos.springbot.teams.content.serialization.TeamsHTMLParser;
import org.finos.springbot.teams.conversations.TeamsConversations;
import org.finos.springbot.teams.state.TeamsStateStorage;
import org.finos.springbot.workflow.actions.consumers.ActionConsumer;
import org.finos.springbot.workflow.form.FormConverter;
import org.finos.springbot.workflow.form.FormValidationProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.bot.builder.MessageFactory;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.connector.teams.TeamsConnectorClient;
import com.microsoft.bot.schema.Activity;
import com.microsoft.bot.schema.Attachment;
import com.microsoft.bot.schema.ResultPair;
import com.microsoft.bot.schema.TextFormatTypes;
import com.microsoft.bot.schema.teams.FileConsentCardResponse;
import com.microsoft.bot.schema.teams.FileInfoCard;

public class FileActivityHandler extends MessageActivityHandler {

	private static final Logger LOG = LoggerFactory.getLogger(FileActivityHandler.class);


	public FileActivityHandler(List<ActionConsumer> messageConsumers, TeamsConversations teamsConversations,
			TeamsStateStorage teamsStateStorage, TeamsHTMLParser parser, FormConverter formConverter,
			FormValidationProcessor validationProcessor, TeamsConnectorClient teamsConnectorClient) {
		super(messageConsumers, teamsConversations, teamsStateStorage, parser, formConverter, validationProcessor,
				teamsConnectorClient);
	}

	@Override
	protected CompletableFuture<Void> onTeamsFileConsentAccept(TurnContext turnContext,
			FileConsentCardResponse fileConsentCardResponse) {
		LOG.info("onTeamsFileConsentAccept called...");	
		
		return upload(fileConsentCardResponse)
				.thenCompose(result -> !result.result() ? fileUploadFailed(turnContext, result.value())
						: fileUploadCompleted(turnContext, fileConsentCardResponse));
	}

	@Override
	protected CompletableFuture<Void> onTeamsFileConsentDecline(TurnContext turnContext,
			FileConsentCardResponse fileConsentCardResponse) {
		LOG.info("onTeamsFileConsentDecline called...");	
		Map<String, String> context = (Map<String, String>) fileConsentCardResponse.getContext();

		Activity reply = MessageFactory
				.text(String.format("Declined. We won't upload file <b>%s</b>.", context.get("filename")));
		reply.setTextFormat(TextFormatTypes.XML);

		return turnContext.sendActivityBlind(reply);
	}

	private CompletableFuture<ResultPair<String>> upload(FileConsentCardResponse fileConsentCardResponse) {
		AtomicReference<ResultPair<String>> result = new AtomicReference<>();

		return CompletableFuture.runAsync(() -> {
			Map<String, String> context = (Map<String, String>) fileConsentCardResponse.getContext();
			LOG.info("filepath - {}", context.get("filepath"));
			LOG.info("File upload endpoint : {}", fileConsentCardResponse.getUploadInfo().getUploadUrl());
			File filePath = new File(context.get("filepath"));

			URLConnection connection = null;
			try {
				URL url = new URL(fileConsentCardResponse.getUploadInfo().getUploadUrl());
				if (url.openConnection() instanceof HttpURLConnection) {
					connection = (HttpURLConnection) url.openConnection();
					((HttpURLConnection) connection).setRequestMethod("PUT");

					connection.setDoOutput(true);
					connection.setRequestProperty("Content-Length", Long.toString(filePath.length()));
					connection.setRequestProperty("Content-Range",
							String.format("bytes 0-%d/%d", filePath.length() - 1, filePath.length()));

					try (FileInputStream fileStream = new FileInputStream(filePath);
							OutputStream uploadStream = connection.getOutputStream()) {
						byte[] buffer = new byte[4096];
						int bytes_read;
						while ((bytes_read = fileStream.read(buffer)) != -1) {
							uploadStream.write(buffer, 0, bytes_read);
						}

						uploadStream.flush();
					}

					try {
						BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
						String inputLine;
						while ((inputLine = in.readLine()) != null)
							LOG.info(inputLine);
						in.close();
					} catch (Exception e) {
						LOG.error("Exception occured while reading steam.. ignore this error " + e);
					}
				}
				result.set(new ResultPair<String>(true, null));
				
			} catch (Throwable t) {
				t.printStackTrace();
				result.set(new ResultPair<String>(false, t.getLocalizedMessage()));
			} finally {
				if (connection != null) {

					if (connection instanceof HttpURLConnection) {
						((HttpURLConnection) connection).disconnect();
					}
				}
			}
		}).thenApply(aVoid -> result.get());
	}

	private CompletableFuture<Void> fileUploadFailed(TurnContext turnContext, String error) {
		
		LOG.info("fileUploadFailed called with error {}" , error);	
		
		Activity reply = MessageFactory.text("<b>File upload failed.</b> Error: <pre>" + error + "</pre>");
		reply.setTextFormat(TextFormatTypes.XML);
		return turnContext.sendActivityBlind(reply);
	}

	private CompletableFuture<Void> fileDownloadCompleted(TurnContext turnContext, Attachment attachment) {
		Activity reply = MessageFactory.text(String.format("<b>%s</b> received and saved.", attachment.getName()));
		reply.setTextFormat(TextFormatTypes.XML);

		return turnContext.sendActivityBlind(reply);
	}

	private CompletableFuture<Void> fileUploadCompleted(TurnContext turnContext,
			FileConsentCardResponse fileConsentCardResponse) {
		
		LOG.info("file Upload Completed with unique id {} ", fileConsentCardResponse.getUploadInfo().getUniqueId());
		
		FileInfoCard downloadCard = new FileInfoCard();
		downloadCard.setUniqueId(fileConsentCardResponse.getUploadInfo().getUniqueId());
		downloadCard.setFileType(fileConsentCardResponse.getUploadInfo().getFileType());

		Attachment asAttachment = new Attachment();
		asAttachment.setContent(downloadCard);
		asAttachment.setContentType(FileInfoCard.CONTENT_TYPE);
		asAttachment.setName(fileConsentCardResponse.getUploadInfo().getName());
		asAttachment.setContentUrl(fileConsentCardResponse.getUploadInfo().getContentUrl());

		Activity reply = MessageFactory
				.text(String.format("<b>File uploaded.</b> Your file <b>%s</b> is ready to download",
						fileConsentCardResponse.getUploadInfo().getName()));
		reply.setTextFormat(TextFormatTypes.XML);
		reply.setAttachment(asAttachment);

		return turnContext.sendActivityBlind(reply);
	}
}
