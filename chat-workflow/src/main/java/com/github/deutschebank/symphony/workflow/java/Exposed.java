package com.github.deutschebank.symphony.workflow.java;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Exposed {

	String description() default "";
	
	String[] rooms() default {};
	
}
