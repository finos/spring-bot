package org.finos.springbot.teams.templating.adaptivecard;

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

import com.fasterxml.jackson.databind.JsonNode;

@Configuration
@AutoConfigureBefore({TeamsWorkflowConfig.class})
public class AdaptiveCardConverterConfig {
	
	@Bean
	protected AdaptiveCardRendering adaptiveCardRendering() {
		return new AdaptiveCardRendering();
	}
	
	@Bean
	protected BeanConverter<JsonNode> acBeanConverter(AdaptiveCardRendering r) {
		return new BeanConverter<JsonNode>(r);
	}
	
	@Bean
	protected BooleanConverter<JsonNode> acBooleanConverter(AdaptiveCardRendering r) {
		return new BooleanConverter<>(r);
	}
	
	@Bean
	protected CollectionConverter acCollectionConverter(AdaptiveCardRendering r) {
		return new CollectionConverter(r);
	}
	
	@Bean
	protected EnumConverter<JsonNode> acEnumConverter(AdaptiveCardRendering r) {
		return new EnumConverter<>(r);
	}
	
	@Bean
	protected TimeConverter acTimeConverter(AdaptiveCardRendering r) {
		return new TimeConverter(r);
	}
	
	@Bean
	protected ValidatingTextFieldConverter acTextFieldConverter(AdaptiveCardRendering r) {
		return new ValidatingTextFieldConverter(TextFieldConverter.LOW_PRIORITY, r, String.class, 
				Number.class, int.class, float.class, double.class, short.class, long.class, byte.class);
	}
	
	@Bean
	protected UserConverter<JsonNode> acUserConverter(AdaptiveCardRendering r) {
		return new UserConverter<>(UserConverter.LOW_PRIORITY, r, User.class);
	}
	
	@Bean
	protected ChatConverter<JsonNode> acChatConverter(AdaptiveCardRendering r) {
		return new ChatConverter<>(ChatConverter.LOW_PRIORITY, r, Chat.class);
	}
	
	@Bean
	protected DropdownAnnotationConverter<JsonNode> acDropdownAnnotationConverter(AdaptiveCardRendering r) {
		return new DropdownAnnotationConverter<>(r);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public AdaptiveCardTemplater adaptiveCardConverter(List<TypeConverter<JsonNode>> converters, AdaptiveCardRendering r) {
		return new AdaptiveCardTemplater(converters, r);
	}
	
}
