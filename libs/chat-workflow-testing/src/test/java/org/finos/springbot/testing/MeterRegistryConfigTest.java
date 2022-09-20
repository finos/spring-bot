package org.finos.springbot.testing;

import org.finos.springbot.MeterRegistryConfig;
import org.finos.springbot.testing.content.TestRoom;
import org.finos.springbot.workflow.content.Chat;
import org.finos.springbot.workflow.content.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

public class MeterRegistryConfigTest {

	private MeterRegistryConfig meterRegistryConfig = new MeterRegistryConfig();
	private final MeterRegistry meterRegistry = new SimpleMeterRegistry();
	private final CountedService countedService = getAdvisedService(new CountedService());

	@Test
	void countedWithoutSuccessfulMetricsAndChatAndUserTag() {
		Chat chat = new TestRoom("test key", "Test name");
		User user = getUser();

		countedService.succeedWithMetrics(chat, user);
		Counter counter = meterRegistry.get("metric.success").tag("Chat", chat.getName()).tag("User", user.getName())
				.counter();
		
		Assertions.assertEquals(counter.count(), 1);
	}

	@Test
	void countedWithoutSuccessfulMetricsAndChatTag() {
		Chat chat = new TestRoom("test key", "Test name");

		countedService.succeedWithMetrics(chat, null);
		Counter counter = meterRegistry.get("metric.success").tag("Chat", chat.getName()).counter();
		Assertions.assertEquals(counter.count(), 1);
	}

	@Test
	void countedWithoutSuccessfulMetricsAndUserTag() {
		User user = getUser();

		countedService.succeedWithMetrics(null, user);
		Counter counter = meterRegistry.get("metric.success").tag("User", user.getName()).counter();
		Assertions.assertEquals(counter.count(), 1);
	}

	private CountedService getAdvisedService(CountedService countedService) {
		AspectJProxyFactory proxyFactory = new AspectJProxyFactory(countedService);
		proxyFactory.addAspect(meterRegistryConfig.countedAspect(meterRegistry));
		return proxyFactory.getProxy();
	}

	private User getUser() {
		return new User() {

			@Override
			public Type getTagType() {
				return null;
			}

			@Override
			public String getName() {
				return "test@db.com";
			}

			@Override
			public String getKey() {
				return null;
			}
		};
	}

	class CountedService {
		@Counted(value = "metric.success")
		void succeedWithMetrics(Chat chat, User user) {

		}
	}
}
