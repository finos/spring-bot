package org.finos.symphony.toolkit.koreai;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.koreai.request.KoreAIRequester;
import org.finos.symphony.toolkit.stream.StreamEventConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.symphonyoss.TaxonomyElement;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.model.StreamType.TypeEnum;
import com.symphony.api.model.V4Event;
import com.symphony.api.model.V4MessageSent;
import com.symphony.api.model.V4RoomUpdated;
import com.symphony.api.model.V4Stream;
import com.symphony.api.model.V4SymphonyElementsAction;
import com.symphony.api.model.V4User;
import com.symphony.user.Mention;
import com.symphony.user.UserId;

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
	private boolean onlyAddressed = true;
	private ObjectMapper symphonyObjectMapper;
	private Long botUserId;

	public KoreAIEventHandler(SymphonyIdentity botIdentity, 
			Long botUserId, 
			KoreAIRequester requester, 
			ObjectMapper symphonyObjectMapper, 
			boolean onlyAddressed) {
		this.botIdentity = botIdentity;
		this.requester = requester;
		this.symphonyObjectMapper = symphonyObjectMapper;
		this.botUserId = botUserId;
		this.onlyAddressed = onlyAddressed;
	}

	@Override
	public void accept(V4Event t) {
		try {
			V4MessageSent ms = t.getPayload().getMessageSent();
			V4User u = t.getInitiator().getUser();
			if (ms != null) {
				V4Stream stream = ms.getMessage().getStream();
				String text = extractText(ms);
				EntityJson ej = parseEntityJson(ms.getMessage().getData());
				if (!u.getEmail().equals(botIdentity.getEmail()) && (isAddressed(stream, ej, text))) {
					try {
						Address a = buildAddress(u, stream);
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
			
		} catch (Exception e) {
			LOG.error("Couldn't handle stream event "+t, e);
		}
	}

	private String extractText(V4MessageSent ms) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document d = db.parse(new ByteArrayInputStream(ms.getMessage().getMessage().getBytes()));
		String text = d.getDocumentElement().getTextContent();
		return text;
	}

	private boolean isAddressed(V4Stream s, EntityJson ej, String text) {
		if (!onlyAddressed) {
			return true;
		}
		
		TypeEnum streamType = TypeEnum.fromValue(s.getStreamType());
		
		switch (streamType) {
		case IM:
			return true;
		case POST:
			return false;
		case ROOM:
		case MIM:
		default:
			if (text.trim().startsWith("/")) {
				return true;
			}
			
			for (Object	o  : ej.values()) {
				if (o instanceof Mention) {
					Mention m = (Mention) o;
					for (TaxonomyElement t : (List<TaxonomyElement>) m.getId()) {
						if (t instanceof UserId) {
							if (t.getValue().equals(botUserId.toString())) {
								return true;
							}
						}
					}
					
					
				}
			}
			
			
			return false;
		}
	}

	private EntityJson parseEntityJson(String data) throws JsonProcessingException {
		return symphonyObjectMapper.readValue(data, EntityJson.class);
	}

	private Address buildAddress(V4User from, V4Stream stream) {
		return new Address(from.getUserId(), 
				from.getFirstName(),
				from.getLastName(), 
				from.getEmail(),
				stream.getStreamId());
	}

}
