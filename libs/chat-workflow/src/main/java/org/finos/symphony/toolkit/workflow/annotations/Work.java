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
	 */
	public String jsonTypeName = "";
	
	public String writeVersion = "1.0";
	
	public String[] readVersions = { "1.0" };
}
