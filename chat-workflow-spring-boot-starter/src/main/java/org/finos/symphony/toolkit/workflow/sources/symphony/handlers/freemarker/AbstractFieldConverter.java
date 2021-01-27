package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

public abstract class AbstractFieldConverter implements FieldConverter {
	
	public static final int MED_PRIORITY = 40;
	public static final int LOW_PRIORITY = 50;
	public static final int BOTTOM_PRIORITY = 2000;
	
	public static final String CENTER_ALIGN = "style=\"text-align:center;\" ";
	public static final String RIGHT_ALIGN = "style=\"text-align: right;\"";
	

	private final int priority;

	public AbstractFieldConverter(int priority) {
		super();
		this.priority = priority;
	}

	@Override
	public int getPriority() {
		return priority;
	}
	
	public static String indent(int n) {
		return "\n"+String.format("%"+n+"s", "");
	}
	
	public static String formatErrorsAndIndent(Variable variable) {
		return indent(variable.depth) 
				+ "<span class=\"tempo-text-color--red\">${entity.errors.contents['"+variable.getFormFieldName()+"']!''}</span>"
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
	
	public static String textField(Variable variable) {
		return formatErrorsAndIndent(variable)
				+ "<text-field "
				+ attribute(variable, "name", variable.getFormFieldName())
				+ attribute(variable, "placeholder", variable.getDisplayName()) +
				">" + text(variable, "!''") + "</text-field>";
	}

	public static String beginIterator(Variable variable, Variable reg) {
		return indent(variable.depth) + "<#list "+variable.getDataPath()+" as "+reg.getDataPath()+">";
	}
	
	public static String endIterator(Variable variable) {
		return indent(variable.depth) + "</#list>";
	}

	
}
