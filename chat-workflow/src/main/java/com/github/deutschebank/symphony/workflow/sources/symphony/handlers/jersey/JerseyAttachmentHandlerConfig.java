package com.github.deutschebank.symphony.workflow.sources.symphony.handlers.jersey;

import java.io.File;
import java.io.FileOutputStream;

import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.deutschebank.symphony.workflow.response.AttachmentResponse;
import com.github.deutschebank.symphony.workflow.sources.symphony.handlers.AttachmentHandler;

@Configuration
//@ConditionalOnClass(FileDataBodyPart.class)
public class JerseyAttachmentHandlerConfig  {

	@Bean
	public AttachmentHandler attachmentHandler() {
		return new AttachmentHandler() {
			
			@Override
			public Object formatAttachment(AttachmentResponse ar) {
				File temp;
				try {
					temp = File.createTempFile("workflow", ar.getSuffix());
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
