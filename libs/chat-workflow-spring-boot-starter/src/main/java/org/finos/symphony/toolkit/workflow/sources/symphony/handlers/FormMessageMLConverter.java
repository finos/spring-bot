package org.finos.symphony.toolkit.workflow.sources.symphony.handlers;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.form.ButtonList;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.annotations.ComplexUI;
import org.springframework.context.ApplicationContext;
import org.springframework.validation.Errors;

public interface FormMessageMLConverter extends GetFields {

	String convert(Class<?> c, Object o, ButtonList actions, boolean editMode, Errors e, EntityJson work);

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public default void readComplexUIComponent(ApplicationContext applicationContext, Class<?> c, EntityJson work) {
		List<Field> fields = getFields(c);
		for (Field f : fields) {
			ComplexUI d = f.getAnnotation(ComplexUI.class);
			if (d != null) {
				try {
					String mappedBy = d.mappedBy();
					Class mappedClass = d.mappedClass();
					Object mappedBean = (Object) applicationContext.getBean(mappedClass);
					Object values = new PropertyDescriptor(mappedBy, mappedClass).getReadMethod().invoke(mappedBean);

					if (values instanceof Set)
						work.put(mappedClass.getSimpleName(), mappedBean);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
}