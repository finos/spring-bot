package org.finos.springbot.symphony.templating;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import org.finos.springbot.workflow.templating.Rendering;
import org.finos.springbot.workflow.templating.Variable;

public class FreemarkerRendering implements Rendering<String> {

	@Override
	public String description(String d) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String list(Class<?> of, List<String> contents, boolean editable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String addFieldName(String field, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String renderDropdown(Variable variable, String location, String key, String value, boolean editable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String renderDropdown(Variable variable, Map<String, String> options, boolean editable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String textField(Variable variable, boolean editable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String checkBox(Variable variable, boolean editable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String collection(Type t, Variable v, String in, boolean editable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String button(String name, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String buttons(String location) {
		// TODO Auto-generated method stub
		return null;
	}

}
