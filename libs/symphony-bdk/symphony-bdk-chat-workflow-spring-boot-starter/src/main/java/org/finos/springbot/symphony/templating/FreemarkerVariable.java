package org.finos.springbot.symphony.templating;

import org.finos.springbot.workflow.templating.Variable;

/**
 * This class represents a FreeMarker template entity variable, such as "entity.id"
 * 
 * @author rob@kite9.com
 *
 */
public class FreemarkerVariable implements Variable {
	
	public final String segment;
	private final FreemarkerVariable parent;
	public final int depth;
	
	public FreemarkerVariable(String name) {
		this(1, name);
	}
	
	private FreemarkerVariable(int depth, String var) {
		this.segment = var;
		this.depth = depth;
		this.parent = null;
	}
	
	private FreemarkerVariable(FreemarkerVariable parent2, String seg) {
		this.segment = seg;
		this.parent = parent2;
		this.depth = parent2.depth + 1;
	}

	public FreemarkerVariable field(String seg) {
		return new FreemarkerVariable(this, seg);
	}
	
	public FreemarkerVariable index() {
		return new FreemarkerVariable(parent.depth + 1, "i"+Character.toString((char) (65 + parent.depth)));
	}
	
	public String getDisplayName() {
		return segment.replaceAll("(.)(\\p{Upper})", "$1 $2").toLowerCase();
	}

	public String getFormFieldName() {
		String dp = getDataPath();
		int formStart = dp.indexOf("form.");
		if (formStart > -1) {
			return dp.substring(formStart+5);
		} else {
			return segment;
		}
	}
	
	public String getDataPath() {
		return (parent != null ? parent.getDataPath() + "." : "") + segment;
	}
	
	public String getErrorPath() {
		return getDataPath() + ".error";
	}

	@Override
	public int getDepth() {
		return depth;
	}

}