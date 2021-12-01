package org.finos.springbot.teams.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.finos.springbot.workflow.response.WorkResponse;
import org.finos.springbot.workflow.response.templating.AbstractResourceTemplateProvider;
import org.finos.springbot.workflow.templating.Mode;
import org.finos.springbot.workflow.templating.WorkTemplater;
import org.springframework.core.io.ResourceLoader;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class TeamsTemplateProvider extends AbstractResourceTemplateProvider<JsonNode, WorkResponse> {

	private final WorkTemplater<JsonNode> formConverter;
	
	private ObjectMapper om;
	private JavascriptSubstitution js = new JavascriptSubstitution();
	
	public TeamsTemplateProvider(
			String templatePrefix, 
			String templateSuffix, 
			ResourceLoader rl, 
			WorkTemplater<JsonNode> formConverter
		) throws IOException {
		super(templatePrefix, templateSuffix, rl);
		this.formConverter = formConverter;
		this.om = new ObjectMapper();
		this.om.setSerializationInclusion(Include.NON_ABSENT);
	}

	@Override
	protected JsonNode getDefaultTemplate(WorkResponse r) {
		JsonNode insert;
		if (WorkResponse.DEFAULT_FORM_TEMPLATE_EDIT.equals(r.getTemplateName())) {
			Class<?> c = ((WorkResponse) r).getFormClass();
			insert = formConverter.convert(c, Mode.FORM);
		} else if (WorkResponse.DEFAULT_FORM_TEMPLATE_VIEW.equals(r.getTemplateName())) {
			Class<?> c = ((WorkResponse) r).getFormClass();
			boolean needsButtons = needsButtons(r);						
			insert = formConverter.convert(c, needsButtons ? Mode.DISPLAY_WITH_BUTTONS : Mode.DISPLAY);
		} else {
			throw new UnsupportedOperationException("Don't know how to construct default template for "+r);
		}
		
		return insert;
	}
	
	protected boolean needsButtons(WorkResponse r) {
		// TODO Auto-generated method stub
		return false;
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

			System.out.println("COMBINED: \n"+ tv);

			return om.readTree(tv);
				
			
		} catch (Exception e) {
			throw new RuntimeException("Couldn't template response", e);
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
