package org.finos.springbot.sources.teams.handlers.adaptivecard;

import org.finos.springbot.sources.teams.TeamsWorkflowConfig;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureBefore({TeamsWorkflowConfig.class})
public class FreemarkerTypeConverterConfig {
	
	@Bean
	public BeanConverter beanConverter() {
		return new BeanConverter();
	}
	
	@Bean
	public BooleanConverter booleanConverter() {
		return new BooleanConverter();
	}
	
	@Bean
	public CollectionConverter collectionConverter() {
		return new CollectionConverter();
	}
	
	@Bean
	public EnumConverter enumConverter() {
		return new EnumConverter();
	}
	
	@Bean
	public HashTagConverter hashTagConverter() {
		return new HashTagConverter();
	}
	
	@Bean
	public CashTagConverter cashTagConverter() {
		return new CashTagConverter();
	}
	
	@Bean
	public TimeConverter timeConverter() {
		return new TimeConverter();
	}
	
	@Bean
	public NumberConverter numberConverter() {
		return new NumberConverter();
	}
	
	@Bean
	public RoomConverter roomConverter() {
		return new RoomConverter();
	}
	
	@Bean
	public DropdownAnnotationConverter dropdownAnnotationConverter() {
		return new DropdownAnnotationConverter();
	}
	
	@Bean
	public StringConverter stringConverter() {
		return new StringConverter();
	}
	
	@Bean
	public UserConverter userConverter() {
		return new UserConverter();
	}
}
