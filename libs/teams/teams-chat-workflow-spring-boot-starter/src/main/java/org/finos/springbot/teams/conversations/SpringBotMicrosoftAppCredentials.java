package org.finos.springbot.teams.conversations;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.ClientCertificateCredential;
import com.azure.identity.ClientCertificateCredentialBuilder;

public class SpringBotMicrosoftAppCredentials {

	private String tenantId;
	private String clientId;
	private ClientCertificateCredential credential;

	public SpringBotMicrosoftAppCredentials(String tenantId, String clientId, String certificate,
			String certificatePassword) {
		this.tenantId = tenantId;
		this.clientId = clientId;
		String pemContent = certificate;
		// Check for file extension and illegal path characters
		boolean isFilePath = (certificate != null && (certificate.endsWith(".p12")));
		try {
			if (!isFilePath) {
				byte[] decode = Base64.getDecoder().decode(pemContent);

				java.nio.file.Path tempFile = Files.createTempFile("cert", ".p12");
				Files.write(tempFile, decode);
				certificate = tempFile.toAbsolutePath().toString();
			}

			this.credential = new ClientCertificateCredentialBuilder().tenantId(tenantId).clientId(clientId)
					.pemCertificate(Files.newInputStream(Paths.get(certificate)))
					.clientCertificatePassword(certificatePassword).build();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Failed to create certificate", e);
		}
	}

	public String getTenantId() {
		return tenantId;
	}

	public String getClientId() {
		return clientId;
	}

	public ClientCertificateCredential getCredential() {
		return credential;
	}

	public String getToken() {
		// SOMETHING LIKE THIS
		return credential.getTokenSync(new TokenRequestContext().addScopes("https://graph.microsoft.com/.default"))
				.getToken();
	}

	public static InputStream stringToInputStream(String content) {
		return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
	}
}