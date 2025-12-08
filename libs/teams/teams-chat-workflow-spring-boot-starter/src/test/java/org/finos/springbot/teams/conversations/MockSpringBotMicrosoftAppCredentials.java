package org.finos.springbot.teams.conversations;

import com.azure.identity.ClientCertificateCredential;

public class MockSpringBotMicrosoftAppCredentials implements SpringBotAppCredentials {

	@Override
	public String getTenantId() {
		return "mock-tenant-id";
	}

	@Override
	public String getClientId() {
		return "mock-client-id";
	}

	@Override
	public ClientCertificateCredential getCredential() {
		return null;
	}

	@Override
	public String getToken() {
		return "mock-token";
	}

}
