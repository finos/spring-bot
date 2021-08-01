package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
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
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.ResourceLoaderUtil;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.annotations.ComplexUI;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

/**
 * Takes a bean and converts it into a form with either an editable or display
 * version of MessageML.
 * 
 * @author Rob Moffat
 *
 */
public class FreemarkerFormMessageMLConverter implements FormMessageMLConverter, WithType, ApplicationContextAware {

	public static final String JUST_BUTTONS_FORM = "just-buttons-form";

	private List<TypeConverter> converters;
	private ResourceLoaderUtil resourceLoaderUtil;
	private ApplicationContext applicationContext;

	public FreemarkerFormMessageMLConverter(ResourceLoaderUtil resourceLoaderUtil,
			List<TypeConverter> fieldConverters) {
		this.resourceLoaderUtil = resourceLoaderUtil;
		this.converters = new ArrayList<TypeConverter>(fieldConverters);
		Collections.sort(this.converters, (a, b) -> Integer.compare(a.getPriority(), b.getPriority()));
	}

	@Override
	public String convert(Class<?> c, Object o, ButtonList actions, boolean editMode, Errors e, EntityJson work) {
		Variable v;

		// ensure o is in the work object
		if (editMode) {
			work.put("formdata", o);
			v = new Variable("entity.formdata");

			// put complex UI type collection values into entityjson, if any
			readComplexUIComponent(applicationContext, c, work);
		} else {
			if (o != null) {
				work.put(EntityJsonConverter.WORKFLOW_001, o);
			}
			v = new Variable("entity." + EntityJsonConverter.WORKFLOW_001);
		}

		work.put("errors", convertErrorsToMap(e));
		work.put("buttons", actions);

		Template t = c.getAnnotation(Template.class);
		String templateName = t == null ? null : (editMode ? t.edit() : t.view());

		if (StringUtils.hasText(templateName)) {
			return resourceLoaderUtil.readTemplateToString(templateName);
		}

		StringBuilder sb = new StringBuilder();
		sb.append("\n<#-- starting template -->");
		Mode m = editMode ? Mode.FORM : ((actions.size() > 0) ? Mode.DISPLAY_WITH_BUTTONS : Mode.DISPLAY);

		if (m == Mode.FORM) {
			sb.append("\n<form " + AbstractTypeConverter.attribute(v, "id", c.getCanonicalName()) + ">");
		}

		sb.append(apply(this, c, editMode, v, topLevelFieldOutput(), null));

		if (m == Mode.DISPLAY_WITH_BUTTONS) {
			sb.append("\n<form " + AbstractTypeConverter.attribute(v, "id", JUST_BUTTONS_FORM) + ">");
			sb.append(handleButtons(actions, work));
			sb.append("\n</form>");
		} else if (m == Mode.FORM) {
			sb.append(handleButtons(actions, work));
			sb.append("\n</form>");
		}

		sb.append("\n<#-- ending template -->\n");
		return sb.toString();
	}

	private ErrorMap convertErrorsToMap(Errors e) {
		return e == null ? new ErrorMap()
				: new ErrorMap(e.getAllErrors().stream().map(err -> (FieldError) err)
						.collect(Collectors.toMap(fe -> fe.getField(), fe -> "" + fe.getDefaultMessage())));
	}

	private String handleButtons(ButtonList actions, EntityJson work) {
		StringBuilder sb = new StringBuilder();
		sb.append("\n  <p><#list entity.buttons.contents as button>");
		sb.append("\n    <button ");
		sb.append("\n         name=\"${button.name}\"");
		sb.append("\n         type=\"${button.buttonType?lower_case}\">");
		sb.append("\n      ${button.text}");
		sb.append("\n    </button>");
		sb.append("\n  </#list></p>");
		return sb.toString();
	}

	@Override
	public TypeConverter getConverter(Type t, WithType ownerController) {
		for (TypeConverter fc : converters) {
			if (fc.canConvert(t)) {
				return fc;
			}
		}

		throw new UnsupportedOperationException("No converter found for " + t);
	}

	@Override
	public String apply(WithType controller, Type t, boolean editMode, Variable variable, WithField context,
			Annotation a) {
		TypeConverter tc = getConverter(t, controller);
		return tc.apply(controller, t, editMode, variable, context, a);
	}

	/**
	 * This is the with-field apply. It doesn't add any wrapper onto the output.
	 */
	public WithField topLevelFieldOutput() {

		return new WithField() {

			public String apply(Field f, boolean editMode, Variable variable, WithType contentHandler) {
				Type t = isComplextUIField(f) ? f.getAnnotation(ComplexUI.class).annotationType() : f.getGenericType();

				return contentHandler.apply(FreemarkerFormMessageMLConverter.this, t, editMode, variable,
						topLevelFieldOutput(), f.getAnnotation(ComplexUI.class));
			}

			@Override
			public boolean expand() {
				return true;
			}
		};
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}