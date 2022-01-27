package org.finos.springbot.teams.form;

import java.io.IOException;

import org.finos.springbot.teams.content.TeamsMultiwayChat;
import org.finos.springbot.teams.content.TeamsUser;
import org.finos.springbot.teams.conversations.TeamsConversations;
import org.finos.springbot.workflow.content.Chat;
import org.finos.springbot.workflow.content.User;

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
import com.fasterxml.jackson.databind.node.TextNode;

/**
 * Handles conversion of teams user picker back to User objects.
 * 
 * @author Rob Moffat
 */
public class TeamsFormDeserializerModule extends Module {

	private static final String NAME = "Teams Form Deserializer Module";
	
	private static final Version VERSION = new Version(1, 0, 0, "", 
			TeamsFormDeserializerModule.class.getPackage().getName().toLowerCase(), 
			"teams-form-deserializer-module");

	private TeamsConversations tc;
	

	@Override
	public String getModuleName() {
		return NAME;
	}

	@Override
	public Version version() {
		return VERSION;
	}
		
	public TeamsFormDeserializerModule(TeamsConversations tc) {
		super();
		this.tc = tc;
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
							if (tn instanceof TextNode) {
								return tc.lookupUser(((TextNode)tn).asText());
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
							return new TeamsMultiwayChat(p.getValueAsString(), null);
						}
					};
					
				} else {
					return null;
				}
			}
		});
	}

}
