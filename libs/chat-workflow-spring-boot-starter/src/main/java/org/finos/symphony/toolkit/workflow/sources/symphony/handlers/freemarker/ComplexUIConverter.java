package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.ResourceLoaderUtil;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.annotations.ComplexUI;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.annotations.ComplexUIType;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

/**
 * @author rupnsur
 *
 */
public class ComplexUIConverter extends AbstractComplexUIConverter implements ApplicationContextAware {

	private ApplicationContext applicationContext;

	public ComplexUIConverter() {
		super(LOW_PRIORITY);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean canConvert(Type t) {
		if (((Class) t).isAnnotation()) {
			return t.getTypeName().equals(ComplexUI.class.getCanonicalName());
		} else {
			return false;
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public String apply(WithType controller, Type t, boolean editMode, Variable variable, WithField showDetail,
			Annotation a) {

		if (editMode) {
			ComplexUI ui = (ComplexUI) a;
			String mappedBy = ui.mappedBy();
			Class mappedClass = ui.mappedClass();
			ComplexUIType complexUIType = ui.uiType();
			String templateName = ui.template();

			if (StringUtils.hasText(templateName)) {
				ResourceLoaderUtil resourceLoaderUtil = applicationContext.getBean(ResourceLoaderUtil.class);
				return resourceLoaderUtil.readTemplateToString(templateName);
			} else
				return createComplexUIElement(variable, mappedBy, mappedClass.getSimpleName(), complexUIType);
		} else {
			return text(variable, "!''");
		}
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}