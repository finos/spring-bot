package org.finos.springbot.teams.conversations;

import java.util.Properties;

import org.finos.springbot.teams.bot.BotDependencyConfiguration;
import org.finos.springbot.teams.state.TeamsStateStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.azure.identity.ClientCertificateCredential;
import com.azure.identity.ClientCertificateCredentialBuilder;
import com.microsoft.bot.builder.BotFrameworkAdapter;
import com.microsoft.bot.connector.authentication.MicrosoftAppCredentials;
import com.microsoft.bot.integration.AdapterWithErrorHandler;
import com.microsoft.bot.integration.BotFrameworkHttpAdapter;
import com.microsoft.bot.schema.ChannelAccount;

public class TeamsConversationsConfig extends BotDependencyConfiguration {

	@Bean
	public SpringBotMicrosoftAppCredentials microsoftCredentials(@Value("${teams.app.tennantId}") String tennantId) {
		com.microsoft.bot.integration.Configuration conf = getConfiguration();

		String clientId = conf.getProperty(MicrosoftAppCredentials.MICROSOFTAPPID);

		ClientCertificateCredential clientCertificateCredential = new ClientCertificateCredentialBuilder()
				.tenantId(tennantId)
				.clientId(clientId)
				.pemCertificate(conf.getProperty("some-certificate	"))
				.clientCertificatePassword("hello")
				.build();

		SpringBotMicrosoftAppCredentials out = new SpringBotMicrosoftAppCredentials(tennantId,
				clientId, clientCertificateCredential);

		return out;
	}

	@Bean
	@ConditionalOnMissingBean
	public TeamsConversations teamsConversations(
			BotFrameworkAdapter bfa,
			SpringBotMicrosoftAppCredentials mac,
			@Value("${teams.bot.id:}") String id,
			TeamsStateStorage teamsState) {
		ChannelAccount botAccount = new ChannelAccount(id);
		return new StateStorageBasedTeamsConversations(bfa, mac, botAccount, teamsState);
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
