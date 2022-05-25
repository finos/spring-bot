package org.finos.springbot;


import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.aspectj.lang.ProceedingJoinPoint;
import org.finos.springbot.workflow.content.Chat;
import org.finos.springbot.workflow.content.User;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Lazy;

import io.micrometer.core.aop.CountedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.Tags;

@Configuration
@ConditionalOnClass(MeterRegistry.class)
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class MeterRegistryConfig {

	@Bean
	@ConditionalOnMissingBean
	@Lazy
	public CountedAspect countedAspect(MeterRegistry registry) {
		Function<ProceedingJoinPoint, Iterable<Tag>> tagsBasedOnJoinPoint = pjp -> Tags.of(getTags(pjp));
		return new CountedAspect(registry, tagsBasedOnJoinPoint);
	}

	private Iterable<Tag> getTags(ProceedingJoinPoint pjp) {
		pjp.getStaticPart();
		
		List<Tag> tags = new LinkedList<>();
		Object[] args = pjp.getArgs();
		Arrays.asList(args).stream().filter(Objects::nonNull).forEach(a -> {
			if (a instanceof Chat) {
				Chat room = (Chat) a;
				tags.add(Tag.of("Chat", room.getName()));
			} else if (a instanceof User) {
				User user = (User) a;
				tags.add(Tag.of("User", user.getName()));
			}
		});
		return tags;
	}

}
