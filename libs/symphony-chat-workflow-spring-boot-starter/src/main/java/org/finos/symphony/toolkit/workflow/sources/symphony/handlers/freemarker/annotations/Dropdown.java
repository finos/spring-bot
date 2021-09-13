package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.AbstractDropdownConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.AbstractDropdownConverter.ElementFormat;

/**
 * Mark a field with this interface to allow it to use dropdown options.
 * 
 * @author rob@kite9.com
 *
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Dropdown {

	/**
	 * Dictates where in the JSON structure to get the options for the dropdown
	 */
	public String data() default "entity.dropdown";
	
	public Class<? extends ElementFormat> format() default AbstractDropdownConverter.MapFormat.class;
}
