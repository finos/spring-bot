package org.finos.springbot.sources.teams.handlers.adaptivecard;

import org.finos.springbot.workflow.templating.Variable;

/**
 * Adaptive-cards compatible variable naming.
 * 
 * @author rob@kite9.com
 *
 */
public class ACVariable implements Variable {
	
	public final String segment;
	private final ACVariable parent;
	public final int depth;
	
	public ACVariable(String name) {
		this(1, name);
	}
	
	private ACVariable(int depth, String var) {
		this.segment = var;
		this.depth = depth;
		this.parent = null;
	}
	
	private ACVariable(ACVariable parent2, String seg) {
		this.segment = seg;
		this.parent = parent2;
		this.depth = parent2.depth + 1;
	}

	public ACVariable field(String seg) {
		return new ACVariable(this, seg);
	}
	
	public ACVariable index() {
		return new ACVariable(parent.depth + 1, "i"+Character.toString((char) (65 + parent.depth)));
	}
	
	public String getDisplayName() {
		return segment.replaceAll("(.)(\\p{Upper})", "$1 $2").toLowerCase();
	}

	public String getFormFieldName() {
		String dp = getInnerDataPath();
		int formStart = dp.indexOf("form.");
		if (formStart > -1) {
			return dp.substring(formStart+5);
		} else {
			return segment;
		}
	}
	
	public String getInnerDataPath() {
		return (parent != null ? parent.getInnerDataPath() + "." : "") + segment;
	}
	
	
	public String getDataPath() {
		return "${" + getInnerDataPath() + "}";
	}
	
	public String getErrorPath() {
		return getDataPath() + ".error";
	}

}