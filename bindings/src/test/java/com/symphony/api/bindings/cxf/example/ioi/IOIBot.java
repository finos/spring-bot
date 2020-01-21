package com.symphony.api.bindings.cxf.example.ioi;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Element;

import com.symphony.api.agent.DatafeedApi;
import com.symphony.api.agent.MessagesApi;
import com.symphony.api.bindings.Streams;
import com.symphony.api.bindings.TestPodConfig;
import com.symphony.api.model.Datafeed;
import com.symphony.api.model.V4Message;
import com.symphony.api.pod.UsersApi;

/**
 * This example bot takes a message posted on Symphony and converts it into FIX.
 * 
 * (Adapted from https://github.com/symphonysa/IOIBotApp by Manuela Caicedo)
 * 
 * @author Rob Moffat, Manuela Caicedo
 *
 */
public class IOIBot {
	
	public static void main(String[] args) throws Exception {
		DatafeedApi dfApi = TestPodConfig.CXF_CERT.getAgentApi(DatafeedApi.class);
		Datafeed df = dfApi.v4DatafeedCreatePost(null, null);
		IOIBot bot = new IOIBot(
				TestPodConfig.CXF_CERT.getAgentApi(MessagesApi.class), 
				TestPodConfig.CXF_CERT.getPodApi(UsersApi.class));
		
		Streams.createWorker(() -> dfApi.v4DatafeedIdReadGet(df.getId(), null, null, 100), (e) -> e.printStackTrace())
			.stream()
			.filter(e -> e.getType().equals("MESSAGESENT"))
			.map(e -> e.getPayload().getMessageSent().getMessage())
			.filter(m -> !m.getUser().getEmail().equals(TestPodConfig.CXF_CERT.getIdentity().getEmail()))
			.forEach(m -> bot.onRoomMessage(m));
	}

	private MessagesApi messagesApi;
	private UsersApi usersApi;
    private FixClient fixClient = new FixClient();

	public IOIBot(MessagesApi ma, UsersApi ua) {
		this.messagesApi = ma;
		this.usersApi = ua;
	}

    public void onRoomMessage(V4Message message) {
    	//String senderEmail = message.getUser().getEmail();
    	//UserV2 sender = usersApi.v2UserGet(null, null, senderEmail, null, true);
        String senderCompany = "Deutsche Bank";
        String targetCompany = "A.N Bank";

        try{
        	Element e = parse(message.getMessage()); 
        	
            String[] parsedText = e.getTextContent().trim().split(" ");
            
            if (parsedText.length != 3) {
            	throw new RuntimeException("Invalid format: "+parsedText);
            }

            IOI ioi = new IOI(parsedText[0].equals("S")? Action.S:Action.B,parsedText[2],parsedText[1],senderCompany,targetCompany);

            this.fixClient.sendFixMessageFromSymphony(ioi);

            messagesApi.v4StreamSidMessageCreatePost(null, message.getStream().getStreamId(), "<messageML>IOI was processed</messageML>", null, null, null, null, null);


        } catch (Exception e) {
        	e.printStackTrace();
        	messagesApi.v4StreamSidMessageCreatePost(null, message.getStream().getStreamId(), "<messageML>IOI was not processed: Message should be in the form: (B|S) &lt;amount&gt; &lt;ticker&gt;</messageML>", null, null, null, null, null);

        }
    }
    
    Element parse(String in) throws Exception {
    	Element node =  DocumentBuilderFactory
    		    .newInstance()
    		    .newDocumentBuilder()
    		    .parse(new ByteArrayInputStream(in.getBytes()))
    		    .getDocumentElement();
    	return node;
    }
}

