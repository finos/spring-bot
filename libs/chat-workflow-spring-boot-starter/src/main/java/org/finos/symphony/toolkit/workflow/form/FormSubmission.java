package org.finos.symphony.toolkit.workflow.form;

public class FormSubmission {
	
	public final Class<?> expected;
	public final Object structure;
	
	public FormSubmission(Class<?> expected, Object structure) {
		super();
		this.expected = expected;
		this.structure = structure;
	}
}