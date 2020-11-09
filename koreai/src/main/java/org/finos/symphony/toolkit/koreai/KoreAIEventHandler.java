package org.finos.symphony.toolkit.koreai;

import java.io.ByteArrayInputStream;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.finos.symphony.toolkit.koreai.request.KoreAIRequester;
import org.finos.symphony.toolkit.stream.StreamEventConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.model.V4Event;
import com.symphony.api.model.V4MessageSent;
import com.symphony.api.model.V4Stream;
import com.symphony.api.model.V4SymphonyElementsAction;
import com.symphony.api.model.V4User;

/**
 * This deals with Symphony events that need to be routed to KoreAI.
 * 
 * @author moffrob
 *
 */
public class KoreAIEventHandler implements StreamEventConsumer {

	private static final Logger LOG = LoggerFactory.getLogger(KoreAIEventHandler.class);

	private SymphonyIdentity botIdentity;
	private KoreAIRequester requester;

	public KoreAIEventHandler(SymphonyIdentity botIdentity, KoreAIRequester requester) {
		this.botIdentity = botIdentity;
		this.requester = requester;
	}

	@Override
	public void accept(V4Event t) {
		V4MessageSent ms = t.getPayload().getMessageSent();
		V4User u = t.getInitiator().getUser();
		if (ms != null) {
			if (!u.getEmail().equals(botIdentity.getEmail())) {
				try {
					Address a = buildAddress(u, ms.getMessage().getStream());
					DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
					Document d = db.parse(new ByteArrayInputStream(ms.getMessage().getMessage().getBytes()));
					String text = d.getDocumentElement().getTextContent();
					requester.send(a, text);
				} catch (Exception e) {
					LOG.error("Couldn't handle message {}", ms);
				}
			}
		}

		// handle form submits
		V4SymphonyElementsAction elements = t.getPayload().getSymphonyElementsAction();
		if (elements != null) {
			String formId = elements.getFormId();
			if (formId.equals("koreai-choice")) {
				String button = ((Map<String, String>) elements.getFormValues()).get("action");
				try {
					Address a = buildAddress(u, elements.getStream());
					requester.send(a, button);
					
				} catch (Exception e) {
					LOG.error("Couldn't handle form submission {}", ms);
				}
			}		
		}
	}

	private Address buildAddress(V4User from, V4Stream stream) {
		return new Address(from.getUserId(), 
				from.getFirstName(),
				from.getLastName(), 
				from.getEmail(),
				stream.getStreamId());
	}

}
