package org.finos.springbot.workflow.templating;

import org.finos.springbot.workflow.annotations.Work;

/**
 * The main interface for converting {@link Work}-annotated beans into templates
 */
public interface WorkTemplater<O, MODE> {
	
	O convert(Class<?> c, MODE m);

}