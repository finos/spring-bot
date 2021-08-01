package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import org.finos.symphony.toolkit.workflow.sources.symphony.SymphonyWorkflowConfig;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureBefore({SymphonyWorkflowConfig.class})
public class FreemarkerTypeConverterConfig {

	
	@Bean
	public AuthorConverter authorConverter() {
		return new AuthorConverter();
	}
	
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
	public ComplexUIConverter complexUIConverter() {
		return new ComplexUIConverter();
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
	public IDConverter idConverter() {
		return new IDConverter();
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
	public StringConverter stringConverter() {
		return new StringConverter();
	}
	
	@Bean
	public UserConverter userConverter() {
		return new UserConverter();
	}
}
