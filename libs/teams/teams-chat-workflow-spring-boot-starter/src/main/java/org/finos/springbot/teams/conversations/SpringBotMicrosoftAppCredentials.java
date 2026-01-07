package org.finos.springbot.teams.conversations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.ClientCertificateCredential;
import com.azure.identity.ClientCertificateCredentialBuilder;

public class SpringBotMicrosoftAppCredentials implements SpringBotAppCredentials {

	private String tenantId = null;
	private String clientId = null;
	private ClientCertificateCredential credential = null;
	
	public SpringBotMicrosoftAppCredentials(String tenantId, String clientId, String certificate,
			String certificatePassword) {
		this.tenantId = tenantId;
		this.clientId = clientId;
		String pemContent = certificate;
		try {

			// Check for file extension and illegal path characters
			boolean isFilePath = (certificate != null && (certificate.endsWith(".p12")));

			if (certificate != null) {
				if (!isFilePath) {
					byte[] decode = Base64.getDecoder().decode(pemContent);

					java.nio.file.Path tempFile = Files.createTempFile("cert", ".p12");
					Files.write(tempFile, decode);
					certificate = tempFile.toAbsolutePath().toString();
				}

				this.credential = new ClientCertificateCredentialBuilder().tenantId(tenantId).clientId(clientId)
						.pemCertificate(Files.newInputStream(Paths.get(certificate)))
						.clientCertificatePassword(certificatePassword).build();
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to create certificate", e);
		}

	}
	
	@Override
	public String getTenantId() {
		return tenantId;
	}

	@Override
	public String getClientId() {
		return clientId;
	}

	@Override
	public ClientCertificateCredential getCredential() {
		return credential;
	}

	@Override
	public String getToken() {
		return credential.getTokenSync(new TokenRequestContext().addScopes("https://graph.microsoft.com/.default"))
				.getToken();
	}

	
}