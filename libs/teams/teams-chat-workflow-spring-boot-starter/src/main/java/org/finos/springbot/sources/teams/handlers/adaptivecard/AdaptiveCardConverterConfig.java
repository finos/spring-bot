package org.finos.springbot.sources.teams.handlers.adaptivecard;

import org.finos.springbot.sources.teams.TeamsWorkflowConfig;
import org.finos.springbot.sources.teams.handlers.adaptivecard.helper.AdaptiveCardRendering;
import org.finos.springbot.workflow.templating.BeanConverter;
import org.finos.springbot.workflow.templating.Rendering;
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
//	
//	@Bean
//	public EnumConverter enumConverter(Rendering<JsonNode> r) {
//		return new EnumConverter();
//	}
//	
	@Bean
	public TimeConverter timeConverter(Rendering<JsonNode> r) {
		return new TimeConverter(r);
	}
	
	@Bean
	public NumberConverter numberConverter(Rendering<JsonNode> r) {
		return new NumberConverter(r);
	}
	
	@Bean
	public UserConverter userConverter(Rendering<JsonNode> r) {
		return new UserConverter(r);
	}
	
//	@Bean
//	public DropdownAnnotationConverter dropdownAnnotationConverter() {
//		return new DropdownAnnotationConverter();
//	}
	
	@Bean
	public StringConverter stringConverter(Rendering<JsonNode> r) {
		return new StringConverter(r);
	}
	
}
