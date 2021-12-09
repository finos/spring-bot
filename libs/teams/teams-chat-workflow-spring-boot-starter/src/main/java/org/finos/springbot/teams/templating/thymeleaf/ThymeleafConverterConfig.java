package org.finos.springbot.teams.templating.thymeleaf;

import java.util.List;

import org.finos.springbot.teams.TeamsWorkflowConfig;
import org.finos.springbot.workflow.content.Chat;
import org.finos.springbot.workflow.content.User;
import org.finos.springbot.workflow.templating.BeanConverter;
import org.finos.springbot.workflow.templating.BooleanConverter;
import org.finos.springbot.workflow.templating.ChatConverter;
import org.finos.springbot.workflow.templating.CollectionConverter;
import org.finos.springbot.workflow.templating.DropdownAnnotationConverter;
import org.finos.springbot.workflow.templating.EnumConverter;
import org.finos.springbot.workflow.templating.TextFieldConverter;
import org.finos.springbot.workflow.templating.TypeConverter;
import org.finos.springbot.workflow.templating.UserConverter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureBefore({TeamsWorkflowConfig.class})
public class ThymeleafConverterConfig {
	
	@Bean
	protected ThymeleafRendering thymleafRendering() {
		return new ThymeleafRendering();
	}
	
	@Bean
	protected BeanConverter<String> tlBeanConverter(ThymeleafRendering r) {
		return new BeanConverter<>(r);
	}
	
	@Bean
	protected BooleanConverter<String> tlBooleanConverter(ThymeleafRendering r) {
		return new BooleanConverter<>(r);
	}
	
	@Bean
	protected CollectionConverter<String> tlCollectionConverter(ThymeleafRendering r) {
		return new CollectionConverter<>(r);
	}
	
	@Bean
	protected EnumConverter<String> tlEnumConverter(ThymeleafRendering r) {
		return new EnumConverter<>(r);
	}
	
	@Bean
	protected TimeConverter tlTimeConverter(ThymeleafRendering r) {
		return new TimeConverter(r);
	}
	
	@Bean
	protected TextFieldConverter<String> textFieldConverter(ThymeleafRendering r) {
		return new TextFieldConverter<>(TextFieldConverter.LOW_PRIORITY, r, String.class, 
				Number.class, int.class, float.class, double.class, short.class, long.class, byte.class);
	}
	
	@Bean
	protected UserConverter<String> tlUserConverter(ThymeleafRendering r) {
		return new UserConverter<>(UserConverter.LOW_PRIORITY, r, User.class);
	}
	
	@Bean
	protected ChatConverter<String> tlChatConverter(ThymeleafRendering r) {
		return new ChatConverter<>(ChatConverter.LOW_PRIORITY, r, Chat.class);
	}
	
	@Bean
	protected DropdownAnnotationConverter<String> tlDropdownAnnotationConverter(ThymeleafRendering r) {
		return new DropdownAnnotationConverter<>(r);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public ThymeleafTemplater thymeleafConverter(List<TypeConverter<String>> converters, ThymeleafRendering r) {
		return new ThymeleafTemplater(converters, r);
	}
	
}
