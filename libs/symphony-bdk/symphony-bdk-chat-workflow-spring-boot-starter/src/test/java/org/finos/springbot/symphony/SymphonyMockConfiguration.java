package org.finos.springbot.symphony;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;

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

    // Provide explicit lenient Mockito mocks with unique names and pre-stubbed behavior
    @Bean(name = "mockStreamService")
    @Primary
    public StreamService streamsApi() {
        StreamService mock = Mockito.mock(StreamService.class, Mockito.withSettings().lenient());
        // Adjust <Long> to <String> if your BDK uses List<String>
        doReturn(new Stream().id(AbstractHandlerMappingTest.CHAT_ID))
            .when(mock).create(org.mockito.ArgumentMatchers.<Long>anyList());
        doReturn(new V3RoomDetail().roomAttributes(new V3RoomAttributes().name(OurController.SOME_ROOM)))
            .when(mock).getRoomInfo(anyString());
        doReturn(Arrays.asList(
                new MemberInfo().id(AbstractHandlerMappingTest.BOT_ID).owner(false),
                new MemberInfo().id(AbstractHandlerMappingTest.ROB_EXAMPLE_ID).owner(true)))
            .when(mock).listRoomMembers(anyString());
        return mock;
    }

    @Bean(name = "mockUserService")
    @Primary
    public UserService usersApi() {
        UserService mock = Mockito.mock(UserService.class, Mockito.withSettings().lenient());
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
        doReturn(Collections.singletonList(botUser))
            .when(mock).listUsersByEmails(eq(Collections.singletonList(AbstractHandlerMappingTest.BOT_EMAIL)), Mockito.anyBoolean(), Mockito.anyBoolean());
        doReturn(Collections.singletonList(robUser))
            .when(mock).listUsersByEmails(eq(Collections.singletonList(AbstractHandlerMappingTest.ROB_EXAMPLE_EMAIL)), Mockito.anyBoolean(), Mockito.anyBoolean());
        doReturn(Collections.singletonList(botUser))
            .when(mock).listUsersByIds(eq(Collections.singletonList(AbstractHandlerMappingTest.BOT_ID)), Mockito.anyBoolean(), Mockito.anyBoolean());
        doReturn(Collections.singletonList(robUser))
            .when(mock).listUsersByIds(eq(Collections.singletonList(AbstractHandlerMappingTest.ROB_EXAMPLE_ID)), Mockito.anyBoolean(), Mockito.anyBoolean());
        doReturn(new V2UserDetail()
                .userSystemInfo(new UserSystemInfo().id(AbstractHandlerMappingTest.ROB_EXAMPLE_ID))
                .userAttributes(new V2UserAttributes()
                    .displayName(AbstractHandlerMappingTest.ROB_NAME)
                    .emailAddress(AbstractHandlerMappingTest.ROB_EXAMPLE_EMAIL)))
            .when(mock).getUserDetail(anyLong());
        return mock;
    }

    @Bean(name = "mockSessionService")
    @Primary
    public SessionService sessionApi() {
        SessionService mock = Mockito.mock(SessionService.class, Mockito.withSettings().lenient());
        doReturn(new UserV2()
                .emailAddress(AbstractHandlerMappingTest.BOT_EMAIL)
                .id(AbstractHandlerMappingTest.BOT_ID))
            .when(mock).getSession();
        return mock;
    }

    @Bean(name = "mockAuthSession")
    @Primary
    public AuthSession authSession() {
        return Mockito.mock(AuthSession.class, Mockito.withSettings().lenient());
    }

    @Bean(name = "mockDatafeedLoop")
    @Primary
    public DatafeedLoop datafeedLoop() {
        return Mockito.mock(DatafeedLoop.class, Mockito.withSettings().lenient());
    }

    @Bean
    public LocalValidatorFactoryBean localValidatorFactoryBean() {
        return new LocalValidatorFactoryBean();
    }

    @Bean
    public OurController ourController() {
        return new OurController();
    }
}