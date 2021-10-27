package org.finos.springbot.symphony.templating;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.finos.springbot.workflow.content.User;
import org.finos.springbot.workflow.templating.AbstractClassConverter;
import org.finos.springbot.workflow.templating.Rendering;
import org.finos.springbot.workflow.templating.Variable;
import org.finos.symphony.toolkit.json.EntityJson;

import com.symphony.user.EmailAddress;
import com.symphony.user.UserId;

/**
 * This does a symphony user drop-down.  We're not using this directly 
 * anymore.
 * 
 * @author rob
 *
 */
public class UserConverter extends AbstractClassConverter<String> {

	public UserConverter(Rendering<String> r) {
		super(LOW_PRIORITY, r, User.class);
	}

	@Override
	public String apply(Field ctx, Type t, boolean editMode, Variable variable) {
		if (editMode) {
			return formatErrorsAndIndent(variable.getFormFieldName(), variable.depth) 
					+ "<person-selector " 
					+ attribute(variable, "name", variable.getFormFieldName())
					+ attribute(variable, "placeholder", variable.getDisplayName())
					+" required=\"false\"/>";
		} else {
			String id = variable.field("id").getDataPath();
			String userId = EntityJson.getSymphonyTypeName(UserId.class);
			String emailAddress = EntityJson.getSymphonyTypeName(EmailAddress.class);
			
			return indent(variable.depth) +  "<#if " + variable.getDataPath() +"??>"
					+ indent(variable.depth)+ "   <#list "+id+" as id>"
					+ indent(variable.depth)+ "     <#if id.type == '"+userId+"'>"
					+ indent(variable.depth)+ "       <mention uid=\"${id.value}\" />    "
					+ indent(variable.depth)+ "       <#break />"
					+ indent(variable.depth)+ "     </#if>  "
					+ indent(variable.depth)+ "     <#if id.type == '"+emailAddress+"'>"
					+ indent(variable.depth)+ "       <mention email=\"${id.value}\" />    "
					+ indent(variable.depth)+ "       <#break />"
					+ indent(variable.depth)+ "     </#if>    "
					+ indent(variable.depth)+ "   </#list>"
					+ indent(variable.depth)+ "</#if>";
		}
	}

}
