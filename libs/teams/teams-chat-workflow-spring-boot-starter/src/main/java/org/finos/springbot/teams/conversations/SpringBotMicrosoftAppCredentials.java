package org.finos.springbot.teams.conversations;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Base64;

import com.azure.core.credential.TokenRequestContext;
import com.azure.identity.ClientCertificateCredential;
import com.azure.identity.ClientCertificateCredentialBuilder;
import com.microsoft.bot.connector.authentication.CertificateAppCredentials;
import com.microsoft.bot.connector.authentication.CertificateAppCredentialsOptions;

public class SpringBotMicrosoftAppCredentials implements SpringBotAppCredentials {

	private String tenantId = null;
	private String clientId = null;
	private ClientCertificateCredential credential = null;
	private CertificateAppCredentials appCredentials = null;

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

				CertificateAppCredentialsOptions out = new CertificateAppCredentialsOptions(clientId,
						Files.newInputStream(Paths.get(certificate)), certificatePassword);

				appCredentials = new CertificateAppCredentials(out);

			}
		} catch (IOException | UnrecoverableKeyException | CertificateException | NoSuchAlgorithmException
				| KeyStoreException | NoSuchProviderException e) {
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
	public CertificateAppCredentials getAppCredentials() {
		return appCredentials;
	}
    
	
	@Override
    public String getToken() {
		
        String token = credential.getTokenSync(
                new TokenRequestContext().addScopes("https://api.botframework.com/.default")
        ).getToken();
        
        return token;
        
    }
	


}