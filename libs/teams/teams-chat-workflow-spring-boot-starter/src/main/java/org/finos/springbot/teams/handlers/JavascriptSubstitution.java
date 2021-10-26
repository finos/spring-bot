package org.finos.springbot.teams.handlers;

import java.io.IOException;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.springframework.util.StreamUtils;

import com.google.common.base.Charsets;

public class JavascriptSubstitution {

	private Context ctx; // javascript templating
	
	public JavascriptSubstitution() {
		super();
		try {
			ctx = Context.newBuilder("js")
					 .allowHostAccess(HostAccess.ALL)
					    //allows access to all Java classes
					  .allowHostClassLookup(className -> true)
					  .allowIO(true)
					  .build();
			
			load("/js/adaptive-expressions2.min.js");
			load("/js/adaptivecards-templating2.min.js");
		} catch (IOException e) {
			throw new RuntimeException("Shouldn't happen - js missing", e);
		}		
	}

	public synchronized String singleThreadedEvalLoop(String dataStr, String templateStr) {
		Value tv = ctx.eval("js", "JSON.stringify(new ACData.Template("+templateStr+").expand("+dataStr+"))");
		return tv.asString();
	}

	private Value load(String f) throws IOException {
		Value out = ctx.eval("js", StreamUtils.copyToString(TeamsTemplateProvider.class.getResourceAsStream(f), Charsets.UTF_8));
		return out;
	}

}
