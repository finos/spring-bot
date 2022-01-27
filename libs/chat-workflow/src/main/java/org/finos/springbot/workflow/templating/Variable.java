package org.finos.springbot.workflow.templating;

/**
 * This class represents a FreeMarker template entity variable, such as "entity.id"
 * 
 * @author rob@kite9.com
 *
 */
public interface Variable {

	public Variable field(String seg);
	
	public Variable index();
	
	public String getDisplayName();

	public String getFormFieldName();
	
	public String getDataPath();
	
	public String getErrorPath();
	
	public int getDepth();

}