package com.github.deutschebank.symphony.workflow.java;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Exposed {

	String description() default "";
	
	String[] rooms() default {};
	
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
