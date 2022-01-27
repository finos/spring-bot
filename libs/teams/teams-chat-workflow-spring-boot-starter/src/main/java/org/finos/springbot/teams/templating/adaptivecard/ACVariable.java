package org.finos.springbot.teams.templating.adaptivecard;

import org.finos.springbot.workflow.templating.Variable;

/**
 * Adaptive-cards compatible variable naming.
 * 
 * @author rob@kite9.com
 *
 */
public class ACVariable implements Variable {
	
	public static final String FORM_IDENTIFIER = "form-field:";
	
	public static final String FORM_INCREMENT = "#increment";
	
	
	public final String formPath;
	public final String dataPath;
	private final int depth;
	
	public ACVariable(String formPath, String dataPath) {
		this(1, formPath, dataPath);
	}
	
	private ACVariable(int depth, String formPath, String dataPath) {
		this.formPath = formPath;
		this.dataPath = dataPath;
		this.depth = depth;
	}
	
	private String append(String a, String b) {
		return a.length() > 0 ? a + "." + b : b;
	}

	public ACVariable field(String seg) {
		return new ACVariable(depth+1, append(formPath,seg), append(dataPath,seg));
	}

	private String getDataIndexString() {
		return "[[index:"+dataPath+"]]";
	}
	
	
	public ACVariable index() {
		return new ACVariable(depth + 1, formPath + getDataIndexString(), "$data");
	}
	
	public String getDisplayName() {
		int hasDot = dataPath.lastIndexOf(".");
		String lastPart = hasDot > -1 ? dataPath.substring(hasDot+1) : dataPath;
		return lastPart.replaceAll("(.)(\\p{Upper})", "$1 $2").toLowerCase();
	}

	public String getFormFieldName() {
		String out = FORM_IDENTIFIER + formPath;
		return out;
	}
	
	public String getFormIncrement() {
		return FORM_INCREMENT+formPath;
	}
	
	
	public String getDataPath() {
		return dataPath;
	}
	
	public String getErrorPath() {
		return getDataPath() + ".error";
	}

	@Override
	public int getDepth() {
		return depth;
	}

}