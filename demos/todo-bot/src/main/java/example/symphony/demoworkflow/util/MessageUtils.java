package example.symphony.demoworkflow.util;

import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * @author rupnsur
 *
 */
public class MessageUtils {
	private static LinkedHashMap<String, String> tokens = null;
	private static Pattern pattern = null;

	private static void init() {
		if (tokens != null) {
			return;
		}
		tokens = new LinkedHashMap<>();
		tokens.put("&", "&amp;");
		tokens.put("<", "&lt;");
		tokens.put(">", "&gt;");
		tokens.put("'", "&apos;");
		tokens.put("\"", "&quot;");
		tokens.put("\\$", "&#36;");
		tokens.put("#", "&#35;");
		tokens.put("\\(", "&#40;");
		tokens.put("\\)", "&#41;");
		tokens.put("=", "&#61;");
		tokens.put(";", "&#59;");
		tokens.put("\\\\", "&#92;");
		tokens.put("\\.", "&#46;");
		tokens.put("`", "&#96;");
		tokens.put("%", "&#37;");
		tokens.put("\\*", "&#42;");
		tokens.put("\\[", "&#91;");
		tokens.put("\\]", "&#93;");
		tokens.put("\\{", "&#123;");
		tokens.put("\\}", "&#125;");

		pattern = Pattern.compile("(" + StringUtils.join(tokens.keySet(), "|") + ")");
	}

	public static String escapeText(String rawText) {
		init();

		Matcher matcher = pattern.matcher(rawText);
		StringBuffer buffer = new StringBuffer();
		while (matcher.find()) {
			String group = matcher.group(1);
			String replacement = tokens.get(group);
			if (replacement == null) {
				replacement = tokens.get("\\" + group);
			}
			matcher.appendReplacement(buffer, replacement);
		}
		matcher.appendTail(buffer);

		return buffer.toString();
	}

	public static String escapeStreamId(String rawStreamId) {
		return rawStreamId.trim().replaceAll("[=]+$", "").replaceAll("\\+", "-").replaceAll("/", "_");
	}
}
