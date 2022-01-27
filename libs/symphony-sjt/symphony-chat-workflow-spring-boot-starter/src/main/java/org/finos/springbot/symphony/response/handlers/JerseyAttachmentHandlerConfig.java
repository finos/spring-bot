package org.finos.springbot.symphony.response.handlers;

import java.io.File;
import java.io.FileOutputStream;

import org.finos.springbot.workflow.response.AttachmentResponse;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(FileDataBodyPart.class)
public class JerseyAttachmentHandlerConfig  {

	@Bean
	public AttachmentHandler attachmentHandler() {
		return new AttachmentHandler() {
			
			@Override
			public Object formatAttachment(AttachmentResponse ar) {
				File temp;
				try {
					temp = File.createTempFile(ar.getName(), "." + ar.getExtension());
					FileOutputStream fos = new FileOutputStream(temp);
					fos.write(ar.getAttachment());
					fos.close();
				} catch (Exception e) {
					throw new RuntimeException("Couldn't create file", e);
				}
				FileDataBodyPart fdbp = new FileDataBodyPart("attachment", temp);
				return fdbp;
			}
		};
	}

}
