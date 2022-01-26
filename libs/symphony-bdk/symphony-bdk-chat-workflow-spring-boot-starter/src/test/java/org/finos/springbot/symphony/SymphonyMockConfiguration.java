package org.finos.springbot.symphony;

import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.finos.springbot.tests.controller.AbstractHandlerMappingTest;
import org.finos.springbot.tests.controller.OurController;
import org.mockito.Mockito;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockReset;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.symphony.bdk.core.auth.AuthSession;
import com.symphony.bdk.core.service.datafeed.DatafeedLoop;
import com.symphony.bdk.core.service.session.SessionService;
import com.symphony.bdk.core.service.stream.StreamService;
import com.symphony.bdk.core.service.user.UserService;
import com.symphony.bdk.gen.api.model.MemberInfo;
import com.symphony.bdk.gen.api.model.Stream;
import com.symphony.bdk.gen.api.model.UserSystemInfo;
import com.symphony.bdk.gen.api.model.UserV2;
import com.symphony.bdk.gen.api.model.V2UserAttributes;
import com.symphony.bdk.gen.api.model.V2UserDetail;
import com.symphony.bdk.gen.api.model.V3RoomAttributes;
import com.symphony.bdk.gen.api.model.V3RoomDetail;


@TestConfiguration
public class SymphonyMockConfiguration implements InitializingBean {

	@MockBean(reset = MockReset.NONE)
	StreamService streamsApi;
	
	@MockBean(reset = MockReset.NONE)
	UserService usersApi;
	
	@MockBean(reset = MockReset.NONE)
	AuthSession authSession;
	
	@MockBean
	DatafeedLoop datafeedLoop;
	
	@MockBean
	SessionService sessionApi;
	
	@Bean
	public LocalValidatorFactoryBean localValidatorFactoryBean() {
		return new LocalValidatorFactoryBean();
	}
	
	
	
	@Bean
	public OurController ourController() {
		return new OurController();
	}
	
	public void mockSessionApi() {
		Mockito.when(sessionApi.getSession()).thenReturn(
			new UserV2()
				.emailAddress(AbstractHandlerMappingTest.BOT_EMAIL)
				.id(AbstractHandlerMappingTest.BOT_ID));
	}
	
	public void mockStreamsApi() {
		Mockito.when(streamsApi.create(Mockito.anyList()))
				.thenReturn(new Stream().id(AbstractHandlerMappingTest.CHAT_ID));
		
		Mockito.when(streamsApi.getRoomInfo(Mockito.eq(AbstractHandlerMappingTest.CHAT_ID)))
				.thenReturn(new V3RoomDetail()
					.roomAttributes(new V3RoomAttributes().name(OurController.SOME_ROOM)));
		
		Mockito.when(streamsApi.listRoomMembers(Mockito.anyString()))
			.thenReturn(Arrays.asList(
				new MemberInfo().id(AbstractHandlerMappingTest.BOT_ID).owner(false),
				new MemberInfo().id(AbstractHandlerMappingTest.ROB_EXAMPLE_ID).owner(true)));
		
	}
	
	public void mockUsersApi() {		
		UserV2 botUser = new UserV2()
			.username(AbstractHandlerMappingTest.BOT_NAME)
			.displayName(AbstractHandlerMappingTest.BOT_NAME)
			.emailAddress(AbstractHandlerMappingTest.BOT_EMAIL)
			.id(AbstractHandlerMappingTest.BOT_ID);

		UserV2 robUser = new UserV2()
				.username(AbstractHandlerMappingTest.ROB_NAME)
				.displayName(AbstractHandlerMappingTest.ROB_NAME)
				.emailAddress(AbstractHandlerMappingTest.ROB_EXAMPLE_EMAIL)
				.id(AbstractHandlerMappingTest.ROB_EXAMPLE_ID);
		
		when(usersApi.listUsersByEmails(Mockito.eq(Collections.singletonList(AbstractHandlerMappingTest.BOT_EMAIL)), Mockito.anyBoolean(), Mockito.anyBoolean()))
			.thenReturn(Collections.singletonList(botUser));
		
		when(usersApi.listUsersByEmails(Mockito.eq(Collections.singletonList(AbstractHandlerMappingTest.ROB_EXAMPLE_EMAIL)), Mockito.anyBoolean(), Mockito.anyBoolean()))
			.thenReturn(Collections.singletonList(robUser));
		
		when(usersApi.listUsersByIds(Mockito.eq(Collections.singletonList(AbstractHandlerMappingTest.BOT_ID)), Mockito.anyBoolean(), Mockito.anyBoolean()))
			.thenReturn(Collections.singletonList(botUser));
		
		when(usersApi.listUsersByIds(Mockito.eq(Collections.singletonList(AbstractHandlerMappingTest.ROB_EXAMPLE_ID)), Mockito.anyBoolean(), Mockito.anyBoolean()))
			.thenReturn(Collections.singletonList(robUser));
		
		when(usersApi.getUserDetail(Mockito.anyLong()))
			.thenReturn(new V2UserDetail()
					.userSystemInfo(new UserSystemInfo().id(AbstractHandlerMappingTest.ROB_EXAMPLE_ID))
					.userAttributes(new V2UserAttributes()
							.displayName(AbstractHandlerMappingTest.ROB_NAME)
							.emailAddress(AbstractHandlerMappingTest.ROB_EXAMPLE_EMAIL)));
		
		
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		mockStreamsApi();
		mockUsersApi();
		mockSessionApi();
	}

}