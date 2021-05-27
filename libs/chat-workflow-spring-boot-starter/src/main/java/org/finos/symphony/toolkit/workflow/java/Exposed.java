package org.finos.symphony.toolkit.workflow.java;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Exposed {

	String[] value();
		
	String description() default "";
	
	String[] rooms() default {};
	
	/**
	 * This means that the exposed method only applies buttons on a given form.
	 */
	String formName() default ""; 
	
	boolean addToHelp() default true;
	
	/**
	 * Whether this method can be exposed as a button
	 */
	boolean isButton() default true;
	
	/**
	 * Whether this method can be called by typing it's name.
	 */
	boolean isMessage() default true;
	
}
