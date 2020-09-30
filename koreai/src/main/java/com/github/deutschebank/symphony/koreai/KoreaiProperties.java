package com.github.deutschebank.symphony.koreai;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author rodriva
 */
@ConfigurationProperties("symphony.koreai")
public class KoreaiProperties {
    private String clientId;
    private String botId;
    private String secret;
    private String jwt;
    private String url;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getBotId() {
        return botId;
    }

    public void setBotId(String botId) {
        this.botId = botId;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
