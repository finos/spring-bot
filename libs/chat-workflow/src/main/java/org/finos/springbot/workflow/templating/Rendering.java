package org.finos.springbot.workflow.templating;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

public interface Rendering<X> {

	X description(String d);

	X list(List<X> contents);

	X addFieldName(String field, X value);
		
	X renderUserDropdown(
			Variable variable, 
			String optionLocation, 
			String optionKey, 
			String optionValue,
			boolean editable);
	
	X renderDropdown(
		Variable variable, 
		String variableKey,
		String optionLocation, 
		String optionKey, 
		String optionValue,
		boolean editable);
	
	X renderDropdown(
			Variable variable, 
			String variableKey,
			Map<String, String> options,
			boolean editable);

	X textField(Variable variable, boolean editable);
	
	X checkBox(Variable variable, boolean editable);

	X collection(Type t, Variable collection, Variable item, X in, boolean editable);

	X button(String text, String id);
	
	X buttons(String location);
	
	public default String extend(String with) {
		return StringUtils.hasText(with) ? "." + with : "";
	}

	X userDisplay(Variable v);

}
