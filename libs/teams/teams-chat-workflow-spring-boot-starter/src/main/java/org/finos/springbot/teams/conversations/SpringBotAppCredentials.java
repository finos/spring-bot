package org.finos.springbot.teams.conversations;

import com.azure.identity.ClientCertificateCredential;

public interface SpringBotAppCredentials {

	String getTenantId();

	String getClientId();

	ClientCertificateCredential getCredential();
	
	String getToken();

}