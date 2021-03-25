package org.finos.symphony.toolkit.maven;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.maven.model.Developer;
import org.codehaus.plexus.util.IOUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.symphony.api.agent.MessagesApi;
import com.symphony.api.authenticator.AuthenticationApi;
import com.symphony.api.authenticator.CertificateAuthenticationApi;
import com.symphony.api.bindings.ApiBuilder;
import com.symphony.api.bindings.JWTHelper;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.model.AuthenticateRequest;
import com.symphony.api.model.Token;
import com.symphony.api.pod.StreamsApi;
import com.symphony.api.pod.UsersApi;

public class SymphonyMessageSender {
	
    private final Logger LOG = LoggerFactory.getLogger(SymphonyMessageSender.class);

    private ProxyingWrapper pod, session, agent, key, relay, login;
    private final String template;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SymphonyIdentity id;

    public SymphonyMessageSender(ProxyingWrapper pod, ProxyingWrapper agent, ProxyingWrapper session, ProxyingWrapper key, ProxyingWrapper relay, ProxyingWrapper login, SymphonyIdentity id) throws IOException {
        template = IOUtil.toString(this.getClass().getResourceAsStream("/template.fm"), UTF_8.toString());
        objectMapper
        	.enable(SerializationFeature.INDENT_OUTPUT)
        	.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        
        this.pod = pod;
        this.agent = agent;
        this.session = session;
        this.key = key;
        this.relay = relay;
        this.login = login;
        this.id = id;
    }
    
    private Token jwtAuth(ApiBuilder apiBuilder) {
    	try {
			String token = JWTHelper.createSignedJwt(id.getCommonName(), id.getPrivateKey());
			AuthenticateRequest ar = new AuthenticateRequest().token(token);
			Token out = apiBuilder.getApi(com.symphony.api.login.AuthenticationApi.class).pubkeyAuthenticatePost(ar);
			return out;
		} catch (Exception e) {
			throw new RuntimeException("Couldn't auth with jwt", e);
		}
    }
		
	@SuppressWarnings("unchecked")
    public void sendMessage(Map<String, Object> data) throws IOException {
		// will use cert login if certs are provided
        String sessionToken = useCertLogin() ? 
        	session.performWithAndWithoutProxies(ab -> ab.getApi(CertificateAuthenticationApi.class).v1AuthenticatePost().getToken()) : 
        	login.performWithAndWithoutProxies(ab -> jwtAuth(ab)).getToken();
        	
        String keyManagerToken = useCertLogin() ? 
        	key.performWithAndWithoutProxies(ab -> ab.getApi(CertificateAuthenticationApi.class).v1AuthenticatePost().getToken()) : 
        	relay.performWithAndWithoutProxies(ab -> jwtAuth(ab)).getToken();

        Map<String, Object> event = (Map<String, Object>) data.get("event");
        filterDevelopersEmails((List<Developer>) event.get("developers"), sessionToken);
        String dataStr = objectMapper.writeValueAsString(data);

        LOG.debug("Writing Message: -----\n"+dataStr+"\n-------");

        for (String recip : (List<String>) event.get("recipients")) {
            String stream;
            if (recip.contains("@")) {
                // we need to create a stream id for this user' email
                stream = findStreamForRecipient(recip, sessionToken);
            } else {
            	// it's a stream id
                stream = recip;
            }

            // post to the stream
            if (stream != null) {
                String encStream = stream.replace("+", "-").replace("/", "_");
                agent.performWithAndWithoutProxies(ab -> ab.getApi(MessagesApi.class)
                        .v4StreamSidMessageCreatePost(sessionToken, encStream, template, dataStr, null, null, null, keyManagerToken));
            }
        }
    }

	protected boolean useCertLogin() {
		return (id.getCertificateChain() != null) && (id.getCertificateChain().length > 0);
	}
	

    private String findStreamForRecipient(String recip, String sessionToken) {
    	return pod.performWithAndWithoutProxies(ab -> {
            Long userId = ab.getApi(UsersApi.class).v3UsersGet(sessionToken, null, recip, null, Boolean.TRUE, Boolean.TRUE)
                    .getUsers().stream()
                    .map(u -> u.getId()).findFirst().orElse(null);

            if (userId != null) {
                return ab.getApi(StreamsApi.class).v1ImCreatePost(Collections.singletonList(userId), sessionToken).getId();
            } else {
                LOG.error("Couldn't determine stream Id for " + recip);
                return null;
            }
    	});
	}

	private void filterDevelopersEmails(List<Developer> list, String sessionToken) {
        for (Developer developer : list) {
            if (developer.getEmail() != null) {
                if (lookupUserId(developer.getEmail(), sessionToken) == null) {
                    // if we don't do this, symphony barfs when creating a mention.
                    developer.setEmail(null);
                }
            }
        }
    }

    private Long lookupUserId(String email, String sessionToken) {
        try {
            return pod.performWithAndWithoutProxies(ab -> {
                UsersApi api = ab.getApi(UsersApi.class);
                return api.v3UsersGet(sessionToken, null, email, null, Boolean.TRUE, Boolean.TRUE)
                        .getUsers().stream()
                        .map(u -> u.getId()).findFirst().orElse(null);
            });
        } catch (NullPointerException e) {
            LOG.warn("No symphony account for " + email);
            return null;
        }

    }
}
