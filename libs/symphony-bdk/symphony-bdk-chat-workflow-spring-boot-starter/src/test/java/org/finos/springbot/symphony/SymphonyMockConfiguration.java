package org.finos.springbot.symphony;

import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;

import org.finos.springbot.tests.controller.AbstractHandlerMappingTest;
import org.finos.springbot.tests.controller.OurController;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
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
public class SymphonyMockConfiguration {

    // Use unique bean names to avoid collisions with BdkApiClientsConfig
    @Bean(name = "mockStreamsApi")
    @Primary
    public StreamService mockStreamsApi() {
        StreamService mock = Mockito.mock(StreamService.class);
        when(mock.create(Mockito.anyList()))
            .thenReturn(new Stream().id(AbstractHandlerMappingTest.CHAT_ID));

        when(mock.getRoomInfo(Mockito.eq(AbstractHandlerMappingTest.CHAT_ID)))
            .thenReturn(new V3RoomDetail()
                .roomAttributes(new V3RoomAttributes().name(OurController.SOME_ROOM)));

        when(mock.listRoomMembers(Mockito.anyString()))
            .thenReturn(Arrays.asList(
                new MemberInfo().id(AbstractHandlerMappingTest.BOT_ID).owner(false),
                new MemberInfo().id(AbstractHandlerMappingTest.ROB_EXAMPLE_ID).owner(true)));
        return mock;
    }

    @Bean(name = "mockUsersApi")
    @Primary
    public UserService mockUsersApi() {
        UserService mock = Mockito.mock(UserService.class);

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

        when(mock.listUsersByEmails(Mockito.eq(Collections.singletonList(AbstractHandlerMappingTest.BOT_EMAIL)), Mockito.anyBoolean(), Mockito.anyBoolean()))
            .thenReturn(Collections.singletonList(botUser));

        when(mock.listUsersByEmails(Mockito.eq(Collections.singletonList(AbstractHandlerMappingTest.ROB_EXAMPLE_EMAIL)), Mockito.anyBoolean(), Mockito.anyBoolean()))
            .thenReturn(Collections.singletonList(robUser));

        when(mock.listUsersByIds(Mockito.eq(Collections.singletonList(AbstractHandlerMappingTest.BOT_ID)), Mockito.anyBoolean(), Mockito.anyBoolean()))
            .thenReturn(Collections.singletonList(botUser));

        when(mock.listUsersByIds(Mockito.eq(Collections.singletonList(AbstractHandlerMappingTest.ROB_EXAMPLE_ID)), Mockito.anyBoolean(), Mockito.anyBoolean()))
            .thenReturn(Collections.singletonList(robUser));

        when(mock.getUserDetail(Mockito.anyLong()))
            .thenReturn(new V2UserDetail()
                    .userSystemInfo(new UserSystemInfo().id(AbstractHandlerMappingTest.ROB_EXAMPLE_ID))
                    .userAttributes(new V2UserAttributes()
                            .displayName(AbstractHandlerMappingTest.ROB_NAME)
                            .emailAddress(AbstractHandlerMappingTest.ROB_EXAMPLE_EMAIL)));
        return mock;
    }

    @Bean(name = "mockAuthSession")
    @Primary
    public AuthSession mockAuthSession() {
        return Mockito.mock(AuthSession.class);
    }

    @Bean(name = "mockDatafeedLoop")
    @Primary
    public DatafeedLoop mockDatafeedLoop() {
        return Mockito.mock(DatafeedLoop.class);
    }

    @Bean(name = "mockSessionService")
    @Primary
    public SessionService mockSessionService() {
        SessionService mock = Mockito.mock(SessionService.class);
        when(mock.getSession()).thenReturn(
            new UserV2()
                .emailAddress(AbstractHandlerMappingTest.BOT_EMAIL)
                .id(AbstractHandlerMappingTest.BOT_ID));
        return mock;
    }

    @Bean
    public LocalValidatorFactoryBean localValidatorFactoryBean() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public org.finos.springbot.tests.controller.OurController ourController() {
        return new OurController();
    }

}