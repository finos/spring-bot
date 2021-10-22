package org.finos.springbot.teams.templating;

import org.finos.springbot.teams.TeamsWorkflowConfig;
import org.finos.springbot.teams.templating.helper.AdaptiveCardRendering;
import org.finos.springbot.workflow.content.Chat;
import org.finos.springbot.workflow.content.User;
import org.finos.springbot.workflow.templating.BeanConverter;
import org.finos.springbot.workflow.templating.BooleanConverter;
import org.finos.springbot.workflow.templating.ChatConverter;
import org.finos.springbot.workflow.templating.DropdownAnnotationConverter;
import org.finos.springbot.workflow.templating.EnumConverter;
import org.finos.springbot.workflow.templating.Rendering;
import org.finos.springbot.workflow.templating.TextFieldConverter;
import org.finos.springbot.workflow.templating.UserConverter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.JsonNode;

@Configuration
@AutoConfigureBefore({TeamsWorkflowConfig.class})
public class AdaptiveCardConverterConfig {
	
	@Bean
	public Rendering<JsonNode> adaptiveCardRendering() {
		return new AdaptiveCardRendering();
	}
	
	@Bean
	public BeanConverter<JsonNode> beanConverter(Rendering<JsonNode> r) {
		return new BeanConverter<JsonNode>(r);
	}
	
	@Bean
	public BooleanConverter booleanConverter(Rendering<JsonNode> r) {
		return new BooleanConverter(r);
	}
	
	@Bean
	public CollectionConverter collectionConverter(Rendering<JsonNode> r) {
		return new CollectionConverter(r);
	}
	
	@Bean
	public EnumConverter<JsonNode> enumConverter(Rendering<JsonNode> r) {
		return new EnumConverter<>(r);
	}
	
	@Bean
	public TimeConverter timeConverter(Rendering<JsonNode> r) {
		return new TimeConverter(r);
	}
	
	@Bean
	public ValidatingTextFieldConverter textFieldConverter(Rendering<JsonNode> r) {
		return new ValidatingTextFieldConverter(TextFieldConverter.LOW_PRIORITY, r, String.class, 
				Number.class, int.class, float.class, double.class, short.class, long.class, byte.class);
	}
	
	@Bean
	public UserConverter<JsonNode> userConverter(Rendering<JsonNode> r) {
		return new UserConverter<>(UserConverter.LOW_PRIORITY, r, User.class);
	}
	
	@Bean
	public ChatConverter<JsonNode> chatConverter(Rendering<JsonNode> r) {
		return new ChatConverter<>(ChatConverter.LOW_PRIORITY, r, Chat.class);
	}
	
	@Bean
	public DropdownAnnotationConverter<JsonNode> dropdownAnnotationConverter(Rendering<JsonNode> r) {
		return new DropdownAnnotationConverter<>(r);
	}
	
}
