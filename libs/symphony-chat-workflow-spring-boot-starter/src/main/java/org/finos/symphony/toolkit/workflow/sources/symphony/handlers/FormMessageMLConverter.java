package org.finos.symphony.toolkit.workflow.sources.symphony.handlers;

public interface FormMessageMLConverter {
	
	public enum Mode {
		
		/**
		 * Create a template to display the contents of the class.
		 */
		DISPLAY, 	
		
		/**
		 * Create a template to display the contents of the class, with some buttons.
		 */
		DISPLAY_WITH_BUTTONS, 
		
		/**
		 * Create a form for editing the contents of the class, with buttons.
		 */
		FORM
	}

	String convert(Class<?> c, Mode m);

}