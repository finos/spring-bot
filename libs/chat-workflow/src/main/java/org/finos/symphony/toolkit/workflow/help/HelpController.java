package org.finos.symphony.toolkit.workflow.help;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.finos.symphony.toolkit.workflow.annotations.Exposed;
import org.finos.symphony.toolkit.workflow.annotations.Exposed.NoFormClass;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.java.mapping.ChatHandlerMapping;
import org.finos.symphony.toolkit.workflow.java.mapping.ChatHandlerMethod;
import org.finos.symphony.toolkit.workflow.java.mapping.ChatMapping;
import org.finos.symphony.toolkit.workflow.response.FormResponse;
import org.finos.symphony.toolkit.workflow.response.Response;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.ResolvableType;
import org.springframework.util.StringUtils;

/**
 * Builds help menu from the @Exposed annotations.
 * 
 * @author rob@kite9.com
 *
 */
public class HelpController implements ApplicationContextAware {
	
    private static final String DEFAULT_FORMATTER_PATTERN = "(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])";


	List<ChatHandlerMapping<Exposed>> exposedHandlers;

	public HelpController() {
		super();
	}
	
	
	protected String defaultDescription(Method m) {
		return Arrays.stream(Optional.ofNullable(m.getName()).orElse("").split(DEFAULT_FORMATTER_PATTERN))
			.map(word -> {
				return null != word && !word.trim().isEmpty() ? Character.toUpperCase(word.charAt(0)) + word.substring(1)
					: "";
			})
			.collect(Collectors.joining(" "));
	}

	@Exposed(value = "help", description="Display this help page")
	public Response handleHelp(Addressable a, User u) {
		initExposedHandlers();
		List<CommandDescription> commands = exposedHandlers
				.stream()
				.flatMap(ec -> ec.getAllHandlers(a, u).stream())
				.filter(hm -> includeInHelp(hm))
				.map(hm -> convertToCommandDescriptions(hm))
				.sorted((c, b) -> c.getDescription().compareTo(b.getDescription()))
				.collect(Collectors.toList());

		return new FormResponse(a, new HelpPage(commands), false);
	}

	private boolean includeInHelp(ChatMapping<Exposed> hm) {
		Exposed e = hm.getMapping();
		
		if (!e.addToHelp()) {
			return false;
		}
		
		if (e.isButton()) {
			return ((e.formClass() == NoFormClass.class) && 
				(!StringUtils.hasText(e.formName())) && noCurlies(e));
		} else if (e.isMessage()) {
			return true;
		} else {
			return false;
		}
	}


	private boolean noCurlies(Exposed e) {
		return Arrays.stream(e.value()).filter(s -> s.contains("{")).count() == 0;
	}


	private CommandDescription convertToCommandDescriptions(ChatMapping<Exposed> hm) {
		Exposed e = hm.getMapping();
		ChatHandlerMethod m = hm.getHandlerMethod();
		String d = StringUtils.hasText(e.description()) ? e.description() : defaultDescription(m.getMethod());
		return new CommandDescription(d, e.isButton(), e.isMessage(), Arrays.asList(e.value()));
	}

	@SuppressWarnings("unchecked")
	public void initExposedHandlers() {
		if (exposedHandlers == null) {
			ResolvableType rt = ResolvableType.forClassWithGenerics(ChatHandlerMapping.class, Exposed.class);
			exposedHandlers = Arrays.stream(ctx.getBeanNamesForType(rt))
				.map(bn -> (ChatHandlerMapping<Exposed>) ctx.getBean(bn))
				.collect(Collectors.toList());
				
		}
	}
	
	private ApplicationContext ctx;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.ctx = applicationContext;
	}

}
