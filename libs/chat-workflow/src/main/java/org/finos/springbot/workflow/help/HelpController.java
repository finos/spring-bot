package org.finos.springbot.workflow.help;

import org.finos.springbot.workflow.annotations.ChatRequest;
import org.finos.springbot.workflow.annotations.Help;
import org.finos.springbot.workflow.annotations.WorkMode;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.content.User;
import org.finos.springbot.workflow.java.mapping.ChatHandlerMapping;
import org.finos.springbot.workflow.java.mapping.ChatHandlerMethod;
import org.finos.springbot.workflow.java.mapping.ChatMapping;
import org.finos.springbot.workflow.response.Response;
import org.finos.springbot.workflow.response.WorkResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.ResolvableType;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.core.annotation.AnnotatedElementUtils.findMergedAnnotation;

/**
 * Builds help menu from the @Exposed annotations.
 * 
 * @author rob@kite9.com
 *
 */
public class HelpController implements ApplicationContextAware {

	private static final Logger LOG = LoggerFactory.getLogger(HelpController.class);
    private static final String DEFAULT_FORMATTER_PATTERN = "(?<!(^|[A-Z]))(?=[A-Z])|(?<!^)(?=[A-Z][a-z])";

	List<ChatHandlerMapping<ChatRequest>> exposedHandlers;

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

	@ChatRequest(value = "help", description="Display this help page")
	public List<Response> handleHelp(Addressable a, User u) {
		initExposedHandlers();
		List<CommandDescription> commands = exposedHandlers
				.stream()
				.flatMap(ec -> ec.getAllHandlers(a, u).stream())
				.filter(hm -> includeInHelp(hm))
				.map(hm -> convertToCommandDescriptions(hm))
				.sorted(Comparator.comparing(CommandDescription::getHelpOrder)
						.thenComparing(CommandDescription::getDescription))
				.collect(Collectors.toList());

		//Collect available args
		Map<Class, Object> availableArgs = new HashMap<>();
		availableArgs.put(Addressable.class, a);
		availableArgs.put(User.class, u);
		availableArgs.put(List.class, commands);

		//Collect methods annotated with @Help
		Map<Method, Help> helpMethods = getHelpAnnotatedMethods();
		//Collect Help page work objects
		List<Object> helpPageWorkObj = getHelpPageWorkObj(availableArgs, helpMethods);
		return generateResponses(a, commands, helpPageWorkObj);
	}

	private List<Response> generateResponses(Addressable a, List<CommandDescription> commands, List<Object> helpPageWorkObj) {
		List<Response> responses = new ArrayList<>();
		if(helpPageWorkObj.isEmpty()) {
			responses.add(new WorkResponse(a, new HelpPage(commands), WorkMode.VIEW));
		} else {
			for (Object helpWorkObj: helpPageWorkObj) {
				responses.add(new WorkResponse(a, helpWorkObj, WorkMode.VIEW));
			}
		}
		return responses;
	}

	private List<Object> getHelpPageWorkObj(Map<Class, Object> availableArgs, Map<Method, Help> helpMethods) {
		List<Object> helpPageWorkObj = new ArrayList<>();

		for (Method helpMethod: helpMethods.keySet()) {
			Object bean = ctx.getBean(helpMethod.getDeclaringClass());
			Object[] args = new Object[helpMethod.getParameterCount()];
			for(int i = 0; i < args.length; i++) {
				args[i] = availableArgs.get(helpMethod.getParameterTypes()[i]);
			}
			try {
				helpPageWorkObj.add(helpMethod.invoke(bean, args));
			} catch (IllegalAccessException | InvocationTargetException e) {
				LOG.error("There was error identifying Help work response - ", e);
			}
		}

		return helpPageWorkObj;
	}

	private Map<Method, Help> getHelpAnnotatedMethods() {
		//Identify method annotated with @Help
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
		scanner.addIncludeFilter(new AnnotationTypeFilter(Controller.class));
		Set<BeanDefinition> helpBeans = new HashSet<>();

		for (String bootAppBeanName : ctx.getBeanNamesForAnnotation(SpringBootApplication.class)) {
			String packageName = getPackageName(ctx.getBean(bootAppBeanName).getClass());
			Set<BeanDefinition> beans = scanner.findCandidateComponents(packageName);
			helpBeans.addAll(beans);
		}

		return helpBeans.stream()
				.map(BeanDefinition::getBeanClassName)
				.map(s -> {
					try {
						return Class.forName(s);
					} catch (ClassNotFoundException e) {
						LOG.warn("Couldn't instantiate: " + s, e);
						return null;
					}
				})
				.filter(Objects::nonNull)
				.flatMap(c -> getHelpAnnotatedMethods(c).entrySet().stream())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	private static String getPackageName(Class<?> c) {
		String cn = c.getName();
		int dot = cn.lastIndexOf('.');
		return (dot != -1) ? cn.substring(0, dot).intern() : "";
	}

	private Map<Method, Help> getHelpAnnotatedMethods(Class<?> targetType) {
		Map<Method, Help> annotatedMethods = null;

		try {
			annotatedMethods = MethodIntrospector.selectMethods(
					targetType,
					(MethodIntrospector.MetadataLookup<Help>) method -> findMergedAnnotation(method, Help.class)
			);
		} catch (Exception ex) {
			// An unresolvable type in a method signature, probably from a lazy bean - let's ignore it.
			LOG.debug("Could not resolve methods for target with name '{}'", targetType.getName(), ex);
		}
		return annotatedMethods == null ? Collections.emptyMap() : annotatedMethods;
	}

	private boolean includeInHelp(ChatMapping<ChatRequest> hm) {
		ChatRequest e = hm.getMapping();
		
		if (!e.addToHelp()) {
			return false;
		}
		
		return true;
	}

	private CommandDescription convertToCommandDescriptions(ChatMapping<ChatRequest> hm) {
		ChatRequest e = hm.getMapping();
		ChatHandlerMethod m = hm.getHandlerMethod();
		String d = StringUtils.hasText(e.description()) ? e.description() : defaultDescription(m.getMethod());
		return new CommandDescription(e.isButtonOnHelpPage(), e.value()[0], d, e.helpOrder(), Arrays.asList(e.value()));
	}

	@SuppressWarnings("unchecked")
	public void initExposedHandlers() {
		if (exposedHandlers == null) {
			ResolvableType rt = ResolvableType.forClassWithGenerics(ChatHandlerMapping.class, ChatRequest.class);
			exposedHandlers = Arrays.stream(ctx.getBeanNamesForType(rt))
				.map(bn -> (ChatHandlerMapping<ChatRequest>) ctx.getBean(bn))
				.collect(Collectors.toList());
				
		}
	}
	
	private ApplicationContext ctx;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.ctx = applicationContext;
	}

}
