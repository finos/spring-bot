package org.finos.springbot.symphony.templating;

import java.util.List;

import org.finos.springbot.workflow.annotations.WorkMode;
import org.finos.springbot.workflow.response.WorkResponse;
import org.finos.springbot.workflow.templating.AbstractTopLevelConverter;
import org.finos.springbot.workflow.templating.Mode;
import org.finos.springbot.workflow.templating.Rendering;
import org.finos.springbot.workflow.templating.TypeConverter;
import org.finos.springbot.workflow.templating.Variable;

/**
 * Takes a bean and converts it into a form with either an editable or display
 * version of MessageML.
 * 
 * @author Rob Moffat
 *
 */
public class FreemarkerWorkTemplater extends AbstractTopLevelConverter<String, WorkMode> {

	
	public static final String JUST_BUTTONS_FORM = "just-buttons-form";
	
	public FreemarkerWorkTemplater(List<TypeConverter<String>> fieldConverters, Rendering<String> r) {
		super(fieldConverters, r);
	}
	
	@Override
	public String convert(Class<?> c, Mode m) { //, Object o, ButtonList actions, boolean editMode, Errors e, EntityJson work) {
		Variable v = new FreemarkerVariable("entity."+WorkResponse.OBJECT_KEY);
		
		
		StringBuilder sb = new StringBuilder();
		sb.append("\n<#-- starting template -->");
		
		if (m == Mode.FORM) {
			sb.append("\n<form id=\"" + c.getCanonicalName() + "\">");
		} 
		
		sb.append(apply(null, this, c, m==Mode.FORM, v, topLevelFieldOutput()));
		
		if (m == Mode.DISPLAY_WITH_BUTTONS) {
			// the form is created here just to contain these buttons.
			sb.append("\n<form id=\"" + JUST_BUTTONS_FORM + "\">");
			sb.append(handleButtons());
			sb.append("\n</form>");
		} else if (m == Mode.FORM) { 
			sb.append(handleButtons());
			sb.append("\n</form>");
		} 

		sb.append("\n<#-- ending template -->\n");
		return sb.toString();
	}

	private String handleButtons() {
		StringBuilder sb = new StringBuilder();
		sb.append("\n  <p><#list entity.buttons.contents as button>");
		sb.append("\n    <button ");
		sb.append("\n         name=\"${button.name}\"");
		sb.append("\n         type=\"${button.buttonType?lower_case}\">");
		sb.append("\n      ${button.text}");
		sb.append("\n    </button>");
		sb.append("\n  </#list></p>");
		return sb.toString();
	}
	
	

}
