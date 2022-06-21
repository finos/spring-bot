package org.finos.springbot.symphony.form;

import java.io.IOException;

import org.finos.springbot.symphony.content.SymphonyRoom;
import org.finos.springbot.symphony.content.SymphonyUser;
import org.finos.springbot.workflow.content.Chat;
import org.finos.springbot.workflow.content.User;
import org.finos.springbot.workflow.conversations.AllConversations;

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
import com.fasterxml.jackson.databind.node.TextNode;

/**
 * Handles conversion of symphony elements' user picker back to User objects.
 * 
 * @author Rob Moffat
 */
public class SymphonyFormDeserializerModule extends Module {

	private static final String NAME = "Symphony Form Deserializer Module";
	
	private static final Version VERSION = new Version(1, 0, 0, "", 
			SymphonyFormDeserializerModule.class.getPackage().getName().toLowerCase(), 
			"symphony-form-deserializer-module");


	@Override
	public String getModuleName() {
		return NAME;
	}

	@Override
	public Version version() {
		return VERSION;
	}
	
	private AllConversations ac;
		
	public SymphonyFormDeserializerModule(AllConversations ac) {
		this.ac = ac;
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
							if (tn.isArray() && (tn.size() > 0)) {
								long ul = ((LongNode) tn.get(0)).asLong();
								return ac.getUserById(""+ul);
							}else if (tn instanceof LongNode) {
								long ul = ((LongNode) tn).asLong();
								return ac.getUserById(""+ul);
							}else if (tn instanceof TextNode) {
								long ul = ((TextNode)tn).asLong();
								return ac.getUserById(""+ul);
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
							return ac.getChatById(p.getValueAsString());
						}
					};
					
				} else {
					return null;
				}
			}
		});
	}

}
