package org.finos.springbot.symphony.templating;

import java.util.List;

import org.finos.springbot.symphony.SymphonyWorkflowConfig;
import org.finos.springbot.symphony.content.SymphonyRoom;
import org.finos.springbot.workflow.content.Chat;
import org.finos.springbot.workflow.content.User;
import org.finos.springbot.workflow.templating.BeanConverter;
import org.finos.springbot.workflow.templating.BooleanConverter;
import org.finos.springbot.workflow.templating.ChatConverter;
import org.finos.springbot.workflow.templating.DropdownAnnotationConverter;
import org.finos.springbot.workflow.templating.EnumConverter;
import org.finos.springbot.workflow.templating.Rendering;
import org.finos.springbot.workflow.templating.TableConverter;
import org.finos.springbot.workflow.templating.TextFieldConverter;
import org.finos.springbot.workflow.templating.TypeConverter;
import org.finos.springbot.workflow.templating.UserConverter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AutoConfigureBefore({SymphonyWorkflowConfig.class})
public class FreemarkerTypeConverterConfig {
	
	@Bean
	protected FreemarkerRendering symphonyFreeMarkerRendering() {
		return new FreemarkerRendering();
	}
	
	@Bean
	protected BeanConverter<String> fmBeanConverter(FreemarkerRendering r) {
		return new BeanConverter<>(r);
	}
	
	@Bean
	protected BooleanConverter<String> fmBooleanConverter(FreemarkerRendering r) {
		return new BooleanConverter<>(r);
	}
	
	@Bean
	protected TableConverter<String> fmCollectionConverter(FreemarkerRendering r) {
		return new TableConverter<>(r);
	}
	
	@Bean
	protected EnumConverter<String> fmEnumConverter(FreemarkerRendering r) {
		return new EnumConverter<>(r);
	}
	
//	@Bean
//	public HashTagConverter hashTagConverter(FreemarkerRendering r) {
//		return new HashTagConverter(r);
//	}
//	
//	@Bean
//	public CashTagConverter cashTagConverter(FreemarkerRendering r) {
//		return new CashTagConverter(r);
//	}
//	
//	@Bean
//	public TimeConverter timeConverter(FreemarkerRendering r) {
//		return new TimeConverter(r);
//	}
//	
	@Bean
	protected TextFieldConverter<String> fmTextFieldConverter(Rendering<String> r) {
		return new TextFieldConverter<String>(TextFieldConverter.LOW_PRIORITY, r, String.class, 
				Number.class, int.class, float.class, double.class, short.class, long.class, byte.class);
	}
	
	@Bean
	protected ChatConverter<String> fmRoomConverter(FreemarkerRendering r) {
		return new ChatConverter<String>(ChatConverter.LOW_PRIORITY, r, SymphonyRoom.class, Chat.class);
	}
	
	@Bean
	protected DropdownAnnotationConverter<String> fmDropdownAnnotationConverter(FreemarkerRendering r) {
		return new DropdownAnnotationConverter<>(r);
	}
	
	@Bean
	protected UserConverter<String> rmUserConverter(FreemarkerRendering r) {
		return new UserConverter<>(UserConverter.LOW_PRIORITY, r, User.class);
	}
	
	
	@Bean
	@ConditionalOnMissingBean
	public FreemarkerWorkTemplater formMessageMLConverter(List<TypeConverter<String>> converters, FreemarkerRendering r) {
		return new FreemarkerWorkTemplater(converters, r);
	}
}
