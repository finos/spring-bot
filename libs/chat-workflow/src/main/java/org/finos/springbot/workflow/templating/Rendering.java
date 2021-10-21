package org.finos.springbot.workflow.templating;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface Rendering<X> {

	X description(String d);
	
	X text(Variable v);

	X propertyPanel(List<X> contents);

	X property(String field, X value);
	
	X button(String name, String text);
	
	X renderDropdown(
		Variable variable, 
		String location, 
		String key, 
		String value);
	
	X renderDropdown(
			Variable variable, 
			Map<String, String> options);
	
	X renderDropdownView(
		Variable variable, 
		String location, 
		String key, 
		String value);
	
	X renderDropdownView(
			Variable variable, 
			Map<String, String> options);

	X textField(Variable variable, Function<X, X> change);


}
