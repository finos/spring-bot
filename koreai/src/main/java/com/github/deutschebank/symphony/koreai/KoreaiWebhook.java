package com.github.deutschebank.symphony.koreai;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * @author rodriva
 */
@Component
public class KoreaiWebhook {

    private static final Logger LOG = LoggerFactory.getLogger(KoreaiConnector.class);

    public String buildPayload(Long userId, String firstName, String lastName, String email, String text) {
        LOG.info("buildPayload for " + text);

        JSONObject session = new JSONObject();
        session.put("new", "false");

        JSONObject message = new JSONObject();
        message.put("text", text);

        JSONObject userInfo = new JSONObject();
        userInfo.put("firstName", firstName);
        userInfo.put("lastName", lastName);
        userInfo.put("email", email);

        JSONObject from = new JSONObject();
        from.put("id", String.valueOf(userId));
        from.put("userInfo", userInfo);

        JSONObject payload = new JSONObject();
        payload.put("session", session);
        payload.put("message", message);
        payload.put("from", from);
        payload.put("to", "");
        LOG.info(payload.toString());
        return payload.toString();
    }
}
