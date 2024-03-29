package org.finos.springbot.teams.templating.thymeleaf;

import java.util.List;

import org.finos.springbot.workflow.annotations.WorkMode;
import org.finos.springbot.workflow.templating.AbstractTopLevelConverter;
import org.finos.springbot.workflow.templating.Mode;
import org.finos.springbot.workflow.templating.Rendering;
import org.finos.springbot.workflow.templating.TypeConverter;
import org.finos.springbot.workflow.templating.Variable;

public class ThymeleafTemplater extends AbstractTopLevelConverter<String, WorkMode> {

	
	public ThymeleafTemplater(List<TypeConverter<String>> fieldConverters, Rendering<String> r) {
		super(fieldConverters, r);
	}

	public static final String JUST_BUTTONS_FORM = "just-buttons-form";
	
	@Override
	public String convert(Class<?> c, Mode m) {
		if ((m==Mode.FORM) || (m==Mode.DISPLAY_WITH_BUTTONS)) {
			throw new UnsupportedOperationException("Teams cannot render editable forms in xml mode, or buttons on html pages.");
		}
		
		Variable v = new ThymeleafVariable("form");
		String inner = apply(null, this, c, false, v, topLevelFieldOutput());
		return
				"<div xmlns:th=\"http://www.thymeleaf.org\">" +
				inner + 
				"</div>";
	}

}
