package org.finos.springbot.symphony.templating;

import java.util.List;

import org.finos.springbot.symphony.SymphonyWorkflowConfig;
import org.finos.springbot.symphony.content.SymphonyRoom;
import org.finos.springbot.symphony.content.SymphonyUser;
import org.finos.springbot.workflow.content.Chat;
import org.finos.springbot.workflow.content.User;
import org.finos.springbot.workflow.templating.BeanConverter;
import org.finos.springbot.workflow.templating.BooleanConverter;
import org.finos.springbot.workflow.templating.ChatConverter;
import org.finos.springbot.workflow.templating.DropdownAnnotationConverter;
import org.finos.springbot.workflow.templating.EnumConverter;
import org.finos.springbot.workflow.templating.TableConverter;
import org.finos.springbot.workflow.templating.TextFieldConverter;
import org.finos.springbot.workflow.templating.TypeConverter;
import org.finos.springbot.workflow.templating.UserConverter;
import org.springframework.beans.factory.annotation.Qualifier;
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
	@Qualifier("freemarker")
	protected BeanConverter<String> fmBeanConverter(FreemarkerRendering r) {
		return new BeanConverter<>(r);
	}
	
	@Bean
	@Qualifier("freemarker")
	protected BooleanConverter<String> fmBooleanConverter(FreemarkerRendering r) {
		return new BooleanConverter<>(r);
	}
	
	@Bean
	@Qualifier("freemarker")
	protected TableConverter<String> fmCollectionConverter(FreemarkerRendering r) {
		return new TableConverter<>(r);
	}
	
	@Bean
	@Qualifier("freemarker")
	protected EnumConverter<String> fmEnumConverter(FreemarkerRendering r) {
		return new EnumConverter<>(r);
	}
	
	@Bean
	@Qualifier("freemarker")
	public UserConverter<String> hashTagConverter(FreemarkerRendering r) {
		return new UserConverter<String>(UserConverter.LOW_PRIORITY, r, SymphonyUser.class);
	}

//	@Bean
//	public TimeConverter timeConverter(FreemarkerRendering r) {
//		return new TimeConverter(r);
//	}
//	
	@Bean
	@Qualifier("freemarker")
	protected TextFieldConverter<String> fmTextFieldConverter(FreemarkerRendering r) {
		return new TextFieldConverter<String>(TextFieldConverter.LOW_PRIORITY, r, String.class, 
				Number.class, int.class, float.class, double.class, short.class, long.class, byte.class);
	}
	
	@Bean
	@Qualifier("freemarker")
	protected ChatConverter<String> fmRoomConverter(FreemarkerRendering r) {
		return new ChatConverter<String>(ChatConverter.LOW_PRIORITY, r, SymphonyRoom.class, Chat.class);
	}
	
	@Bean
	@Qualifier("freemarker")
	protected DropdownAnnotationConverter<String> fmDropdownAnnotationConverter(FreemarkerRendering r) {
		return new DropdownAnnotationConverter<>(r);
	}
	
	@Bean
	@Qualifier("freemarker")
	protected UserConverter<String> rmUserConverter(FreemarkerRendering r) {
		return new UserConverter<>(UserConverter.LOW_PRIORITY, r, User.class);
	}
	
	
	@Bean
	@ConditionalOnMissingBean
	public FreemarkerWorkTemplater formMessageMLConverter(@Qualifier("freemarker") List<TypeConverter<String>> converters, FreemarkerRendering r) {
		return new FreemarkerWorkTemplater(converters, r);
	}
}
