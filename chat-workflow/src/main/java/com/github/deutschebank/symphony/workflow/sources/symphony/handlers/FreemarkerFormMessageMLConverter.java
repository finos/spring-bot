package com.github.deutschebank.symphony.workflow.sources.symphony.handlers;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.util.HtmlUtils;

import com.github.deutschebank.symphony.workflow.content.Author;
import com.github.deutschebank.symphony.workflow.content.ID;
import com.github.deutschebank.symphony.workflow.content.Room;
import com.github.deutschebank.symphony.workflow.content.Tag;
import com.github.deutschebank.symphony.workflow.content.User;
import com.github.deutschebank.symphony.workflow.form.Button;
import com.github.deutschebank.symphony.workflow.sources.symphony.TagSupport;
import com.github.deutschebank.symphony.workflow.sources.symphony.Template;
import com.github.deutschebank.symphony.workflow.sources.symphony.elements.edit.TableAddRow;
import com.github.deutschebank.symphony.workflow.sources.symphony.elements.edit.TableDeleteRows;
import com.github.deutschebank.symphony.workflow.sources.symphony.elements.edit.TableEditRow;
import com.github.deutschebank.symphony.workflow.sources.symphony.room.SymphonyRooms;
import com.github.deutschebank.symphony.workflow.validation.ErrorHelp;

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

	@Override
	public String convert(Class<?> c, Object o, List<Button> actions, boolean editMode, Errors e) {
		
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
		Mode m = editMode ? Mode.FORM : ((actions.size() > 0) ? Mode.DISPLAY_WITH_BUTTONS : Mode.DISPLAY);
		if (o instanceof String) {
			sb.append(o.toString());
		} else {
			// convert to an object form
			if (m == Mode.FORM) {
				sb.append("<form " + attribute("id", c.getCanonicalName()) + ">");
			} else {
				sb.append("<table>");
			}
			if (m == Mode.FORM) {
				sb.append(withFields(c, o, formField, true, e));
			} else {
				sb.append(withFields(c, o, formDisplay, false, e));
			}
			if (m == Mode.DISPLAY_WITH_BUTTONS) {
				sb.append("</table><form " + attribute("id", c.getCanonicalName()) + ">");
				sb.append(handleButtons(actions));
				sb.append("</form>");
			} else if (m == Mode.FORM) {
				sb.append(handleButtons(actions));
				sb.append("</form>");
			} else {
				sb.append("</table>");
			}
		}

		return sb.toString();
	}

	private String handleButtons(List<Button> actions) {
		StringBuilder sb = new StringBuilder();
		sb.append("<p>");
		for (Button button : actions) {
			sb.append("<button ");
			sb.append(attribute("name", button.getName()));
			sb.append(attribute("type", button.getType().toString().toLowerCase()));
			sb.append(">");
			sb.append(HtmlUtils.htmlEscape(button.getText()));
			sb.append("</button>");
		}
		sb.append("</p>");
		return sb.toString();
	}

	static interface WithField {

		public String apply(Class<?> beanClass, Object bean, Field f, boolean editMode, Errors e);

	}

	public WithField formField = (beanClass, bean, f, editMode, e) -> {
		Class<?> c = f.getType();
		if (String.class.isAssignableFrom(c)) {
			return convertTextField((String) bean, e, editMode, f.getName());
		} else if (numberClass(c)) {
			return convertNumberField((Number) bean, e, editMode, f.getName());
		} else if (Collection.class.isAssignableFrom(c)) {
			Class<?> elementClass = (Class<?>) ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0];
			return convertTable(elementClass, (Collection<?>) bean, e, editMode);
		} else if (boolClass(c)) {
			return convertCheckboxField((Boolean) bean, e, editMode, f.getName());
		} else if (Author.class.isAssignableFrom(c)) {
			return convertAuthor((Author) bean, e, editMode, f.getName());			
		} else if (User.class.isAssignableFrom(c)) {
			return convertUser((User) bean, e, editMode, f.getName());
		} else if (ID.class.isAssignableFrom(c)) {
			return convertID((ID) bean, e, editMode, f.getName());
		} else if (Tag.class.isAssignableFrom(c)) {
			return convertTag((Tag) bean, e, editMode, f.getName());
		} else if (Instant.class.isAssignableFrom(c)) {
			return convertTextField(bean.toString(), e, editMode, f.getName());
		} else if (c.isEnum()) {
			return convertEnum((Enum<?>) bean, e, c, editMode, f.getName());
		} else if (Room.class.isAssignableFrom(c)) {
			return convertRoom((Room) bean, e, c, editMode, f.getName());
		} else {
			return convertInner(c, bean, editMode, e);
		}
	};

	private boolean numberClass(Class<?> c) {
		return Number.class.isAssignableFrom(c);
	}

	private String convertRoom(Room r, Errors e, Class<?> c, boolean editMode, String name) {
		if (editMode) {
			return renderDropdown(r, e, ru.getAllRooms(), name, (o) -> o.getId(), (o) -> o.getRoomName());
		} else if (r != null){
			return r.getRoomName();
		} else {
			return "";
		}
	}

	/**
	 * This converts tags that aren't users.  So, hashtags and cashtags only.
	 * For these tags, the id and the name are the same thing, so we only need to 
	 * worry about one of those
	 */
	private String convertTag(Tag bean, Errors e, boolean editMode, String name) {
		if (editMode) {
			return convertTextField(bean.getText(), e, editMode, "$cashtag or $hashtag");
		} else {
			return TagSupport.format(bean);
		}
	}
	
	private String convertID(ID id, Errors e, boolean editMode, String name) {
		return TagSupport.format(id);
	}

	private String convertEnum(Enum<?> n, Errors e, Class<?> c, boolean editMode, String name) {
		if (editMode) {
			return renderDropdown(n, e, Arrays.asList(c.getEnumConstants()), name, (g) -> ((Enum<?>)g).name(), (g) -> g.toString());
		} else if (n != null) {
			return n.toString();
		} else {
			return "";
		}
	}

	private <V> String renderDropdown(V selected, Errors e, Collection<V> options, String name, Function<V, String> keyFunction, Function<V, String> displayFunction) {
		StringBuilder out = new StringBuilder();
		out.append("<select "+ attribute("name", e.getNestedPath()));
		out.append(attribute("required", "false"));
		out.append(attribute("data-placeholder", "Choose "+name));
		out.append(">");
		
		Object selectedKey = selected == null ? null : keyFunction.apply(selected);
		
		for (V o : options) {
			out.append("<option ");
			out.append(attribute("value", keyFunction.apply(o)));
			if (keyFunction.apply(o).equals(selectedKey)) {
				out.append(attribute("selected", "true"));
			}
			out.append(">");
			out.append(displayFunction.apply(o));
			out.append("</option>");
		}
		
		out.append("</select>");
		return out.toString();
	}

	private String convertInner(Class<?> c, Object o, boolean editMode, Errors e) {
		StringBuilder sb = new StringBuilder();
		if (o instanceof String) {
			sb.append(o.toString());
		} else {
			// convert to an object form
			sb.append("<table>");
			if (editMode) {
				sb.append(withFields(c, o, formField, true, e));
			} else {
				sb.append(withFields(c, o, formDisplay, false, e));
			}
			sb.append("</table>");
		}

		return sb.toString();
	}

	private boolean boolClass(Class<?> c) {
		return (Boolean.class.isAssignableFrom(c)) || (boolean.class.isAssignableFrom(c));
	}

	private WithField formDisplay = (beanClass, bean, f, editMode, e) -> {
		return "<tr><td><b>" + f.getName() + ":</b></td><td>" + formField.apply(beanClass, bean, f, editMode, e) + "</td></tr>";
	};

	private WithField tableDisplay = (beanClass, bean, f, editMode, e) -> {
		String align = numberClass(f.getType()) ? RIGHT_ALIGN : (boolClass(f.getType()) ? CENTER_ALIGN : "");
		return "<td " + align + ">" + formField.apply(beanClass, bean, f, editMode, e) + "</td>";
	};

	private WithField tableColumnNames = (beanClass, bean, f, editMode, e) -> {
		String align = numberClass(f.getType()) ? RIGHT_ALIGN : (boolClass(f.getType()) ? CENTER_ALIGN : "");
		return "<td " + align + "><b>" + f.getName() + "</b></td>";
	};

	private String withFields(Class<?> c, Object bean, WithField action, boolean editMode, Errors e) {
		StringBuilder out = new StringBuilder();
		if ((c != Object.class) && (c!=null)) {
			out.append(withFields(c.getSuperclass(), bean, action, editMode, e));

			for (Field f : c.getDeclaredFields()) {
				if (!Modifier.isStatic(f.getModifiers())) {
					f.setAccessible(true);
					e.pushNestedPath(f.getName());
					Object value;
					try {
						value = bean != null ? f.get(bean) : null;
					} catch (Exception e1) {
						throw new UnsupportedOperationException(
								"Couldn't get value of field " + f.getName() + " on object " + bean);
					}
					String text = action.apply(c, value, f, editMode, e);
					out.append(text);
					e.popNestedPath();
				}
			}
		}

		return out.toString();
	}

	private String convertTable(Class<?> elementClass, Collection<?> collection, Errors e, boolean editMode) {
		StringBuilder sb = new StringBuilder();
		sb.append(ErrorHelp.errors(e));
		sb.append("<table><thead><tr>");
		sb.append(withFields(elementClass, null, tableColumnNames, editMode, e));
		if (editMode) {
			sb.append("<td " + CENTER_ALIGN + "><button name=\"" + e.getNestedPath() + TableDeleteRows.ACTION_SUFFIX
					+ "\">Delete</button></td>");
			sb.append("<td " + CENTER_ALIGN + "><button name=\"" + e.getNestedPath() + TableAddRow.ACTION_SUFFIX
					+ "\">New</button></td>");
		}
		sb.append("</tr></thead><tbody>");
		int i = 0;

		if (collection != null) {
			for (Object o : collection) {
				sb.append("<tr>");
				e.pushNestedPath("[" + i + "]");
				sb.append(withFields(elementClass, o, tableDisplay, false, e));
				if (editMode) {
					sb.append("<td " + CENTER_ALIGN + "><checkbox name=\"" + e.getNestedPath()
							+ TableDeleteRows.SELECT_SUFFIX + "\" /></td>");
					sb.append("<td " + CENTER_ALIGN + "><button name=\"" + e.getNestedPath() + TableEditRow.EDIT_SUFFIX
							+ "\">Edit</button></td>");
				}
				e.popNestedPath();
				sb.append("</tr>");
				i++;
			}
		}

		sb.append("</tbody></table>");
		return sb.toString();
	}

	private String convertNumberField(Number n, Errors e, boolean editMode, String placeholder) {
		if (editMode) {
			return ErrorHelp.errors(e) + "<text-field " + attribute("name", e.getNestedPath())
					+ attribute("placeholder", placeholder) +
					/*
					 * attribute("required", required)+ attribute("masked", masked)+
					 * attribute("maxlength", maxLength)+ attribute("minlength", minLength)+
					 */
					">" + text(n == null ? "" : n.toString()) + "</text-field>";
		} else if (n != null) {
			return n.toString();
		} else {
			return "";
		}
	}

	private String convertUser(User bean, Errors e, boolean editMode, String name) {
		if (editMode) {
			return ErrorHelp.errors(e) + "<person-selector " 
					+ attribute("name", e.getNestedPath()) +
				 "placeholder=\""+name+"\" required=\"false\"/>";
		} else if (bean == null) {
			return "-- no user --";
		} else {
			return TagSupport.format(bean);
		}
	}
	
	private String convertAuthor(Author bean, Errors e, boolean editMode, String name) {
		if (editMode) {
			return "";
		} else if (bean == null) {
			return "-- no user --";
		} else {
			return TagSupport.format(bean);
		}
	}

	private String convertTextField(String value, Errors e, boolean editMode, String placeholder) {
		if (editMode) {
			return ErrorHelp.errors(e) + "<text-field " + attribute("name", e.getNestedPath())
					+ attribute("placeholder", placeholder) +
					">" + text(value) + "</text-field>";
		} else {
			return value;
		}
	}

	public String convertCheckboxField(Boolean value, Errors e, boolean editMode, String name) {
		if (editMode) {
			return ErrorHelp.errors(e) + "<checkbox " + attribute("name", e.getNestedPath())
					+ attribute("checked", value) + attribute("value", "true") + ">" + text(name) + "</checkbox>";
		} else {
			return value ? "Y" : "N";
		}
	}

	public static String attribute(String name, String value) {
		if (!StringUtils.isEmpty(value)) {
			return name + "=\"" + HtmlUtils.htmlEscape(value) + "\" ";
		} else {
			return "";
		}
	}

	public static String attribute(String name, Boolean value) {
		if (value != null) {
			return name + "=\"" + value + "\" ";
		} else {
			return "";
		}
	}

	public static String attribute(String name, Integer value) {
		if (value != null) {
			return name + "=\"" + value + "\" ";
		} else {
			return "";
		}
	}

	public static String text(String value) {
		if (value != null) {
			return HtmlUtils.htmlEscape(value);
		} else {
			return "";
		}
	}

	public static <T> BinaryOperator<T> throwingMerger() {
		return (u, v) -> {
			throw new IllegalStateException(String.format("Duplicate key %s", u));
		};
	}

}
