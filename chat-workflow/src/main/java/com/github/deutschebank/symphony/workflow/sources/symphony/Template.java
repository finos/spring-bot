package com.github.deutschebank.symphony.workflow.sources.symphony;

public @interface Template {

	public String view() default "";
	
	public String edit() default "";
}
