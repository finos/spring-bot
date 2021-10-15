package org.finos.springbot.workflow.templating;

import java.util.List;

public interface Rendering<X> {

	X description(String d);
	
	X text(Variable v);

	X propertyPanel(List<X> contents);

	X property(String field, X value);
	
	X button(String name, String text);

}
