package com.github.deutschebank.symphony.koreai;

import io.jsonwebtoken.impl.DefaultJwtBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;

/**
 * https://developer.kore.ai/docs/bots/channel-enablement/adding-webhook-channel/
 *
 * @author rodriva
 */
@Component
public class KoreaiConnector {
    private final KoreaiResponseMessageAdapter koreaiResponseParser;
    private final KoreaiWebhook koreaiWebhook;
    private CloseableHttpClient client;
    private ExecutorService executor = Executors.newSingleThreadExecutor();


    private static final Logger LOG = LoggerFactory.getLogger(KoreaiConnector.class);

    public KoreaiConnector(KoreaiResponseMessageAdapter koreaiResponseParser, KoreaiWebhook koreaiWebhook) {
        this.koreaiResponseParser = koreaiResponseParser;
        this.koreaiWebhook = koreaiWebhook;
        client = HttpClients.custom().build();
    }

    public Future<String> sendPost(KoreaiProperties koreai, Long userId, String firstName, String lastName, String email, String symphonyQuery, Consumer<String> callback) {

        HttpPost httpPost = new HttpPost(koreai.getUrl() + koreai.getBotId());

        StringEntity entity = new StringEntity(koreaiWebhook.buildPayload(userId, firstName, lastName, email, symphonyQuery), ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json");
        String jwt;
        if (koreai.getSecret() != null) {
            DefaultJwtBuilder defaultJwtBuilder = new DefaultJwtBuilder();
            Map<String, Object> header = new LinkedHashMap<>();
            header.put("alg", "HS256");
            header.put("typ", "JWT");
            defaultJwtBuilder.setHeader(header);
            defaultJwtBuilder.setPayload(String.format("{\n" +
                    "  \"appId\": \"%s\",\n" +
                    "  \"sub\" : \"%s\"\n" +
                    "  }", koreai.getClientId(), new Random().nextInt(1000)));
            jwt = defaultJwtBuilder.signWith(HS256, koreai.getSecret()).compact();
        } else {
            jwt = koreai.getJwt();
        }
        LOG.info("JWT " + jwt);
        httpPost.setHeader("Authorization", "Bearer " + jwt);

        return executor.submit(() -> {

            try (CloseableHttpResponse response = client.execute(httpPost)) {
                InputStream inputStream = response.getEntity().getContent();
                String output = koreaiResponseParser.parse(inputStream);

                if (callback != null && output != null) {
                    callback.accept(output);
                }
                return output;
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
                return null;
            }
        });
    }
}
