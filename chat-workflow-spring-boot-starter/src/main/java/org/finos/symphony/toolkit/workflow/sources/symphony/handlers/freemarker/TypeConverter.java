package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Type;

/**
 * Add instances of this bean to your project for custom Freemarker type 
 * conversions.
 * 
 * @author rob@kite9.com
 *
 */
public interface TypeConverter extends WithType {

	/**
	 * Higher priority classes are tested first, so ensure your class has higher priority than
	 * the built-in ones. i.e. AbstractTypeConverter.MED_PRIORITY
	 * @return
	 */
	public int getPriority();
	
	/**
	 * Return true if your class should be used to convert this type.
	 */
	public boolean canConvert(Type t);
		
}
