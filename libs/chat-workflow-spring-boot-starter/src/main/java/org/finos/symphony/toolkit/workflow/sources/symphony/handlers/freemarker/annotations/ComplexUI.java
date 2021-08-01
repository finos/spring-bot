package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * UI component - list of UI types like dropdown, list, etc
 * @author rupnsur
 *
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ComplexUI {
	/**
	 * provide class name, which contains set of values for a dropdown 
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	Class mappedClass();
	
	/**
	 * provide variable name, which holds set of values for a dropdown.
	 * @return
	 */
	String mappedBy();
	
	/**
	 * provide UI type, e.g. ComplexUIType.DROPDOWN
	 * @return
	 */
	ComplexUIType uiType();
	
	/**
	 * provide complex UI template.
	 * @return
	 */
	String template() default "";
	
}