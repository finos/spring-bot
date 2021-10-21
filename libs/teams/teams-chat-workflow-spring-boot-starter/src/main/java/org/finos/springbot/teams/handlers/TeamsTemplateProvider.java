package org.finos.springbot.teams.handlers;

import java.io.IOException;
import java.io.InputStream;

import org.finos.springbot.workflow.annotations.WorkMode;
import org.finos.springbot.workflow.response.WorkResponse;
import org.finos.springbot.workflow.response.templating.AbstractResourceTemplateProvider;
import org.finos.springbot.workflow.templating.WorkTemplater;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Charsets;

public class TeamsTemplateProvider extends AbstractResourceTemplateProvider<JsonNode, WorkResponse> {

	private final WorkTemplater<JsonNode, WorkMode> formConverter;
	
	private ObjectMapper om;
	
	private Context ctx; // javascript templating
	
	public TeamsTemplateProvider(
			String templatePrefix, 
			String templateSuffix, 
			ResourceLoader rl, 
			WorkTemplater<JsonNode, WorkMode> formConverter
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
		
		// load libraries
		//ctx.eval("js", "https://unpkg.com/adaptivecards-templating/dist/adaptivecards-templating.min.js");
		
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
			insert = formConverter.convert(c, WorkMode.EDIT);
		} else if (WorkResponse.DEFAULT_FORM_TEMPLATE_VIEW.equals(r.getTemplateName())) {
			Class<?> c = ((WorkResponse) r).getFormClass();
			insert = formConverter.convert(c, WorkMode.VIEW);
		} else {
			throw new UnsupportedOperationException("Don't know how to construct default template for "+r);
		}
		
		return insert;
	}
	
	@Override
	protected JsonNode deserializeTemplate(InputStream is) throws IOException {
		return om.readTree(is);
	}

	@Override
	protected JsonNode applyTemplate(JsonNode template, WorkResponse t) {
		
		JsonNode _$root = om.valueToTree(t.getData());
		ObjectNode data = om.createObjectNode();
		data.set("$root", _$root);
		
		try {
			String dataStr = om.writerWithDefaultPrettyPrinter().writeValueAsString(data);
			String templateStr = om.writerWithDefaultPrettyPrinter().writeValueAsString(template);

			System.out.println("TEMPLATE: \n"+templateStr); 
			System.out.println("DATA: \n"+ dataStr);

			Value tv = ctx.eval("js", "JSON.stringify(new ACData.Template("+templateStr+").expand("+dataStr+"))");

			
//			JsonNode dataNode = om.valueToTree(data);
//			((ObjectNode)template).set("$data", dataNode);
//			System.out.println("COMBINED: \n"+ om.writerWithDefaultPrettyPrinter().writeValueAsString(template));
	
			return om.readTree(tv.asString());
				
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return template;
	}

}
