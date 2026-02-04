package org.finos.springbot.teams.templating.thymeleaf;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.thymeleaf.autoconfigure.ThymeleafProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.spring6.ISpringTemplateEngine;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ITemplateResolver;

@Configuration
@EnableConfigurationProperties(ThymeleafProperties.class)
@Profile("teams")
public class ThymeleafEngineConfig {

	/**
	 * this prevents thymleaf trying to auto-configure a template-engine, which clashes with bdk.
	 * This code is taken from {@link ThymeleafAutoConfiguration}, but with the bean-rename.
	 */
	@Bean(name = "templateEngineTL")
	@ConditionalOnMissingBean(ISpringTemplateEngine.class)
	SpringTemplateEngine templateEngine(ThymeleafProperties properties,
			ObjectProvider<ITemplateResolver> templateResolvers, ObjectProvider<IDialect> dialects) {
		SpringTemplateEngine engine = new SpringTemplateEngine();
		engine.setEnableSpringELCompiler(properties.isEnableSpringElCompiler());
		engine.setRenderHiddenMarkersBeforeCheckboxes(properties.isRenderHiddenMarkersBeforeCheckboxes());
		templateResolvers.orderedStream().forEach(engine::addTemplateResolver);
		dialects.orderedStream().forEach(engine::addDialect);
		return engine;
	}
	
}
