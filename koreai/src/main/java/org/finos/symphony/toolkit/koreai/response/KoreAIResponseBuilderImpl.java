package org.finos.symphony.toolkit.koreai.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class KoreAIResponseBuilderImpl implements KoreAIResponseBuilder {

	ObjectMapper om;
	
	public KoreAIResponseBuilderImpl(ObjectMapper om) {
		super();
		this.om = om;
	}

	@Override
	public KoreAIResponse formatResponse(String json) throws Exception {
		TypeReference<Map<String, Object>> tr = new TypeReference<Map<String,Object>>() {};
		Map<String, Object> out = om.readValue(json, tr);
		KoreAIResponse r = new KoreAIResponse();
		r.setResponse(out);
		
		String text = getFirstText(out);
		
		// check for nested templates
		if ((text instanceof String) && (((String)text).startsWith("{\"type\":"))) {
			Map<String, Object> template = om.readValue(text, tr);
			r.setTemplate(template);
			text = getFirstText(template);
        }
		
		// handle messageML conversion
		String messageML = handleMessageMLConversion(text);
		r.setMessageML(messageML);
		
		
		// handle options
		handleOptions(r);
		
		// figure out correct template to use
		decideTemplate(r);
		
		return r;
	}

	protected void decideTemplate(KoreAIResponse kr) {
		if (!kr.getOptions().isEmpty()) {
        	kr.setSymphonyTemplate("form");
        }
	}

	   
	public static final String BR = "<br />";
	
	protected String handleMessageMLConversion(String text) {
		// convert newlines to <br />
		text = text.replaceAll("\\\\n", "\n").replaceAll("\n", BR);
		
		// now convert urls to symphony format
		text = text.replaceAll("(https?:\\/\\/[\\w.\\/\\+_\\=\\-\\?]*)", "<a href=\"$1\">$1</a>");
		return text;
	}

	protected String getFirstText(Map<String, Object> in){
		if (in.get("text") instanceof String) {
			return (String) in.get("text");
		} else if (in.get("text") instanceof List) {
			return ((List<String>) in.get("text")).get(0);
		} else {
			return "";
		}
	}
			
	public static final Pattern OPTION = Pattern.compile("^[ ]?[a-z]\\)\\ (.*)$");

	public void handleOptions(KoreAIResponse template) {
		String messageML = template.getMessageML();
		
        String[] multiline = messageML.split(BR);
        StringBuilder text = new StringBuilder();
        List<String> options = new ArrayList<String>();
        for (String string : multiline) {
        	Matcher m = OPTION.matcher(string);
			if (m.find()) {
				options.add(m.group(1));
			} else {
				text.append(string);
				text.append(BR);
			}
		}
        
        template.setOptions(options);
        template.setMessageML(text.toString());
    }
	
}
