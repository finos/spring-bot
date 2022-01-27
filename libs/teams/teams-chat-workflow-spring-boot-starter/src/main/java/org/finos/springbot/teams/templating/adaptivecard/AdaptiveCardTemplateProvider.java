package org.finos.springbot.teams.templating.adaptivecard;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.finos.springbot.teams.templating.MatcherUtil;
import org.finos.springbot.workflow.annotations.WorkMode;
import org.finos.springbot.workflow.form.ButtonList;
import org.finos.springbot.workflow.response.WorkResponse;
import org.finos.springbot.workflow.response.templating.AbstractResourceTemplateProvider;
import org.finos.springbot.workflow.templating.Mode;
import org.finos.springbot.workflow.templating.WorkTemplater;
import org.springframework.core.io.ResourceLoader;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class AdaptiveCardTemplateProvider extends AbstractResourceTemplateProvider<JsonNode, JsonNode, WorkResponse> {

	private final WorkTemplater<JsonNode> formConverter;
	
	protected ObjectMapper om;
	protected JavascriptSubstitution js = new JavascriptSubstitution();
	
	public AdaptiveCardTemplateProvider(
			String templatePrefix, 
			String templateSuffix, 
			String defaultTemplateName,
			ResourceLoader rl, 
			WorkTemplater<JsonNode> formConverter
		) throws IOException {
		super(templatePrefix, templateSuffix, defaultTemplateName, rl);
		this.formConverter = formConverter;
		this.om = new ObjectMapper();
		this.om.registerModule(new JavaTimeModule());
		this.om.setSerializationInclusion(Include.NON_ABSENT);
		this.om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	}

	@Override
	protected JsonNode getDefaultTemplate(WorkResponse r) {
		JsonNode insert;
		if ((WorkResponse.DEFAULT_FORM_TEMPLATE_EDIT.equals(r.getTemplateName())) || (r.getMode() == WorkMode.EDIT)) {
			Class<?> c = ((WorkResponse) r).getFormClass();
			insert = formConverter.convert(c, Mode.FORM);
		} else if ((WorkResponse.DEFAULT_FORM_TEMPLATE_VIEW.equals(r.getTemplateName())) || (r.getMode() == WorkMode.VIEW)) {
			Class<?> c = ((WorkResponse) r).getFormClass();
			boolean needsButtons = needsButtons(r);						
			insert = formConverter.convert(c, needsButtons ? Mode.DISPLAY_WITH_BUTTONS : Mode.DISPLAY);
		} else {
			throw new UnsupportedOperationException("Don't know how to construct default template for "+r);
		}
		
		return insert;
	}
	
	protected boolean needsButtons(WorkResponse r) {
		Map<String, Object> data = r.getData();
		ButtonList bl = (ButtonList) data.get(ButtonList.KEY);
		return ((bl != null) && (bl.getContents().size() > 0));
	}

	@Override
	protected JsonNode deserializeTemplate(InputStream is) throws IOException {
		return om.readTree(is);
	}

	@Override
	protected JsonNode applyTemplate(JsonNode template, WorkResponse t) {
		
		JsonNode _$root = om.valueToTree(getData(t));
		ObjectNode data = om.createObjectNode();
		data.set("$root", _$root);
		
		try {
			String dataStr = om.writerWithDefaultPrettyPrinter().writeValueAsString(data);
			String templateStr = om.writerWithDefaultPrettyPrinter().writeValueAsString(template);

			System.out.println("TEMPLATE: \n"+templateStr); 
			System.out.println("DATA: \n"+ om.writerWithDefaultPrettyPrinter().writeValueAsString(_$root));

			String tv = js.singleThreadedEvalLoop(dataStr, templateStr);
			JsonNode combined = om.readTree(tv);
			handleFormFieldNames(new HashMap<String, Integer>(), combined, null, null);
			
			System.out.println("COMBINED: \n"+ om.writerWithDefaultPrettyPrinter().writeValueAsString(combined));
			return combined;
			
		} catch (Exception e) {
			throw new RuntimeException("Couldn't template response", e);
		}
	}
	
	static final Pattern INDEX_REPLACER = Pattern.compile("\\[\\[index:(.*?)\\]\\]");

	private void handleFormFieldNames(Map<String, Integer> indexes, JsonNode n, ObjectNode parent, String fieldName) {
		if (n.isTextual() && (n.asText().startsWith(ACVariable.FORM_IDENTIFIER))) {
			String out = n.asText().substring(ACVariable.FORM_IDENTIFIER.length());
			
			Matcher m = INDEX_REPLACER.matcher(out);
			out = MatcherUtil.replaceAll(out, m, r -> {
				String group1 = r.group(1);
				int currentInt = indexes.getOrDefault(group1, 0);
				return "["+currentInt+"]";
			});
			
			parent.replace(fieldName, TextNode.valueOf(out));
			
		} else if (n.isArray()) {
			for (JsonNode jsonNode : n) {
				handleFormFieldNames(indexes, jsonNode, null, null);
			}
		} else if (n.isObject()) {
			ObjectNode on = ((ObjectNode) n);
			
			for (Iterator<String> iterator = on.fieldNames(); iterator.hasNext();) {
				String field = iterator.next();
				JsonNode value = on.get(field);
				handleFormFieldNames(indexes, value, on, field);
			}
			
			if (n.has(ACVariable.FORM_INCREMENT)) {
				// we need to increment here.
				String out = ((ObjectNode)n).remove(ACVariable.FORM_INCREMENT).asText();
				Matcher m = INDEX_REPLACER.matcher(out);
				if (m.find(out.lastIndexOf("[["))) {
					String group1 = m.group(1);
					int currentInt = indexes.getOrDefault(group1, 0);
					indexes.put(group1, currentInt+1);
				}
			}
		} 
	}

	protected Map<String, Object> getData(WorkResponse t) {
		 Map<String, Object> out = t.getData();
		 if (t.getFormClass() != null) {
			 out.put("formid", t.getFormClass().getCanonicalName());
		 }
		 return out;
	}

}
