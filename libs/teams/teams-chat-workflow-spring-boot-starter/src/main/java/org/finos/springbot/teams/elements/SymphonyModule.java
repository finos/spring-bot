package org.finos.springbot.teams.elements;

import java.io.IOException;

import org.finos.springbot.teams.content.TeamsChat;
import org.finos.springbot.teams.content.TeamsUser;
import org.finos.symphony.toolkit.workflow.content.Chat;
import org.finos.symphony.toolkit.workflow.content.User;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.deser.Deserializers;
import com.fasterxml.jackson.databind.node.LongNode;

/**
 * Handles conversion of symphony elements' user picker back to User objects.
 * 
 * @author Rob Moffat
 */
public class SymphonyModule extends Module {

	private static final String NAME = "Symphony Workflow Module";
	
	private static final Version VERSION = new Version(1, 0, 0, "", 
			SymphonyModule.class.getPackage().getName().toLowerCase(), 
			"workflow-module");


	@Override
	public String getModuleName() {
		return NAME;
	}

	@Override
	public Version version() {
		return VERSION;
	}
		
	public SymphonyModule() {
		super();
	}

	@Override
	public void setupModule(SetupContext context) {
		context.addDeserializers(new Deserializers.Base() {

			@Override
			public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config,
					BeanDescription beanDesc) throws JsonMappingException {
				
				if (User.class.isAssignableFrom(type.getRawClass())) {
					return new JsonDeserializer<User>() {

						@Override
						public User deserialize(JsonParser p, DeserializationContext ctxt)
								throws IOException, JsonProcessingException {
							
							TreeNode tn = p.readValueAsTree();
							if (tn.size() > 0) {
								long ul = ((LongNode) tn.get(0)).asLong();
								return new TeamsUser(ul);
							} else {
								return null;
							}
							
						}
					};
				} else if (Chat.class.isAssignableFrom(type.getRawClass())) {
					return new JsonDeserializer<Chat>() {

						@Override
						public Chat deserialize(JsonParser p, DeserializationContext ctxt)
								throws IOException, JsonProcessingException {
							return new TeamsChat(null, p.getValueAsString());
						}
					};
					
				} else {
					return null;
				}
			}
		});
	}

}
