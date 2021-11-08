package org.finos.springbot.workflow.content.serialization;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.finos.springbot.workflow.content.Content;
import org.finos.springbot.workflow.content.Heading;
import org.finos.springbot.workflow.content.OrderedContent;
import org.finos.springbot.workflow.content.Table;
import org.springframework.util.StringUtils;
import org.springframework.web.util.HtmlUtils;

/**
 * Converts {@link Content} classes into xml/html etc.  styles of markup.
 * 
 * @author rob@kite9.com
 *
 */
public class MarkupWriter implements Function<Content, String> {
	
	Map<Class<? extends Content>, Function<Content, String>> tagMap = new LinkedHashMap<>();
	
	public MarkupWriter(Map<Class<? extends Content>, Function<Content, String>> tagMap) {
		super();
		this.tagMap = tagMap;
	}

	public MarkupWriter() {
	}

	@Override
	public String apply(Content t) {
		if (t == null) { 
			return "";
		}
		
		Function<Content, String> writer = findWriter(t);
		
		if (writer == null) {
			return "";
		} else {
			return writer.apply(t);
		}
	}

	protected Function<Content, String> findWriter(Content t) {
		return tagMap.keySet().stream()
			.filter(c -> c.isAssignableFrom(t.getClass()))
			.map(c -> tagMap.get(c))
			.findFirst().orElseGet(() -> null);
	}
	
	public class PlainWriter implements Function<Content, String> {

		@Override
		public String apply(Content t) {
			return " " + HtmlUtils.htmlEscape(t.getText())+ " ";		
		}
		
	}
	
	public class SimpleTagWriter implements Function<Content, String> {

		String tag;
		
		public SimpleTagWriter(String tag) {
			super();
			this.tag = tag;
		}

		@Override
		public String apply(Content t) {
			return "<"+tag+formatAttributes(t)+">" + HtmlUtils.htmlEscape(t.getText())+ "</"+tag+">";		
		}	
		
		protected String formatAttributes(Content t) {
			String out = getAttributes(t).entrySet().stream()
				.map(e -> HtmlUtils.htmlEscape(e.getKey()) + "=\""+HtmlUtils.htmlEscape(e.getValue()) + "\"")
				.reduce((a, b) -> a+" "+b)
				.orElse("");
			return StringUtils.hasText(out) ? " "+out : "";
		}
		
		protected Map<String, String> getAttributes(Content t) {
			return Collections.emptyMap();
		}
	}
	
	
	public class OrderedTagWriter implements Function<Content, String> {
		
		String tag;
		Function<Content, String> following;
		
		public OrderedTagWriter(String tag, Function<Content, String> following) {
			super();
			this.tag = tag;
			this.following = following;
		}
		
		public OrderedTagWriter(String tag) {
			super();
			this.tag = tag;
		}

		@Override
		public String apply(Content t) {
			return "<"+getTagName(t)+">" + 
				((OrderedContent<?>)t).getContents().stream()
					.map(c -> writeInner(c)) 
					.reduce("", (a, b) -> a.trim() + " "+ b.trim()) + 
					"</"+getTagName(t)+">";		
		}

		protected String getTagName(Content t) {
			return tag;
		}

		protected String writeInner(Content c){
			if (following == null) {
				return MarkupWriter.this.apply(c);
			} else {
				return following.apply(c);
			}
		}
		
	}
	
	public class HeadingWriter extends OrderedTagWriter {

		public HeadingWriter(String tag) {
			super(tag);
		}

		@Override
		protected String getTagName(Content t) {
			if (t instanceof Heading) {
				return tag + ((Heading)t).getLevel();
			} else {
				return tag;
			}
		}
		
		
		
	}
	
	public class TableWriter implements Function<Content, String> {

		@Override
		public String apply(Content t) {
			return "<table><thead>" 
				 + writeRow("th", ((Table) t).getColumnNames())
				 + "</thead><tbody>"
				 + ((Table) t).getData().stream()
				 		.map(r -> writeRow("td", r))
				 		.reduce("", (a, b) -> a.trim() + b.trim())
				 +"</tbody></table>";
		}
		
		private String writeRow(String tag, List<Content> row) {
			return "<tr>"
				+ row.stream()
					.map(td -> "<" + tag + ">" + MarkupWriter.this.apply(td) + "</" + tag + ">")
			 		.reduce("", (a, b) -> a.trim() + b.trim())
			 	+ "</tr>";
		}
	}
	
	public void add(Class<? extends Content> cl, Function<Content, String> mapper) {
		tagMap.put(cl, mapper);
	}

}
