package org.finos.springbot.teams.handlers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.finos.springbot.workflow.response.WorkResponse;
import org.finos.springbot.workflow.response.templating.AbstractResourceTemplateProvider;
import org.finos.springbot.workflow.templating.Mode;
import org.finos.springbot.workflow.templating.WorkTemplater;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Charsets;

public class TeamsTemplateProvider extends AbstractResourceTemplateProvider<JsonNode, WorkResponse> {

	private final WorkTemplater<JsonNode> formConverter;
	
	private ObjectMapper om;
	
	private Context ctx; // javascript templating
	
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
		ctx = Context.newBuilder("js")
				 .allowHostAccess(HostAccess.ALL)
				    //allows access to all Java classes
				  .allowHostClassLookup(className -> true)
				  .allowIO(true)
				  .build();
		
		load("/js/adaptive-expressions2.min.js");
		load("/js/adaptivecards-templating2.min.js");		
	}

	private Value load(String f) throws IOException {
		Value out = ctx.eval("js", StreamUtils.copyToString(TeamsTemplateProvider.class.getResourceAsStream(f), Charsets.UTF_8));
		return out;
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
			System.out.println("DATA: \n"+ _$root);

			Value tv = singleThreadedEvalLoop(dataStr, templateStr);

			System.out.println("COMBINED: \n"+ tv.asString());

			return om.readTree(tv.asString());
				
			
		} catch (Exception e) {
			throw new RuntimeException("Couldn't template response", e);
		}
	}

	private synchronized Value singleThreadedEvalLoop(String dataStr, String templateStr) {
		Value tv = ctx.eval("js", "JSON.stringify(new ACData.Template("+templateStr+").expand("+dataStr+"))");
		return tv;
	}

	protected Map<String, Object> getData(WorkResponse t) {
		 Map<String, Object> out = t.getData();
		 if (t.getFormClass() != null) {
			 out.put("formid", t.getFormClass().getCanonicalName());
		 }
		 return out;
	}

}
