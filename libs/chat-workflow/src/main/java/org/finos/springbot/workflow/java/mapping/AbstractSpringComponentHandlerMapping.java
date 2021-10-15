package org.finos.springbot.workflow.java.mapping;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.finos.springbot.workflow.content.Chat;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.core.MethodIntrospector;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public abstract class AbstractSpringComponentHandlerMapping<T> extends ApplicationObjectSupport implements ChatHandlerMapping<T>, InitializingBean {

	private boolean detectHandlerMethodsInAncestorContexts = false;
	
	protected MappingRegistry mappingRegistry = new MappingRegistry();
	
	/**
	 * 
	 * Detects handler methods at initialization.
	 * 
	 * @see #initHandlerMethods
	 */
	@Override
	public void afterPropertiesSet() {
		initHandlerMethods();
	}

	/**
	 * Scan beans in the ApplicationContext, detect and register handler methods.
	 * 
	 * @see #getCandidateBeanNames()
	 * @see #processCandidateBean
	 * @see #handlerMethodsInitialized
	 */
	protected void initHandlerMethods() {
		String[] candidateBeanNames = getCandidateBeanNames();
		for (String beanName : candidateBeanNames) {
			processCandidateBean(beanName);
		}
		handlerMethodsInitialized(getHandlerMethods());
	}
	
	/**
	 * Invoked after all handler methods have been detected.
	 * @param handlerMethods a read-only map with handler methods and mappings.
	 */
	protected void handlerMethodsInitialized(Map<T, ChatHandlerMethod> handlerMethods) {
		// Total includes detected mappings + explicit registrations via registerMapping
		int total = handlerMethods.size();
		if ((logger.isTraceEnabled() && total == 0) || (logger.isDebugEnabled() && total > 0) ) {
			logger.debug(total + " mappings in " + this.getClass().getCanonicalName());
		}
	}
	
	/**
	 * Determine the names of candidate beans in the application context.
	 * @since 5.1
	 * @see #setDetectHandlerMethodsInAncestorContexts
	 * @see BeanFactoryUtils#beanNamesForTypeIncludingAncestors
	 */
	protected String[] getCandidateBeanNames() {
		return (this.detectHandlerMethodsInAncestorContexts ?
				BeanFactoryUtils.beanNamesForTypeIncludingAncestors(obtainApplicationContext(), Object.class) :
				obtainApplicationContext().getBeanNamesForType(Object.class));
	}
	
	/**
	 * Determine the type of the specified candidate bean and call
	 * {@link #detectHandlerMethods} if identified as a handler type.
	 * <p>This implementation avoids bean creation through checking
	 * {@link org.springframework.beans.factory.BeanFactory#getType}
	 * and calling {@link #detectHandlerMethods} with the bean name.
	 * @param beanName the name of the candidate bean
	 * @since 5.1
	 * @see #isHandler
	 * @see #detectHandlerMethods
	 */
	protected void processCandidateBean(String beanName) {
		Object bean = null;
		try {
			ApplicationContext ctx = obtainApplicationContext();
			bean = ctx.getBean(beanName);
		}
		catch (Throwable ex) {
			// An unresolvable bean type, probably from a lazy bean - let's ignore it.
			if (logger.isTraceEnabled()) {
				logger.trace("Could not resolve type for bean '" + beanName + "'", ex);
			}
		}
		if (bean != null) {
			
			detectHandlerMethods(bean);
		}
	}
	
	/**
	 * Return a (read-only) map with all mappings and HandlerMethod's.
	 */
	public Map<T, ChatHandlerMethod> getHandlerMethods() {
		this.mappingRegistry.acquireReadLock();
		try {
			return Collections.unmodifiableMap(
					this.mappingRegistry.getRegistrations().entrySet().stream()
							.collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().handlerMethod)));
		}
		finally {
			this.mappingRegistry.releaseReadLock();
		}
	}

	/**
	 * Look for handler methods in the specified handler bean.
	 * @param handler either a bean name or an actual handler instance
	 * @see #getMappingForMethod
	 */
	protected void detectHandlerMethods(Object handler) {
		Class<?> handlerType = (handler instanceof String ?
				obtainApplicationContext().getType((String) handler) : handler.getClass());

		if (handlerType != null) {
			Class<?> userType = ClassUtils.getUserClass(handlerType);
			Map<Method, T> methods = MethodIntrospector.selectMethods(userType,
					(MethodIntrospector.MetadataLookup<T>) method -> {
						try {
							return getMappingForMethod(method, userType);
						}
						catch (Throwable ex) {
							throw new IllegalStateException("Invalid mapping on handler class [" +
									userType.getName() + "]: " + method, ex);
						}
					});
			if (logger.isTraceEnabled()) {
				logger.trace(formatMappings(userType, methods));
			}
			
			methods.forEach((method, mapping) -> {
				Method invocableMethod = AopUtils.selectInvocableMethod(method, userType);
				registerHandlerMethod(handler, invocableMethod, mapping);
			});
		}
	}

	protected void registerHandlerMethod(Object handler, Method method, T mapping) {
		this.mappingRegistry.register(mapping, handler, method);
	}

	
	
	@Nullable
	protected abstract T getMappingForMethod(Method method, Class<?> handlerType);
	
	private String formatMappings(Class<?> userType, Map<Method, T> methods) {
		String formattedType = Arrays.stream(ClassUtils.getPackageName(userType).split("\\."))
				.map(p -> p.substring(0, 1))
				.collect(Collectors.joining(".", "", "." + userType.getSimpleName()));
		Function<Method, String> methodFormatter = method -> Arrays.stream(method.getParameterTypes())
				.map(Class::getSimpleName)
				.collect(Collectors.joining(",", "(", ")"));
		return methods.entrySet().stream()
				.map(e -> {
					Method method = e.getKey();
					return e.getValue() + ": " + method.getName() + methodFormatter.apply(method);
				})
				.collect(Collectors.joining("\n\t", "\n\t" + formattedType + ":" + "\n\t", ""));
	}
	
	
	/**
	 * A registry that maintains all mappings to handler methods, exposing methods
	 * to perform lookups and providing concurrent access.
	 *
	 * <p>Package-private for testing purposes.
	 */
	class MappingRegistry {

		private final Map<T, MappingRegistration<T>> registry = new HashMap<>();

		private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

		/**
		 * Return all registrations.
		 * @since 5.3
		 */
		public Map<T, MappingRegistration<T>> getRegistrations() {
			return this.registry;
		}

		/**
		 * Acquire the read lock when using getMappings and getMappingsByUrl.
		 */
		public void acquireReadLock() {
			this.readWriteLock.readLock().lock();
		}

		/**
		 * Release the read lock after using getMappings and getMappingsByUrl.
		 */
		public void releaseReadLock() {
			this.readWriteLock.readLock().unlock();
		}

		public void register(T mapping, Object handler, Method method) {
			this.readWriteLock.writeLock().lock();
			try {
				ChatHandlerMethod handlerMethod = createHandlerMethod(handler, method);
				validateMethodMapping(handlerMethod, mapping);
				
				this.registry.put(mapping, createMappingRegistration(mapping, handlerMethod));
			}
			finally {
				this.readWriteLock.writeLock().unlock();
			}
		}
		
		protected ChatHandlerMethod createHandlerMethod(Object handler, Method method) {
			if (handler instanceof String) {
				return new ChatHandlerMethod((String) handler,
						obtainApplicationContext().getAutowireCapableBeanFactory(), method);
			}
			return new ChatHandlerMethod(handler, method);
		}
		

		private void validateMethodMapping(ChatHandlerMethod handlerMethod, T mapping) {
			ChatMapping<T> registration = this.registry.get(mapping);
			ChatHandlerMethod existingHandlerMethod = (registration != null ? registration.getHandlerMethod() : null);
			if (existingHandlerMethod != null && !existingHandlerMethod.equals(handlerMethod)) {
				throw new IllegalStateException(
						"Ambiguous mapping. Cannot map '" + handlerMethod.getBean() + "' method \n" +
						handlerMethod + "\nto " + mapping + ": There is already '" +
						existingHandlerMethod.getBean() + "' bean method\n" + existingHandlerMethod + " mapped. \n" +
						"Try setting value / buttonText to disambiguate the annotations. ");
			}
		}

		public void unregister(T mapping) {
			this.readWriteLock.writeLock().lock();
			try {
				this.registry.remove(mapping);
			}
			finally {
				this.readWriteLock.writeLock().unlock();
			}
		}
	}


	abstract static class MappingRegistration<T> implements ChatMapping<T> {

		private final T mapping;

		private final ChatHandlerMethod handlerMethod;
		
		private final String name;

		public MappingRegistration(
				T mapping, ChatHandlerMethod handlerMethod) {

			Assert.notNull(mapping, "Mapping must not be null");
			Assert.notNull(handlerMethod, "HandlerMethod must not be null");
			this.mapping = mapping;
			this.handlerMethod = handlerMethod;
			this.name = handlerMethod.getBeanType().getName() + "-" + handlerMethod.getMethod().getName();
		}

		@Override
		public T getMapping() {
			return this.mapping;
		}

		@Override
		public ChatHandlerMethod getHandlerMethod() {
			return this.handlerMethod;
		}

		@Override
		public String getUniqueName() {
			return name;
		}
		
		protected boolean roomMatched(String[] rooms, Chat addressable) {
			for (String r : rooms) {
				if (addressable.getName().equals(r)) {
					return true;
				}
			}
			
			return false;
		}

		
		
	}

	protected abstract MappingRegistration<T> createMappingRegistration(T mapping, ChatHandlerMethod handlerMethod);

	
}
