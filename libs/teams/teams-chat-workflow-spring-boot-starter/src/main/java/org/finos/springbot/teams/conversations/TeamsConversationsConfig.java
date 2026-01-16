package org.finos.springbot.teams.conversations;

import java.util.Properties;

import org.finos.springbot.teams.bot.BotDependencyConfiguration;
import org.finos.springbot.teams.state.TeamsStateStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.microsoft.bot.builder.BotFrameworkAdapter;
import com.microsoft.bot.connector.authentication.AppCredentials;
import com.microsoft.bot.connector.authentication.AppCredentialsInterceptor;
import com.microsoft.bot.connector.authentication.AuthenticationConfiguration;
import com.microsoft.bot.connector.authentication.CertificateAppCredentials;
import com.microsoft.bot.connector.authentication.ChannelProvider;
import com.microsoft.bot.integration.AdapterWithErrorHandler;
import com.microsoft.bot.integration.BotFrameworkHttpAdapter;
import com.microsoft.bot.integration.Configuration;
import com.microsoft.bot.schema.ChannelAccount;

public class TeamsConversationsConfig extends BotDependencyConfiguration {

	@Bean
	public SpringBotAppCredentials springBotAppCredentials(@Value("${teams.app.tennantId}") String tennantId) {
		com.microsoft.bot.integration.Configuration conf = getConfiguration();

		String clientId = conf
				.getProperty(com.microsoft.bot.connector.authentication.MicrosoftAppCredentials.MICROSOFTAPPID);

		SpringBotAppCredentials out = new SpringBotMicrosoftAppCredentials(tennantId,
				clientId, conf.getProperty("MicrosoftAppIdPemCertificate"),
				conf.getProperty("MicrosoftAppIdPemCertificatePassword"));

		// MicrosoftAppCredentials mac = new MicrosoftAppCredentials(
		// conf.getProperty(MicrosoftAppCredentials.MICROSOFTAPPID),
		// conf.getProperty(MicrosoftAppCredentials.MICROSOFTAPPPASSWORD),
		// tennantId);

		return out;
	}


//	@Primary
//	@Bean
//	CredentialProvider credentialProvider(SpringBotMicrosoftAppCredentials cr) {
//		return new SimpleCredentialProvider(cr.getClientId(), null) ;
//	}
	
	@Primary
	@Bean
	public CertificateAppCredentials certificateCredentials(SpringBotAppCredentials credentials) {
		return credentials.getAppCredentials();
	}
	
	
	@Primary
	@Bean
	public AppCredentialsInterceptor appCredentialsInterceptor(CertificateAppCredentials credentials) {
		return new AppCredentialsInterceptor(credentials);
	}
	
	@Primary
	@Bean
	public BotFrameworkAdapter botFrameworkAdapter(
			CertificateAppCredentials withCredentials,
	        AuthenticationConfiguration withAuthConfig,
	        ChannelProvider withChannelProvider) {
		
		Configuration conf = getConfiguration();
		
		String clientId = conf.getProperty("MicrosoftAppId");
		withCredentials.setAppId(clientId);

		BotFrameworkAdapter adapter = new BotFrameworkAdapter(withCredentials,
		        withAuthConfig,
		        withChannelProvider,
		        null,
		        null);
		
	    return adapter;
	}
	
	@Bean
	@ConditionalOnMissingBean
	public TeamsConversations teamsConversations(
			BotFrameworkAdapter bfa,
			SpringBotAppCredentials appCredentials,
			@Value("${teams.bot.id:}") String id,
			TeamsStateStorage teamsState) {
		ChannelAccount botAccount = new ChannelAccount(id);
		return new StateStorageBasedTeamsConversations(bfa, appCredentials, botAccount, teamsState);
	}

	@Override
	public com.microsoft.bot.integration.Configuration getConfiguration() {
		return new com.microsoft.bot.integration.Configuration() {

			@Override
			public String getProperty(String key) {
				return ac.getEnvironment().getProperty("teams.bot." + key);
			}

			@Override
			public String[] getProperties(String key) {
				throw new UnsupportedOperationException("Couldn't getProperties for " + key);
			}

			@Override
			public Properties getProperties() {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Bean
	@ConditionalOnMissingBean
	public BotFrameworkHttpAdapter getBotFrameworkHttpAdaptor() {
		AdapterWithErrorHandler out = new AdapterWithErrorHandler(getConfiguration());
		return out;
	}

	@Autowired
	ApplicationContext ac;
}
