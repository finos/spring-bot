package org.finos.symphony.toolkit.workflow.form;

/**
 * This is used when a form cannot be easily mapped to a java object.
 * 
 * @author rob@kite9.com
 *
 */
public class FormSubmission {
	
	public final String formName;
	public final Object structure;
	
	public FormSubmission(String formName, Object structure) {
		super();
		this.formName = formName;
		this.structure = structure;
	}
}