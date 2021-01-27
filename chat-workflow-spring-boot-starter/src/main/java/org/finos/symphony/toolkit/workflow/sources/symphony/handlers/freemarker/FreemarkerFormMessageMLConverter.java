package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.form.ButtonList;
import org.finos.symphony.toolkit.workflow.form.ErrorMap;
import org.finos.symphony.toolkit.workflow.sources.symphony.Template;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.EntityJsonConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.FormMessageMLConverter;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

/**
 * Takes a bean and converts it into a form with either an editable or display
 * version of MessageML.
 * 
 * @author Rob Moffat
 *
 */
public class FreemarkerFormMessageMLConverter implements FormMessageMLConverter, WithField {

	
	public static final String JUST_BUTTONS_FORM = "just-buttons-form";

	private ResourceLoader rl;
	private List<FieldConverter> converters;
	
	public FreemarkerFormMessageMLConverter(ResourceLoader rl, List<FieldConverter> fieldConverters) {
		this.rl = rl;
		this.converters = new ArrayList<FieldConverter>(fieldConverters);
		Collections.sort(this.converters, (a, b) -> Integer.compare(a.getPriority(), b.getPriority()));
	}
	
	@Override
	public String convert(Class<?> c, Object o, ButtonList actions, boolean editMode, Errors e, EntityJson work) {
		Variable v;
		
		// ensure o is in the work object
		if (editMode) {
			work.put("formdata", o);
			v = new Variable("entity.formdata");
		} else {
			if (o != null) {
				work.put(EntityJsonConverter.WORKFLOW_001, o);
			}
			v = new Variable("entity."+EntityJsonConverter.WORKFLOW_001);
		}
		
		work.put("errors", convertErrorsToMap(e));
		work.put("buttons", actions);
		
		Template t = c.getAnnotation(Template.class);
		String templateName = t == null ? null : (editMode ? t.edit() : t.view());
		
		if (templateName != null) {
			Resource r = rl.getResource(templateName);
			if (!r.exists()) {
				throw new UnsupportedOperationException("Template not available: "+templateName);
			}
			try {
				return StreamUtils.copyToString(r.getInputStream(), Charset.defaultCharset());
			} catch (IOException e1) {
				throw new UnsupportedOperationException("Template not available:", e1);
			}
		}
		
		
		StringBuilder sb = new StringBuilder();
		sb.append("\n<#-- starting template -->");
		Mode m = editMode ? Mode.FORM : ((actions.size() > 0) ? Mode.DISPLAY_WITH_BUTTONS : Mode.DISPLAY);
		if (o instanceof String) {
			sb.append(o.toString());
		} else {
			// convert to an object form
			if (m == Mode.FORM) {
				sb.append("\n<form " + AbstractFieldConverter.attribute(v, "id", c.getCanonicalName()) + ">");
			} else {
				sb.append("\n<table>");
			}
			if (m == Mode.FORM) {
				sb.append(withFields(c, formField, true, v, work));
			} else {
				sb.append(withFields(c, formDisplay, false, v, work));
			}
			if (m == Mode.DISPLAY_WITH_BUTTONS) {
				sb.append("\n</table>\n<form " + AbstractFieldConverter.attribute(v, "id", JUST_BUTTONS_FORM) + ">");
				sb.append(handleButtons(actions, work));
				sb.append("\n</form>");
			} else if (m == Mode.FORM) {
				sb.append(handleButtons(actions, work));
				sb.append("\n</form>");
			} else {
				sb.append("\n</table>");
			}
		} 

		sb.append("\n<#-- ending template -->\n");
		return sb.toString();
	}

	private ErrorMap convertErrorsToMap(Errors e) {
		return e == null ? new ErrorMap() : new ErrorMap(e.getAllErrors().stream()
			.map(err -> (FieldError) err)
			.collect(Collectors.toMap(fe -> fe.getField(), fe -> ""+fe.getDefaultMessage())));
	}

	private String handleButtons(ButtonList actions, EntityJson work) {
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

	protected WithField formField = (beanClass, f, editMode, variable, ej, ctx) -> {
		for(FieldConverter fc : converters) {
			if (fc.canConvert(f)) {
				return fc.apply(beanClass, f, editMode, variable, ej, ctx);
			}
		} 
		
		throw new UnsupportedOperationException("Can't convert "+f);
		
	};

	protected WithField formDisplay = (beanClass, f, editMode, variable, ej, action) -> {
		return "<tr><td><b>" + f.getName() + ":</b></td><td>" + formField.apply(beanClass, f, editMode, variable, ej, action) + "</td></tr>";
	};


	private String withFields(Class<?> c, WithField action, boolean editMode, Variable variable, EntityJson ej) {
		StringBuilder out = new StringBuilder();
		if ((c != Object.class) && (c!=null)) {
			out.append(withFields(c.getSuperclass(), action, editMode, variable, ej));

			for (Field f : c.getDeclaredFields()) {
				if (!Modifier.isStatic(f.getModifiers())) {
					String text = action.apply(c, f, editMode, variable.field(f.getName()), ej, this);
					out.append(text);
				}
			}
		}

		return out.toString();
	}

	@Override
	public String apply(Class<?> beanClass, Field f, boolean editMode, Variable variable, EntityJson ej, WithField context) {
		if (f == null) {
			return withFields(beanClass, context, editMode, variable, ej);
		} else {
			return formField.apply(beanClass, f, editMode, variable, ej, context);
		}
	}


}
