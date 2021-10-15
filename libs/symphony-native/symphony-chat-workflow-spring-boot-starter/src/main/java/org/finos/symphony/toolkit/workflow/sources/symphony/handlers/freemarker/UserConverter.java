package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.finos.springbot.workflow.content.User;
import org.finos.symphony.toolkit.json.EntityJson;

import com.symphony.user.EmailAddress;
import com.symphony.user.UserId;

public class UserConverter extends AbstractClassConverter {

	public UserConverter() {
		super(LOW_PRIORITY, User.class);
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
