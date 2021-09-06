package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.finos.symphony.toolkit.workflow.response.WorkResponse;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.FormMessageMLConverter;

/**
 * Takes a bean and converts it into a form with either an editable or display
 * version of MessageML.
 * 
 * @author Rob Moffat
 *
 */
public class FreemarkerFormMessageMLConverter implements FormMessageMLConverter, WithType {

	
	public static final String JUST_BUTTONS_FORM = "just-buttons-form";

	private List<TypeConverter> converters;
	
	public FreemarkerFormMessageMLConverter(List<TypeConverter> fieldConverters) {
		this.converters = new ArrayList<TypeConverter>(fieldConverters);
		Collections.sort(this.converters, (a, b) -> Integer.compare(a.getPriority(), b.getPriority()));
	}
	
	@Override
	public String convert(Class<?> c, Mode m) { //, Object o, ButtonList actions, boolean editMode, Errors e, EntityJson work) {
		Variable v = new Variable("entity."+WorkResponse.OBJECT_KEY);
		
		
		StringBuilder sb = new StringBuilder();
		sb.append("\n<#-- starting template -->");
		
		if (m == Mode.FORM) {
			sb.append("\n<form " + AbstractTypeConverter.attribute(v, "id", c.getCanonicalName()) + ">");
		} 
		
		sb.append(apply(null, this, c, m==Mode.FORM, v, topLevelFieldOutput()));
		
		if (m == Mode.DISPLAY_WITH_BUTTONS) {
			// the form is created here just to contain these buttons.
			sb.append("\n<form " + AbstractTypeConverter.attribute(v, "id", JUST_BUTTONS_FORM) + ">");
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
	
	

	
	@Override
	public TypeConverter getConverter(Field ctx, Type t, WithType ownerController) {
		for(TypeConverter fc : converters) {
			if (fc.canConvert(ctx, t)) {
				return fc;
			}
		} 
		
		throw new UnsupportedOperationException("No converter found for "+t);
	}

	@Override
	public String apply(Field ctx, WithType controller, Type t, boolean editMode, Variable variable, WithField context) {
		TypeConverter tc = getConverter(ctx, t, controller);
		return tc.apply(ctx, controller, t, editMode, variable, context);
	}

	/**
	 * This is the with-field apply.  It doesn't add any wrapper onto the output.
	 */
	public WithField topLevelFieldOutput() {
		
		return new WithField() {
			public String apply(Field f, boolean editMode, Variable variable, WithType contentHandler) {
				Type t = f.getGenericType();
				return contentHandler.apply(f, FreemarkerFormMessageMLConverter.this, t, editMode, variable, topLevelFieldOutput());
			}

			@Override
			public boolean expand() {
				return true;
			}
		};
		
	}

}
