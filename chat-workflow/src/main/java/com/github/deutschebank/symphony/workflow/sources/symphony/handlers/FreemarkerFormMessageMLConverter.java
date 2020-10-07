package com.github.deutschebank.symphony.workflow.sources.symphony.handlers;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;

import com.github.deutschebank.symphony.json.EntityJson;
import com.github.deutschebank.symphony.workflow.content.Author;
import com.github.deutschebank.symphony.workflow.content.CashTag;
import com.github.deutschebank.symphony.workflow.content.HashTag;
import com.github.deutschebank.symphony.workflow.content.ID;
import com.github.deutschebank.symphony.workflow.content.Room;
import com.github.deutschebank.symphony.workflow.content.User;
import com.github.deutschebank.symphony.workflow.form.ButtonList;
import com.github.deutschebank.symphony.workflow.form.ErrorMap;
import com.github.deutschebank.symphony.workflow.form.RoomList;
import com.github.deutschebank.symphony.workflow.sources.symphony.Template;
import com.github.deutschebank.symphony.workflow.sources.symphony.elements.edit.TableAddRow;
import com.github.deutschebank.symphony.workflow.sources.symphony.elements.edit.TableDeleteRows;
import com.github.deutschebank.symphony.workflow.sources.symphony.elements.edit.TableEditRow;
import com.github.deutschebank.symphony.workflow.sources.symphony.room.SymphonyRooms;

/**
 * Takes a bean and converts it into a form with either an editable or display
 * version of MessageML.
 * 
 * @author Rob Moffat
 *
 */
public class FreemarkerFormMessageMLConverter implements FormMessageMLConverter {

	private static final String CENTER_ALIGN = "style=\"text-align:center;\" ";
	public static final String RIGHT_ALIGN = "style=\"text-align: right;\"";

	enum Mode {
		DISPLAY, DISPLAY_WITH_BUTTONS, FORM
	};
	
	SymphonyRooms ru;
	ResourceLoader rl;
	
	public FreemarkerFormMessageMLConverter(SymphonyRooms ru, ResourceLoader rl) {
		this.ru = ru;
		this.rl = rl;
	}
	
	public class Variable {
		
		String segment;
		Variable parent = null;
		int depth = 0;
		
		public Variable(String name) {
			this(0, name);
		}
		
		private Variable(int depth, String var) {
			this.segment = var;
			this.depth = depth;
		}
		
		private Variable(Variable parent2, String seg) {
			this.segment = seg;
			this.parent = parent2;
			this.depth = parent2.depth + 1;
		}

		public Variable field(String seg) {
			return new Variable(this, seg);
		}
		
		public Variable index() {
			return new Variable(parent.depth + 1, "i"+Character.toString((char) (65 + parent.depth)));
		}
		
		public String getDisplayName() {
			return segment.replaceAll("(.)(\\p{Upper})", "$1 $2").toLowerCase();
		}

		public String getFormFieldName() {
			return segment;
		}
		
		public String getDataPath() {
			return (parent != null ? parent.getDataPath() + "." : "") + segment;
		}
		
		public String getErrorPath() {
			return getDataPath() + ".error";
		}

	}

	@Override
	public String convert(Class<?> c, Object o, ButtonList actions, boolean editMode, Errors e, EntityJson work) {
		Variable v;
		
		// ensure o is in the work object
		if (editMode) {
			work.putIfAbsent("formdata", o);
			v = new Variable("entity.formdata");
		} else {
			work.putIfAbsent(EntityJsonConverter.WORKFLOW_001, o);
			v = new Variable("entity."+EntityJsonConverter.WORKFLOW_001);
		}
		
		work.putIfAbsent("errors", convertErrorsToMap(e));
		work.putIfAbsent("buttons", actions);
		
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
				sb.append("\n<form " + attribute(v, "id", c.getCanonicalName()) + ">");
			} else {
				sb.append("\n<table>");
			}
			if (m == Mode.FORM) {
				sb.append(withFields(c, formField, true,v, work));
			} else {
				sb.append(withFields(c, formDisplay, false,v, work));
			}
			if (m == Mode.DISPLAY_WITH_BUTTONS) {
				sb.append("\n</table>\n<form " + attribute(v, "id", c.getCanonicalName()) + ">");
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
			.collect(Collectors.toMap(fe -> fe.getField(), fe -> fe.getDefaultMessage())));
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

	static interface WithField {

		public String apply(Class<?> beanClass, Field f, boolean editMode, Variable variable, EntityJson ej);

	}

	public WithField formField = (beanClass, f, editMode, variable, ej) -> {
		Class<?> c = f.getType();
		if (String.class.isAssignableFrom(c)) {
			return convertTextField(variable, editMode);
		} else if (numberClass(c)) {
			return convertNumberField(variable, editMode);
		} else if (Collection.class.isAssignableFrom(c)) {
			Class<?> elementClass = (Class<?>) ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0];
			return convertTable(elementClass, variable, editMode, ej);
		} else if (boolClass(c)) {
			return convertCheckboxField(variable, editMode);
		} else if (Author.class.isAssignableFrom(c)) {
			return convertAuthor(variable, editMode);			
		} else if (User.class.isAssignableFrom(c)) {
			return convertUser(variable, editMode);
		} else if (ID.class.isAssignableFrom(c)) {
			return convertID(variable, editMode);
		} else if (CashTag.class.isAssignableFrom(c)) {
			return convertCashTag(variable, editMode);
		} else if (HashTag.class.isAssignableFrom(c)) {
			return convertHashTag(variable, editMode);
		} else if (Instant.class.isAssignableFrom(c)) {
			return convertTextField(variable, editMode);
		} else if (c.isEnum()) {
			return convertEnum(variable, c, editMode);
		} else if (Room.class.isAssignableFrom(c)) {
			return convertRoom(variable, c, editMode, ej);
		} else {
			return convertInner(c, editMode, variable, ej);
		}
	};

	private boolean numberClass(Class<?> c) {
		return Number.class.isAssignableFrom(c);
	}

	private String convertRoom(Variable v, Class<?> c, boolean editMode, EntityJson ej) {
		ej.putIfAbsent("room", new RoomList(ru.getAllRooms()));
		
		if (editMode) {
			StringBuilder out = new StringBuilder();
			out.append(indent(v.depth) + "<select "+ attribute(v, "name", v.getFormFieldName()));
			out.append(attribute(v, "required", "false"));
			out.append(attribute(v, "data-placeholder", "Choose "+v.getDisplayName()));
			out.append(">");
			out.append(indent(v.depth) + "<#list entity.rooms as r>");
			out.append(indent(v.depth) + "<option ");
			out.append(attribute(v, "value", "hi"));
			out.append(attributeParam(v, "selected", "bingo"));
			out.append(indent(v.depth) + ">");
			out.append("</option>");
			out.append("</select>");
			return out.toString();
		} else {
			return convertTextField(v, editMode);
		}
	}


	protected String convertHashTag(Variable variable, boolean editMode) {
		if (editMode) {
			return convertTextField(variable, editMode);
		} else {
			return indent(variable.depth)+"<#if " + variable.getDataPath() +"??><hash "
				+ attributeParam(variable, "tag", variable.getDataPath()+".name!''")
				+ " /></#if>";
		}
	}
	
	protected String convertCashTag(Variable variable, boolean editMode) {
		if (editMode) {
			return convertTextField(variable, editMode);
		} else {
			return indent(variable.depth)+"<#if " + variable.getDataPath() +"??><cash "
				+ attributeParam(variable, "tag", variable.getDataPath()+".name!''")
				+ " /></#if>";
		}
	}
	
	protected String convertID(Variable variable, boolean editMode) {
		if (editMode) {
			return "";
		} else {
			return indent(variable.depth)+"<#if " + variable.getDataPath() +"??><hash "
				+ attributeParam(variable, "tag", variable.getDataPath()+".name!''")
				+ " /></#if>";
		}
	}

	private String convertEnum(Variable variable, Class<?> c, boolean editMode) {
		if (editMode) {
			return renderDropdown(variable, 
					Arrays.asList(c.getEnumConstants()), variable.getFormFieldName(), 
					(g) -> ((Enum<?>)g).name(), 
					(g) -> g.toString(),
					(v, g) -> "((" + v.getDataPath()+"!'') == '"+g.toString()+"')?then('true', 'false')");
		} else {
			return convertTextField(variable, false);
		}
	}

	private <V> String renderDropdown(Variable v, Collection<V> options, String name, Function<V, String> keyFunction, Function<V, String> displayFunction, BiFunction<Variable, V,String> selectedFunction) {
		StringBuilder out = new StringBuilder();
		out.append("<select "+ attribute(v, "name", name));
		out.append(attribute(v, "required", "false"));
		out.append(attribute(v, "data-placeholder", "Choose "+v.getDisplayName()));
		out.append(">");
				
		for (V o : options) {
			out.append(indent(v.depth)+ "<option ");
			out.append(attribute(v, "value", keyFunction.apply(o)));
			out.append(attributeParam(v, "selected", selectedFunction.apply(v, o)));
			out.append(">");
			out.append(displayFunction.apply(o));
			out.append("</option>");
		}
		
		out.append("</select>");
		return out.toString();
	}

	private String convertInner(Class<?> c, boolean editMode, Variable variable, EntityJson ej) {
		StringBuilder sb = new StringBuilder();
		if (String.class.isAssignableFrom(c)) {
			sb.append("${"+variable+"}");
		} else {
			// convert to an object form
			sb.append("<table>");
			if (editMode) {
				sb.append(withFields(c, formField, true, variable, ej));
			} else {
				sb.append(withFields(c, formDisplay, false, variable, ej));
			}
			sb.append("</table>");
		}

		return sb.toString();
	}

	private boolean boolClass(Class<?> c) {
		return (Boolean.class.isAssignableFrom(c)) || (boolean.class.isAssignableFrom(c));
	}

	private WithField formDisplay = (beanClass, f, editMode, variable, ej) -> {
		return "<tr><td><b>" + f.getName() + ":</b></td><td>" + formField.apply(beanClass, f, editMode, variable, ej) + "</td></tr>";
	};

	private WithField tableDisplay = (beanClass, f, editMode, variable, ej) -> {
		String align = numberClass(f.getType()) ? RIGHT_ALIGN : (boolClass(f.getType()) ? CENTER_ALIGN : "");
		return  indent(variable.depth) + "<td " + align + ">" + formField.apply(beanClass, f, editMode, variable, ej) + "</td>";
	};

	private WithField tableColumnNames = (beanClass, f, editMode, variable, ej) -> {
		String align = numberClass(f.getType()) ? RIGHT_ALIGN : (boolClass(f.getType()) ? CENTER_ALIGN : "");
		return indent(variable.depth+1) + "<td " + align + "><b>" + f.getName() + "</b></td>";
	};

	private String withFields(Class<?> c, WithField action, boolean editMode, Variable variable, EntityJson ej) {
		StringBuilder out = new StringBuilder();
		if ((c != Object.class) && (c!=null)) {
			out.append(withFields(c.getSuperclass(), action, editMode, variable, ej));

			for (Field f : c.getDeclaredFields()) {
				if (!Modifier.isStatic(f.getModifiers())) {
					String text = action.apply(c, f, editMode, variable.field(f.getName()), ej);
					out.append(text);
				}
			}
		}

		return out.toString();
	}
	
	private String beginIterator(Variable variable, Variable reg) {
		return indent(variable.depth) + "<#list "+variable.getDataPath()+" as "+reg.getDataPath()+">";
	}
	
	private String endIterator(Variable variable) {
		return indent(variable.depth) + "</#list>";
	}

	private String convertTable(Class<?> elementClass, Variable variable, boolean editMode, EntityJson ej) {
		StringBuilder sb = new StringBuilder();
		sb.append(formatErrorsAndIndent(variable));
		sb.append(indent(variable.depth) + "<table><thead><tr>");
		sb.append(withFields(elementClass, tableColumnNames, editMode, variable, ej));
		if (editMode) {
			sb.append("<td " + CENTER_ALIGN + "><button name=\"" + variable.getFormFieldName() + "." + TableDeleteRows.ACTION_SUFFIX
					+ "\">Delete</button></td>");
			sb.append("<td " + CENTER_ALIGN + "><button name=\"" + variable.getFormFieldName() + "." + TableAddRow.ACTION_SUFFIX
					+ "\">New</button></td>");
		}
		sb.append(indent(variable.depth) + "</tr></thead><tbody>");
		
		Variable subVar = variable.index();

		sb.append(beginIterator(variable, subVar));
		sb.append(indent(subVar.depth) + "<tr>");
		sb.append(withFields(elementClass, tableDisplay, false, subVar, ej));
		if (editMode) {
			sb.append("<td " + CENTER_ALIGN + "><checkbox name=\""+ variable.getFormFieldName() + ".${" + subVar.getDataPath() + "?index}." + TableDeleteRows.SELECT_SUFFIX + "\" /></td>");
			sb.append("<td " + CENTER_ALIGN + "><button name=\"" + variable.getFormFieldName() + "[${" + subVar.getDataPath() + "?index}]." + TableEditRow.EDIT_SUFFIX + "\">Edit</button></td>");
		}
		sb.append("</tr>");
		sb.append(endIterator(variable));
		sb.append(indent(variable.depth) + "</tbody></table>");
		return sb.toString();
	}

	private String convertNumberField(Variable variable, boolean editMode) {
		if (editMode) {
			return formatErrorsAndIndent(variable) + "<text-field " 
					+ attribute(variable, "name", variable.getFormFieldName())
					+ attribute(variable, "placeholder", variable.getDisplayName())
					/*
					 * attribute("required", required)+ attribute("masked", masked)+
					 * attribute("maxlength", maxLength)+ attribute("minlength", minLength)+
					 */
					+ ">" 
					+ text(variable, "!''") 
					+ "</text-field>";
		} else {
			return text(variable, "!''");
		}
	}

	private String convertUser(Variable variable, boolean editMode) {
		if (editMode) {
			return formatErrorsAndIndent(variable) 
					+ "<person-selector " 
					+ attribute(variable, "name", variable.getFormFieldName())
					+ attribute(variable, "placeholder", variable.getDisplayName())
					+" required=\"false\"/>";
		} else {
			return "<#if " + variable.getDataPath() +"??><mention "
					+ attributeParam(variable, "uid", variable.field("id").getDataPath())
					+ " /></#if>";
		}
	}
	
	private String convertAuthor(Variable variable, boolean editMode) {
		if (editMode) {
			return "";
		} else {
			return convertUser(variable, editMode);
		}
	}

	private String convertTextField(Variable variable, boolean editMode) {
		if (editMode) {
			return formatErrorsAndIndent(variable)
					+ "<text-field "
					+ attribute(variable, "name", variable.getFormFieldName())
					+ attribute(variable, "placeholder", variable.getDisplayName()) +
					">" + text(variable, "!''") + "</text-field>";
		} else {
			return text(variable, "!''");
		}
	}

	public String convertCheckboxField(Variable variable, boolean editMode) {
		if (editMode) {
			return formatErrorsAndIndent(variable) + 
					"<checkbox " 
					+ attribute(variable, "name", variable.getFormFieldName())
					+ attributeParam(variable, "checked", variable.getDataPath()+"?string('true', 'false')") 
					+ attribute(variable, "value", "true") 
					+ ">" 
					+ variable.getDisplayName()
					+ "</checkbox>";
		} else {
			return text(variable, "?string(\"Y\", \"N\")");
		}
	}

	private static String indent(int n) {
		return "\n"+String.format("%"+n+"s", "");
	}
	
	private String formatErrorsAndIndent(Variable variable) {
		return indent(variable.depth) 
				+ "<span class=\"tempo-text-color--red\">${entity.errors['"+variable.getFormFieldName()+"']!''}</span>"
				+ indent(variable.depth);
	}

	public static String attributeParam(Variable v, String name, String value) {
		return indent(v.depth+1) + name + "=\"${" + value + "}\"";
	}
	
	public static String attribute(Variable v, String name, String value) {
		return indent(v.depth+1) + name + "=\"" + value + "\"";
	}

	public static String text(Variable variable, String suffix) {
		return "${"+variable.getDataPath()+suffix+"}";
	}

	public static <T> BinaryOperator<T> throwingMerger() {
		return (u, v) -> {
			throw new IllegalStateException(String.format("Duplicate key %s", u));
		};
	}

}
