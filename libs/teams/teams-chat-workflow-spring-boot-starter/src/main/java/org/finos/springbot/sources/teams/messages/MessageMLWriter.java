package org.finos.springbot.sources.teams.messages;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.finos.symphony.toolkit.workflow.content.Content;
import org.finos.symphony.toolkit.workflow.content.OrderedContent;
import org.finos.symphony.toolkit.workflow.content.Table;
import org.springframework.web.util.HtmlUtils;

public class MessageMLWriter implements Function<Content, String> {
	
	Map<Class<? extends Content>, Function<Content, String>> tagMap = new HashMap<>();
	
	public MessageMLWriter(Map<Class<? extends Content>, Function<Content, String>> tagMap) {
		super();
		this.tagMap = tagMap;
	}

	public MessageMLWriter() {
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
			return "<"+tag+">" + HtmlUtils.htmlEscape(t.getText())+ "</"+tag+">";		
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
			return "<"+tag+">" + 
				((OrderedContent<?>)t).getContents().stream()
					.map(c -> writeInner(c)) 
					.reduce("", (a, b) -> a.trim() + " "+ b.trim()) + 
					"</"+tag+">";		
		}

		protected String writeInner(Content c){
			if (following == null) {
				return MessageMLWriter.this.apply(c);
			} else {
				return following.apply(c);
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
					.map(td -> "<" + tag + ">" + MessageMLWriter.this.apply(td) + "</" + tag + ">")
			 		.reduce("", (a, b) -> a.trim() + b.trim())
			 	+ "</tr>";
		}
	}
	
	public void add(Class<? extends Content> cl, Function<Content, String> mapper) {
		tagMap.put(cl, mapper);
	}

}
