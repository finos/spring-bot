package org.finos.symphony.toolkit.workflow.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates that we should provide edit functionality for this object when it is in a chat
 * 
 * @author Rob Moffat
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Work {

	public String name() default "";
	
	public String instructions() default "";
	
	public boolean editable() default false;
	
	public String[] rooms() default {};
}
