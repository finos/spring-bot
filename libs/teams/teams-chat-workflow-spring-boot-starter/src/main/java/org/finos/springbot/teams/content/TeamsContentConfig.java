package org.finos.springbot.teams.content;

import org.finos.springbot.workflow.content.BlockQuote;
import org.finos.springbot.workflow.content.CodeBlock;
import org.finos.springbot.workflow.content.Heading;
import org.finos.springbot.workflow.content.Image;
import org.finos.springbot.workflow.content.Link;
import org.finos.springbot.workflow.content.Message;
import org.finos.springbot.workflow.content.OrderedList;
import org.finos.springbot.workflow.content.Paragraph;
import org.finos.springbot.workflow.content.Table;
import org.finos.springbot.workflow.content.UnorderedList;
import org.finos.springbot.workflow.content.Word;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TeamsContentConfig {
	

	@Bean
	@ConditionalOnMissingBean
	public TeamsMarkupWriter teamsHTMLWriter() {
		TeamsMarkupWriter out = new TeamsMarkupWriter();
		out.add(Message.class, out.new OrderedTagWriter("div"));
		out.add(Paragraph.class, out.new OrderedTagWriter("p"));
		out.add(OrderedList.class, out.new OrderedTagWriter("ol", out.new OrderedTagWriter("li")));
		out.add(UnorderedList.class, out.new OrderedTagWriter("ul", out.new OrderedTagWriter("li")));
		out.add(BlockQuote.class, out.new SimpleTagWriter("blockquote"));
		out.add(Word.class, out.new PlainWriter());
		out.add(Table.class, out.new TableWriter());
		out.add(CodeBlock.class, out.new SimpleTagWriter("code"));
		out.add(Heading.class, out.new HeadingWriter("h"));
		out.add(Image.class, out.new ImageWriter());
		out.add(Link.class, out.new LinkWriter());
		out.add(TeamsMention.class, out.new EntityTagWriter("at"));
		return out;
	}
	
	
	@Bean
	@ConditionalOnMissingBean
	public TeamsHTMLParser teamsHTMLParser() {
		return new TeamsHTMLParser();
	}
	
}
