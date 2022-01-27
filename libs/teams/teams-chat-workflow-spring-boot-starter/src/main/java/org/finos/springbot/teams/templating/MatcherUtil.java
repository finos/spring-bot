package org.finos.springbot.teams.templating;

import java.util.Objects;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;

public class MatcherUtil {

	/**
	 * Added this here since it's only available since Java1.9 in Matcher.
	 */
	public static String replaceAll(String in, Matcher m, Function<MatchResult, String> replacer) {
	    Objects.requireNonNull(replacer);
	    int at = 0;
	    boolean result = m.find();
	    
	    if (result) {
	        StringBuilder sb = new StringBuilder();
	        do {
	        	sb.append(in.substring(at, m.start()));
	            String replacement =  replacer.apply(m.toMatchResult());
	            sb.append(replacement);
	            at = m.end();
	            result = m.find();
	        } while (result);
	        sb.append(in.substring(at));
	        return sb.toString();
	    }
	    return in;
	}

}
