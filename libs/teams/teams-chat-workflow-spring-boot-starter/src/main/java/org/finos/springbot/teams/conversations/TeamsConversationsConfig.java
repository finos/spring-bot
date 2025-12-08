package org.finos.springbot.teams.conversations;

import java.util.Properties;

import org.finos.springbot.teams.bot.BotDependencyConfiguration;
import org.finos.springbot.teams.state.TeamsStateStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.microsoft.bot.builder.BotFrameworkAdapter;
import com.microsoft.bot.integration.AdapterWithErrorHandler;
import com.microsoft.bot.integration.BotFrameworkHttpAdapter;
import com.microsoft.bot.schema.ChannelAccount;

public class TeamsConversationsConfig extends BotDependencyConfiguration {

	@Bean
	public SpringBotAppCredentials  microsoftCredentials(@Value("${teams.app.tennantId}") String tennantId) {
		com.microsoft.bot.integration.Configuration conf = getConfiguration();
		
		String clientId = conf.getProperty(com.microsoft.bot.connector.authentication.MicrosoftAppCredentials.MICROSOFTAPPID);
		
		SpringBotAppCredentials out = new SpringBotMicrosoftAppCredentials(tennantId,
				clientId, conf.getProperty("MicrosoftAppIdPemCertificate"), conf.getProperty("MicrosoftAppIdPemCertificatePassword"));
		
//		MicrosoftAppCredentials mac = new MicrosoftAppCredentials(
//				conf.getProperty(MicrosoftAppCredentials.MICROSOFTAPPID),
//				conf.getProperty(MicrosoftAppCredentials.MICROSOFTAPPPASSWORD),
//				tennantId);
		
		return out;
	}
	
	
	@Bean 
	@ConditionalOnMissingBean
	public TeamsConversations teamsConversations(
			BotFrameworkAdapter bfa, 
			SpringBotMicrosoftAppCredentials appCredentials, 
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
				return ac.getEnvironment().getProperty("teams.bot."+key);
			}
			
			@Override
			public String[] getProperties(String key) {
				throw new UnsupportedOperationException("Couldn't getProperties for "+key);
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
