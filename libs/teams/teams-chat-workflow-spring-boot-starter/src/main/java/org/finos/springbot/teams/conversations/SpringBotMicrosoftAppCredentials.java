package org.finos.springbot.teams.conversations;

import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.ClientCertificateCredential;

public class SpringBotMicrosoftAppCredentials {

    private String tenantId;
    private String clientId;
    private ClientCertificateCredential credential;

    public SpringBotMicrosoftAppCredentials(String tenantId, String clientId,
            ClientCertificateCredential clientCertificateCredential) {
        this.tenantId = tenantId;
        this.clientId = clientId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getToken() {
        // SOMETHING LIKE THIS
        return credential.getTokenSync(new TokenRequestContext().addScopes("https://graph.microsoft.com/.default"))
                .getToken();
    }

}
