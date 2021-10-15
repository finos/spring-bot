package org.finos.springbot.sources.teams.handlers;

import java.io.IOException;
import java.io.InputStream;

import org.finos.springbot.workflow.form.ButtonList;
import org.finos.springbot.workflow.response.Response;
import org.finos.springbot.workflow.response.WorkResponse;
import org.finos.springbot.workflow.response.templating.AbstractResourceTemplateProvider;
import org.springframework.core.io.ResourceLoader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TeamsTemplateProvider extends AbstractResourceTemplateProvider<JsonNode, WorkResponse> {

	private final WorkConverter<Mode, JsonNode> formConverter;
	
	private ObjectMapper om;
	
	public TeamsTemplateProvider(
			String templatePrefix, 
			String templateSuffix, 
			ResourceLoader rl, 
			WorkConverter<Mode, JsonNode> formConverter
		) {
		super(templatePrefix, templateSuffix, rl);
		this.formConverter = formConverter;
		this.om = new ObjectMapper();
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
	
	protected boolean needsButtons(Response r) {
		if (r instanceof WorkResponse) {
			ButtonList bl = (ButtonList) ((WorkResponse) r).getData().get(ButtonList.KEY);
			return (bl != null) && (bl.getContents().size() > 0);
		} else {
			return false;
		}
	}

	@Override
	protected JsonNode deserializeTemplate(InputStream is) throws IOException {
		return om.readTree(is);
	}

	@Override
	protected JsonNode applyTemplate(JsonNode template, WorkResponse t) {
		Object data = t.getData();
		try {
			System.out.println("TEMPLATE: \n"+ om.writerWithDefaultPrettyPrinter().writeValueAsString(template));
			System.out.println("DATA: \n"+ om.writerWithDefaultPrettyPrinter().writeValueAsString(data));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return template;
	}

}
