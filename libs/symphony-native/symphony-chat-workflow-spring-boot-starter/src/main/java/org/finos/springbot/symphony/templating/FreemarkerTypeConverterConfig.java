package org.finos.springbot.symphony.templating;

import java.util.List;

import org.finos.springbot.symphony.SymphonyWorkflowConfig;
import org.finos.springbot.workflow.templating.BeanConverter;
import org.finos.springbot.workflow.templating.BooleanConverter;
import org.finos.springbot.workflow.templating.DropdownAnnotationConverter;
import org.finos.springbot.workflow.templating.EnumConverter;
import org.finos.springbot.workflow.templating.Rendering;
import org.finos.springbot.workflow.templating.TextFieldConverter;
import org.finos.springbot.workflow.templating.TypeConverter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureBefore({SymphonyWorkflowConfig.class})
public class FreemarkerTypeConverterConfig {
	
	@Bean
	protected Rendering<String>symphonyFreeMarkerRendering() {
		return new FreemarkerRendering();
	}
	
	
	@Bean
	public BeanConverter<String> beanConverter(FreemarkerRendering r) {
		return new BeanConverter<>(r);
	}
	
	@Bean
	public BooleanConverter<String> booleanConverter(FreemarkerRendering r) {
		return new BooleanConverter<>(r);
	}
//	
//	@Bean
//	public CollectionConverter collectionConverter() {
//		return new CollectionConverter();
//	}
//	
	@Bean
	public EnumConverter<String> enumConverter(FreemarkerRendering r) {
		return new EnumConverter<>(r);
	}
	
	@Bean
	public HashTagConverter hashTagConverter(FreemarkerRendering r) {
		return new HashTagConverter(r);
	}
	
	@Bean
	public CashTagConverter cashTagConverter(FreemarkerRendering r) {
		return new CashTagConverter(r);
	}
	
//	@Bean
//	public TimeConverter timeConverter(FreemarkerRendering r) {
//		return new TimeConverter(r);
//	}
//	
	@Bean
	public TextFieldConverter<String> textFieldConverter(Rendering<String> r) {
		return new TextFieldConverter<String>(TextFieldConverter.LOW_PRIORITY, r, String.class, 
				Number.class, int.class, float.class, double.class, short.class, long.class, byte.class);
	}
	
//	
//	@Bean
//	public ChatConverter<String> roomConverter(FreemarkerRendering r) {
//		return new ChatConverter<String>(r);
//	}
	
	@Bean
	public DropdownAnnotationConverter<String> dropdownAnnotationConverter(FreemarkerRendering r) {
		return new DropdownAnnotationConverter<>(r);
	}
	
	@Bean
	public UserConverter userConverter(FreemarkerRendering r) {
		return new UserConverter(r);
	}
	
	
	@Bean
	@ConditionalOnMissingBean
	public FreemarkerWorkTemplater formMessageMLConverter(List<TypeConverter<String>> converters, FreemarkerRendering r) {
		return new FreemarkerWorkTemplater(converters, r);
	}
}
