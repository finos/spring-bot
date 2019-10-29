package com.github.deutschebank.symphony.spring.api;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;

/**
 * This is going to add extra beans to the spring context for all of the 
 * different symphony APIs.  Since it's possible to declare more than one
 * pod in the configuration, this will default the apis to using the first 
 * listed pod.
 * 
 * If you want multiple beans for the other pods, instantiate other instances of this class.
 * 
 * @author Rob Moffat
 *
 */
public class ApiBeanRegistration implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware {
	
	public static final String BEAN_NAME = "SymphonyApiBeanFactory";
	private static final Logger LOG = LoggerFactory.getLogger(ApiBeanRegistration.class);
	
	String apiBuilderBeanName;
	String packageName;
	String methodName;
	ApplicationContext context;

	public ApiBeanRegistration(String apiBuilderBeanName, String packageName, String methodName) {
		this.apiBuilderBeanName = apiBuilderBeanName;
		this.packageName = packageName;
		this.methodName = methodName;
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		try {
			Set<Class<?>> classesInPackage = getClassesInPackage(packageName);
			
			for (Class<?> c : classesInPackage) {
				BeanDefinition bd = constructBeanDefintion(c);
				registry.registerBeanDefinition("someprefix"+c.getName(), bd);
				LOG.debug("Registered Symphony API Bean: {}", c.getName());
			}
			
			LOG.info("Registered {} beans in package {}", classesInPackage.size(), packageName);
		} catch (Exception e) {
			throw new FatalBeanException("Couldn't create bean definitions for "+packageName, e);
		}
	}

	private Set<Class<?>> getClassesInPackage(String basePackage) throws Exception {
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false) {

			/**
			 * Our beans can be based just on interfaces.
			 */
			@Override
			protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
				AnnotationMetadata metadata = beanDefinition.getMetadata();
				return metadata.isInterface() && metadata.isIndependent();
			}
		};
		scanner.setEnvironment(this.context.getEnvironment());
		scanner.setResourceLoader(this.context);
		scanner.addIncludeFilter(new AnnotationTypeFilter(Path.class));
		Set<Class<?>> entitySet = new HashSet<>();
		for (BeanDefinition candidate : scanner.findCandidateComponents(basePackage)) {
			entitySet.add(ClassUtils.forName(candidate.getBeanClassName(),this.context.getClassLoader()));
		}
		
		return entitySet;
	}

	public BeanDefinition constructBeanDefintion(Class<?> c) {
		GenericBeanDefinition bd = new GenericBeanDefinition();
		bd.setFactoryBeanName(apiBuilderBeanName);
		bd.setFactoryMethodName(methodName);
		ConstructorArgumentValues cav = new ConstructorArgumentValues();
		cav.addGenericArgumentValue(c);
		bd.setConstructorArgumentValues(cav);
		bd.setLazyInit(true);
		bd.setDependsOn(apiBuilderBeanName);
		return bd;
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}
}

