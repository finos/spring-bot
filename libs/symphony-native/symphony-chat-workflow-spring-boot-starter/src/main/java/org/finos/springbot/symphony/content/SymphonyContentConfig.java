package org.finos.springbot.symphony.content;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.finos.springbot.symphony.content.serialization.MessageMLParser;
import org.finos.springbot.workflow.content.BlockQuote;
import org.finos.springbot.workflow.content.CodeBlock;
import org.finos.springbot.workflow.content.Content;
import org.finos.springbot.workflow.content.Heading;
import org.finos.springbot.workflow.content.Image;
import org.finos.springbot.workflow.content.Link;
import org.finos.springbot.workflow.content.Message;
import org.finos.springbot.workflow.content.OrderedList;
import org.finos.springbot.workflow.content.Paragraph;
import org.finos.springbot.workflow.content.Table;
import org.finos.springbot.workflow.content.UnorderedList;
import org.finos.springbot.workflow.content.User;
import org.finos.springbot.workflow.content.Word;
import org.finos.springbot.workflow.content.serialization.MarkupWriter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SymphonyContentConfig {

	@Bean
	@ConditionalOnMissingBean
	public MessageMLParser symphonyMessageMLParser() {
		return new MessageMLParser();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public MarkupWriter symphonyMessageMLWriter() {
		MarkupWriter out = new MarkupWriter();
		out.add(Message.class, out.new OrderedTagWriter("messageML"));
		out.add(Paragraph.class, out.new OrderedTagWriter("p"));
		out.add(OrderedList.class, out.new OrderedTagWriter("ol", out.new OrderedTagWriter("li")));
		out.add(UnorderedList.class, out.new OrderedTagWriter("ul", out.new OrderedTagWriter("li")));

		out.add(BlockQuote.class, out.new SimpleTagWriter("div") {
			@Override
			protected Map<String, String> getAttributes(Content t) {
				return Collections.singletonMap("style", "color: blue; margin: 15px;");
			}
		});
		
		out.add(CodeBlock.class, out.new SimpleTagWriter("code"));
		
		out.add(Word.class, out.new PlainWriter());
		out.add(Table.class, out.new TableWriter());
		out.add(Heading.class, out.new HeadingWriter("h"));
		out.add(User.class, out.new SimpleTagWriter("mention") {

			@Override
			protected Map<String, String> getAttributes(Content t) {
				SymphonyUser su = (SymphonyUser) t;
				Map<String, String> out = new LinkedHashMap<String, String>();
				if (su.getEmailAddress() != null) {
					out.put("email", su.getEmailAddress());
				}
				
				if (su.getUserId() != null) {
					out.put("uid", su.getUserId());
				}
				
				return out;
			}
		});
		
		out.add(CashTag.class, out.new SimpleTagWriter("cash") {

			@Override
			protected Map<String, String> getAttributes(Content t) {
				return Collections.singletonMap("tag", ((CashTag)t).getName());
			}
			
		});
		
		out.add(HashTag.class, out.new SimpleTagWriter("hash") {

			@Override
			protected Map<String, String> getAttributes(Content t) {
				return Collections.singletonMap("tag", ((HashTag)t).getName());
			}
			
		});
		
		out.add(Image.class, out.new SimpleTagWriter("img") {

			@Override
			protected Map<String, String> getAttributes(Content t) {
				return Collections.singletonMap("src", ((Image)t).getUrl());
			}
			
		});
		
		out.add(Link.class, out.new SimpleTagWriter("a") {

			@Override
			protected Map<String, String> getAttributes(Content t) {
				return Collections.singletonMap("href", ((Link)t).getHRef());
			}
			
		});
		
		return out;
	}
	
}
