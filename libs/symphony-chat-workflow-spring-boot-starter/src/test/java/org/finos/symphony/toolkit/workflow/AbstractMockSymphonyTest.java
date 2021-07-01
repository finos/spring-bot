package org.finos.symphony.toolkit.workflow;

import org.finos.symphony.toolkit.spring.api.SymphonyApiConfig;
import org.finos.symphony.toolkit.workflow.fixture.TestWorkflowConfig;
import org.finos.symphony.toolkit.workflow.java.perform.PerformerConfig;
import org.finos.symphony.toolkit.workflow.java.resolvers.ResolverConfig;
import org.finos.symphony.toolkit.workflow.sources.symphony.SymphonyBot;
import org.finos.symphony.toolkit.workflow.sources.symphony.SymphonyWorkflowConfig;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.AuthorConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.BeanConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.BooleanConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.CashTagConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.CollectionConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.EnumConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.HashTagConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.IDConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.InstantConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.NumberConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.RoomConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.StringConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.UserConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.jersey.JerseyAttachmentHandlerConfig;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.symphony.api.agent.DatafeedApi;
import com.symphony.api.agent.MessagesApi;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.model.User;
import com.symphony.api.pod.RoomMembershipApi;
import com.symphony.api.pod.StreamsApi;
import com.symphony.api.pod.UsersApi;

@ExtendWith(SpringExtension.class)

@SpringBootTest(classes = { 
		AuthorConverter.class,
		BeanConverter.class,
		BooleanConverter.class, 
		CashTagConverter.class,
		CollectionConverter.class,
		EnumConverter.class,
		HashTagConverter.class,
		IDConverter.class,
		InstantConverter.class,
		NumberConverter.class,
		RoomConverter.class,
		StringConverter.class,
		UserConverter.class,
		PerformerConfig.class,
		ResolverConfig.class,
		TestWorkflowConfig.class, 
		SymphonyWorkflowConfig.class,
		AbstractMockSymphonyTest.MockConfiguration.class, 
		JerseyAttachmentHandlerConfig.class })
public abstract class AbstractMockSymphonyTest {

	private static final String BOT_EMAIL = "dummybot@example.com";
	
	@MockBean
	MessagesApi messagesApi;
	
	@MockBean
	DatafeedApi datafeedApi;
	
	@MockBean
	RoomMembershipApi rmApi;
	
	@MockBean
	StreamsApi streamsApi;
	
	/**
	 * This is here, otherwise we start the real symphony bot talking to mock endpoints, which 
	 * wouldn't work.
	 */
	@MockBean
	SymphonyBot symphonyBot;
	
	@Configuration
	public static class MockConfiguration {
	
		@Bean(name=SymphonyApiConfig.SINGLE_BOT_IDENTITY_BEAN)
		public SymphonyIdentity symphonyIdentity() {
			SymphonyIdentity botIdentity = Mockito.mock(SymphonyIdentity.class);
			Mockito.when(botIdentity.getEmail()).then((i) -> BOT_EMAIL);
			return botIdentity;
		}
		
		@Bean
		public UsersApi usersApi() {
			UsersApi usersApi = Mockito.mock(UsersApi.class);
			Mockito.when(usersApi.v1UserGet(Mockito.eq(BOT_EMAIL), Mockito.isNull(), Mockito.anyBoolean())).thenReturn(new User().emailAddress(BOT_EMAIL).id(111l));
			return usersApi;
		}
		
		@Bean
		public LocalValidatorFactoryBean localValidatorFactoryBean() {
			return new LocalValidatorFactoryBean();
		}
		

	}
}
