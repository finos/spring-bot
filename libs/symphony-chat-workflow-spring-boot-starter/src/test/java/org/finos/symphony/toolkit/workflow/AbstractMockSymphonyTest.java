package org.finos.symphony.toolkit.workflow;

import org.finos.symphony.toolkit.spring.api.SymphonyApiConfig;
import org.finos.symphony.toolkit.workflow.fixture.OurController;
import org.finos.symphony.toolkit.workflow.response.AttachmentResponse;
import org.finos.symphony.toolkit.workflow.sources.symphony.SymphonyWorkflowConfig;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.AttachmentHandler;
import org.junit.jupiter.api.BeforeEach;
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
import com.symphony.api.model.UserV2;
import com.symphony.api.pod.RoomMembershipApi;
import com.symphony.api.pod.StreamsApi;
import com.symphony.api.pod.UsersApi;

@ExtendWith(SpringExtension.class)

@SpringBootTest(classes = { 
		AbstractMockSymphonyTest.MockConfiguration.class, 
	SymphonyWorkflowConfig.class,
})
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
			Mockito.when(usersApi.v2UserGet(Mockito.isNull(), Mockito.nullable(long.class), Mockito.eq(BOT_EMAIL), Mockito.isNull(), Mockito.anyBoolean())).thenReturn(new UserV2().emailAddress(BOT_EMAIL).id(111l));
			return usersApi;
		}
		
		@Bean
		public LocalValidatorFactoryBean localValidatorFactoryBean() {
			return new LocalValidatorFactoryBean();
		}
		
		@Bean
		public AttachmentHandler mockAttachmentHandler() {
			return new AttachmentHandler() {
				
				@Override
				public Object formatAttachment(AttachmentResponse ar) {
					return ar.getAttachment();
				}
			};
		}

		@Bean
		public OurController ourController() {
			return new OurController();
		}
		
		
	}
}
