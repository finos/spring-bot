package org.finos.symphony.toolkit.workflow.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.stereotype.Indexed;

/**
 * This indicates that this object is a value object that should be 
 * sent into chats / returned from chats.  
 * 
 * @see VersionSpace in EntityJson project for uses of this.
 * 
 * @author Rob Moffat
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Indexed
public @interface Work {

	/**
	 * Replace this name if you want to give a specific name for the class over the wire.
	 * Where multiple classes are used, the first one is used for serialization, others can be used for 
	 * deserialization backwards compatibility.
	 */
	public String[] jsonTypeName() default { "" };
	
	public String writeVersion() default "1.0";
	
	public String[] readVersions() default { "1.0" };
	
	/**
	 * Set this to true if we want to be able to look up instances of the annotated class in chat history.
	 */
	public boolean index() default true;
}
