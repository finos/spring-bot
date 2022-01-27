package org.finos.springbot.symphony.data;

import java.io.IOException;

import org.finos.springbot.symphony.content.HashTag;

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
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.node.TextNode;

/**
 * Handles legacy hashtag format conversion to new 8.0.0 plus format.
 * 
 * @author Rob Moffat
 */
public class LegacyFormatModule extends Module {

	private static final String NAME = "Symphony Workflow Module";
	
	private static final Version VERSION = new Version(1, 0, 0, "", 
			LegacyFormatModule.class.getPackage().getName().toLowerCase(), 
			"legacy-hashtag-module");


	@Override
	public String getModuleName() {
		return NAME;
	}

	@Override
	public Version version() {
		return VERSION;
	}
		
	public LegacyFormatModule() {
		super();
	}

	@Override
	public void setupModule(SetupContext context) {
		context.addDeserializers(new Deserializers.Base() {

			@Override
			public JsonDeserializer<?> findBeanDeserializer(JavaType type, DeserializationConfig config,
					BeanDescription beanDesc) throws JsonMappingException {
				
				
				if (HashTag.class.isAssignableFrom(type.getRawClass())) {
					return new JsonDeserializer<HashTag>() {

						@Override
						public HashTag deserialize(JsonParser p, DeserializationContext ctxt)
								throws IOException, JsonProcessingException {
							
							TreeNode tn = p.readValueAsTree();
  							TreeNode id = tn.get("id");
							TreeNode value = tn.get("value");
							
							if (id instanceof TextNode) {
								return new HashTag(((TextNode) id).asText());
							} else if (value instanceof TextNode) {
								return new HashTag(((TextNode) value).asText());
							} else {
								throw new InvalidFormatException(p, "Couldn't create HashTag", null, HashTag.class);
							}
						}

						@Override
						public HashTag deserializeWithType(JsonParser p, DeserializationContext ctxt,
								TypeDeserializer typeDeserializer) throws IOException {
							return deserialize(p, ctxt);
						}

						@Override
						public HashTag deserializeWithType(JsonParser p, DeserializationContext ctxt,
								TypeDeserializer typeDeserializer, HashTag intoValue) throws IOException {
							return deserialize(p, ctxt);
						}
						
						
					};
				} else {
					return null;
				}
			}
		});
	}

}
