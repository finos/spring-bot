package org.finos.springbot.teams.conversations;

import com.azure.identity.ClientCertificateCredential;
import com.microsoft.bot.connector.authentication.CertificateAppCredentials;

public interface SpringBotAppCredentials {

	String getTenantId();

	String getClientId();

	ClientCertificateCredential getCredential();
	
	String getToken();

	CertificateAppCredentials getAppCredentials();

}