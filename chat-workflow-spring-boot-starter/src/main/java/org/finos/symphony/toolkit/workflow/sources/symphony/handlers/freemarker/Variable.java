package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

/**
 * This class represents a FreeMarker template entity variable, such as "entity.id"
 * 
 * @author rob@kite9.com
 *
 */
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