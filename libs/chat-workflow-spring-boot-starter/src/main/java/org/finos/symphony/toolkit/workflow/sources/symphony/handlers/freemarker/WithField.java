package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.annotations.Display;

import java.lang.reflect.Field;

import static java.util.Optional.ofNullable;

/**
 * General interface for performing some function against a field, with a given variable.
 */
public interface WithField {

	public String apply(Field f, boolean editMode, Variable variable, EntityJson ej, WithType contentHandler);
	
	/**
	 * Return true if we are going to expand the contents of this field.
	 */
	public boolean expand();

	default String getFieldOrientation(Field f, boolean editMode, Variable variable, EntityJson ej, WithType controller, WithField inner) {
		String fieldNameOrientation = getFieldNameOrientation(f);
		if (null != fieldNameOrientation && !fieldNameOrientation.trim().isEmpty()) {
			StringBuilder sb = new StringBuilder();

			sb.append("<tr><td");

			String labelStyle = getLabelStyle(f);
			if (null != labelStyle && !labelStyle.trim().isEmpty()) {
				sb.append(" style=\"");
				sb.append(labelStyle);
				sb.append("\"");
			}
			sb.append("><b>");
			sb.append(fieldNameOrientation);
			sb.append(":</b></td>");

			sb.append("<td");
			String dataStyle = getDataStyle(f);
			if (null != dataStyle && !dataStyle.trim().isEmpty()) {
				sb.append(" style=\"");
				sb.append(dataStyle);
				sb.append("\"");
			}
			sb.append(">");
			sb.append(inner.apply(f, editMode, variable, ej, controller));

			sb.append("</td></tr>");
			return sb.toString();

		} else {
			return "";
		}

	}

	default String getFieldNameOrientation(Field f) {
		return ofNullable(f.getAnnotation(Display.class)).map(display -> {
			if (display.visible()) {
				return ofNullable(display.name()).orElse(f.getName());
			} else {
				return "";
			}
		}).orElse(f.getName());
	}

	default String getLabelStyle(Field f) {
		return ofNullable(f.getAnnotation(Display.class)).map(display -> {
			if (null != display.labelStyle() && !display.labelStyle().trim().isEmpty()) {
				return display.labelStyle();
			} else {
				return "";
			}
		}).orElse("");
	}

	default String getDataStyle(Field f) {
		return ofNullable(f.getAnnotation(Display.class)).map(display -> {
			if (null != display.dataStyle() && !display.dataStyle().trim().isEmpty()) {
				return display.dataStyle();
			} else {
				return "";
			}
		}).orElse("");
	}

}
