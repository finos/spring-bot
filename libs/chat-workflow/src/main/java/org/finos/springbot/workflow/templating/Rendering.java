package org.finos.springbot.workflow.templating;

import java.util.List;
import java.util.function.BiFunction;
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
		Function<String, String> sourceFunction, 
		Function<String, String> keyFunction, 
		BiFunction<String, String, String> valueFunction);

	X textField(Variable variable, Function<X, X> change);


}
