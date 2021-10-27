package org.finos.symphony.toolkit.workflow;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.finos.springbot.symphony.response.handlers.AttachmentHandler;
import org.finos.springbot.tests.controller.AbstractHandlerMappingTest;
import org.finos.springbot.tests.controller.OurController;
import org.finos.springbot.workflow.response.AttachmentResponse;
import org.finos.symphony.toolkit.spring.api.SymphonyApiConfig;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.symphony.api.agent.DatafeedApi;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.model.MemberInfo;
import com.symphony.api.model.MembershipList;
import com.symphony.api.model.RoomAttributes;
import com.symphony.api.model.RoomDetail;
import com.symphony.api.model.Stream;
import com.symphony.api.model.UserV2;
import com.symphony.api.model.V3RoomAttributes;
import com.symphony.api.model.V3RoomDetail;
import com.symphony.api.pod.RoomMembershipApi;
import com.symphony.api.pod.StreamsApi;
import com.symphony.api.pod.UsersApi;

@Configuration
public class SymphonyMockConfiguration {
	
	@MockBean
	DatafeedApi datafeedApi;
	
	@Bean(name=SymphonyApiConfig.SINGLE_BOT_IDENTITY_BEAN)
	public SymphonyIdentity symphonyIdentity() {
		SymphonyIdentity botIdentity = Mockito.mock(SymphonyIdentity.class);
		Mockito.when(botIdentity.getEmail()).then((i) -> AbstractHandlerMappingTest.BOT_EMAIL);
		return botIdentity;
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
	
	@Bean
	public StreamsApi streamsApi() {
		StreamsApi streamsApi = Mockito.mock(StreamsApi.class);
		Mockito.when(streamsApi.v1ImCreatePost(Mockito.anyList(), Mockito.isNull()))
				.thenReturn(new Stream().id(AbstractHandlerMappingTest.CHAT_ID));
		
		Mockito.when(streamsApi.v3RoomIdInfoGet(Mockito.eq(AbstractHandlerMappingTest.CHAT_ID), Mockito.isNull()))
				.thenReturn(new V3RoomDetail()
					.roomAttributes(new V3RoomAttributes().name(OurController.SOME_ROOM)));
		
		return streamsApi;
	}
	
	@Bean
	public UsersApi usersApi() {
		UsersApi usersApi = Mockito.mock(UsersApi.class);
		
		when(usersApi.v2UserGet(Mockito.isNull(), Mockito.isNull(), Mockito.eq(AbstractHandlerMappingTest.BOT_EMAIL), Mockito.isNull(), Mockito.anyBoolean()))
			.thenReturn(new UserV2().emailAddress(AbstractHandlerMappingTest.BOT_EMAIL).id(AbstractHandlerMappingTest.BOT_ID));
		
		when(usersApi.v2UserGet(Mockito.isNull(), Mockito.isNull(), Mockito.eq(AbstractHandlerMappingTest.ROB_EXAMPLE_EMAIL), Mockito.isNull(), Mockito.anyBoolean()))
			.thenReturn(new UserV2().emailAddress(AbstractHandlerMappingTest.ROB_EXAMPLE_EMAIL).id(AbstractHandlerMappingTest.ROB_EXAMPLE_ID));
		
		when(usersApi.v2UserGet(Mockito.isNull(), Mockito.nullable(long.class), Mockito.eq(AbstractHandlerMappingTest.BOT_EMAIL), Mockito.isNull(), Mockito.anyBoolean()))
			.thenReturn(new UserV2().emailAddress(AbstractHandlerMappingTest.BOT_EMAIL).id(111l));
		
		when(usersApi.v2UserGet(any(), any(), any(), any(), any()))
			.then(a -> new UserV2().id(AbstractHandlerMappingTest.ROB_EXAMPLE_ID).displayName(AbstractHandlerMappingTest.ROB_NAME).emailAddress(AbstractHandlerMappingTest.ROB_EXAMPLE_EMAIL));
		
		return usersApi;
	}

	@Bean
	public RoomMembershipApi roomMembershipApi() throws Exception {
		RoomMembershipApi rmApi = Mockito.mock(RoomMembershipApi.class);
		MembershipList out = new MembershipList();
		out.add(new MemberInfo().id(AbstractHandlerMappingTest.BOT_ID).owner(false));
		out.add(new MemberInfo().id(AbstractHandlerMappingTest.ROB_EXAMPLE_ID).owner(true));
		Mockito.when(rmApi.v2RoomIdMembershipListGet(Mockito.anyString(), Mockito.isNull())).thenReturn(out);
		return rmApi;
	}
	
}