package org.finos.springbot.workflow.content.serialization;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import org.finos.springbot.workflow.content.Content;
import org.finos.springbot.workflow.content.Heading;
import org.finos.springbot.workflow.content.Image;
import org.finos.springbot.workflow.content.Link;
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
public class MarkupWriter<C> implements BiFunction<Content, C, String> {
	
	Map<Class<? extends Content>, BiFunction<Content, C,String>> tagMap = new LinkedHashMap<>();
	
	public MarkupWriter(Map<Class<? extends Content>, BiFunction<Content, C, String>> tagMap) {
		super();
		this.tagMap = tagMap;
	}

	public MarkupWriter() {
	}

	@Override
	public String apply(Content t, C c) {
		if (t == null) { 
			return "";
		}
		
		BiFunction<Content, C, String> writer = findWriter(t);
		
		if (writer == null) {
			return "";
		} else {
			return writer.apply(t, c);
		}
	}

	protected BiFunction<Content, C, String> findWriter(Content t) {
		return tagMap.keySet().stream()
			.filter(c -> c.isAssignableFrom(t.getClass()))
			.map(c -> tagMap.get(c))
			.findFirst().orElseGet(() -> null);
	}
	
	public class PlainWriter implements BiFunction<Content, C, String> {

		@Override
		public String apply(Content t, C c) {
			return " " + HtmlUtils.htmlEscape(t.getText())+ " ";		
		}
		
	}
	
	public class SimpleTagWriter implements BiFunction<Content, C, String> {

		String tag;
		
		public SimpleTagWriter(String tag) {
			super();
			this.tag = tag;
		}

		@Override
		public String apply(Content t, C c) {
			return "<"+tag+formatAttributes(t)+">" + getContainedMarkup(t)+ "</"+tag+">";		
		}

		protected String getContainedMarkup(Content t) {
			return HtmlUtils.htmlEscape(t.getText());
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
	
	
	public class OrderedTagWriter implements BiFunction<Content, C, String> {
		
		String tag;
		BiFunction<Content, C, String> following;
		
		public OrderedTagWriter(String tag, BiFunction<Content, C, String> following) {
			super();
			this.tag = tag;
			this.following = following;
		}
		
		public OrderedTagWriter(String tag) {
			super();
			this.tag = tag;
		}

		@Override
		public String apply(Content t, C ctx) {
			return "<"+getTagName(t)+">" + 
				((OrderedContent<?>)t).getContents().stream()
					.map(c -> writeInner(c, ctx)) 
					.reduce("", (a, b) -> a.trim() + " "+ b.trim()) + 
					"</"+getTagName(t)+">";		
		}

		protected String getTagName(Content t) {
			return tag;
		}

		protected String writeInner(Content c, C ctx){
			if (following == null) {
				return MarkupWriter.this.apply(c, ctx);
			} else {
				return following.apply(c, ctx);
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
	
	public class TableWriter implements BiFunction<Content, C, String> {

		@Override
		public String apply(Content t, C c) {
			return "<table><thead>" 
				 + writeRow("th", ((Table) t).getColumnNames(), c)
				 + "</thead><tbody>"
				 + ((Table) t).getData().stream()
				 		.map(r -> writeRow("td", r, c))
				 		.reduce("", (a, b) -> a.trim() + b.trim())
				 +"</tbody></table>";
		}
		
		private String writeRow(String tag, List<Content> row, C c) {
			return "<tr>"
				+ row.stream()
					.map(td -> "<" + tag + ">" + MarkupWriter.this.apply(td, c) + "</" + tag + ">")
			 		.reduce("", (a, b) -> a.trim() + b.trim())
			 	+ "</tr>";
		}
	}
	
	public class LinkWriter extends SimpleTagWriter {
		
		public LinkWriter() {
			super("a");
		}

		@Override
		protected Map<String, String> getAttributes(Content t) {
			return Collections.singletonMap("href", ((Link)t).getHRef());
		}
	}

	public class ImageWriter extends SimpleTagWriter {
		
		public ImageWriter() {
			super("img");
		}

		@Override
		protected Map<String, String> getAttributes(Content t) {
			return Collections.singletonMap("src", ((Image)t).getUrl());
		}
	}

	
	public void add(Class<? extends Content> cl, BiFunction<Content, C, String> mapper) {
		tagMap.put(cl, mapper);
	}

}
