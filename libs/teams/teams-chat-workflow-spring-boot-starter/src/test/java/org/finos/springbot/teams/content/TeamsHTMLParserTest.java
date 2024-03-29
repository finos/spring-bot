package org.finos.springbot.teams.content;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.finos.springbot.teams.content.serialization.ParseContext;
import org.finos.springbot.teams.content.serialization.TeamsHTMLParser;
import org.finos.springbot.teams.conversations.TeamsConversations;
import org.finos.springbot.workflow.content.BlockQuote;
import org.finos.springbot.workflow.content.Image;
import org.finos.springbot.workflow.content.Message;
import org.finos.springbot.workflow.content.OrderedList;
import org.finos.springbot.workflow.content.Paragraph;
import org.finos.springbot.workflow.content.UnorderedList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.bot.schema.Entity;

@SpringBootTest(classes = {
		TeamsContentConfig.class,
})
public class TeamsHTMLParserTest {

	@Autowired
	TeamsHTMLParser parser;
	
	@MockBean
	TeamsConversations tc;
	
	@Test
	public void testSimpleImageMessageParse() {
		Message m = parser.apply("<div><img src=\"https://abc.123/image.jpg\" width=\"333\" height=\"250\" alt=\"Some alt\" style=\"padding-top:5px\"></div>", null);
		Assertions.assertEquals(1, m.only(Image.class).size());
		Assertions.assertEquals("https://abc.123/image.jpg", m.getNth(Image.class, 0).get().getUrl());
		Assertions.assertEquals("Some alt", m.getNth(Image.class, 0).get().getAlt());
	}
	
	@Test
	public void testListAndBlockQuoteMarkup() {
		Message m = parser.apply("<p>this <strong>is a piece </strong></p>\n"
				+ "<ol>\n"
				+ "<li><strong>of text</strong><br>\n"
				+ "</li><li>that is <br>\n"
				+ "</li><li>formatted<br>\n"
				+ "</li></ol>\n"
				+ "<blockquote>\n"
				+ "<p>To be or not to be<br>\n"
				+ "</p>\n"
				+ "</blockquote>\n"
				+ "<p><br>\n"
				+ "</p>", null);
		
		OrderedList ul = m.getNth(OrderedList.class, 0).get();
		Assertions.assertEquals(3, ul.getContents().size());
		Assertions.assertEquals("formatted", ul.getNth(Paragraph.class, 2).get().getText());
		BlockQuote cb = m.getNth(BlockQuote.class, 0).get();
		Paragraph p = cb.getNth(Paragraph.class, 0).get();
		Assertions.assertEquals("To be or not to be", p.getText());
	}
	
	@Test
	public void testMixedBlockQuoteParse() {
		Message m = parser.apply("<blockquote>\n"
				+ "<ul>\n"
				+ "<li>one</li><li>two</li><li>three</li></ul>\n"
				+ "<p>this is another para</p>\n"
				+ "</blockquote>", null);
		Assertions.assertTrue(m.getContents().get(0) instanceof BlockQuote);
		BlockQuote bq = m.getNth(BlockQuote.class, 0).get();
		UnorderedList ul = bq.getNth(UnorderedList.class, 0).get();
		Assertions.assertEquals(3, ul.size());
	}
	
	@Test
	public void testMention() {
		String[] someEntities = new String[] {
				 "{\"type\":\"mention\",\"text\":\"<at>Rob's Echo App</at>\",\"mentioned\":{\"id\":\"abc123\",\"name\":\"Rob's Echo App\"}}",
				 "{\"type\":\"mention\",\"text\":\"<at>Suresh Rupnar</at>\",\"mentioned\":{\"id\":\"29:1et6-4yR75MhoRMsybG_kWQSm_4tXqh_WLZVQmXyY4FmpKCgpazJaZCt-uyQRe5R8M46KC4adpqgWGNmIbD2xEw\",\"name\":\"Suresh Rupnar\"}}",
				 "{\"type\":\"mention\",\"text\":\"<at>General</at>\",\"mentioned\":{\"id\":\"29:1-XAcnCve4eHbCHU4O8XHbx5Sx3cM2CxKtU4BbXmv1e99zp-4oUdX-8mP8SkToJzfkXxd4c1bQvSeAZGowOQwdv2h20lH6c-bbWTPRrdhi68\",\"name\":\"General\"}}"
		};
		List<Entity> entities = parseEntities(someEntities);
		ParseContext pc = new ParseContext(new TeamsChannel("abc123", "rando"), entities);
		
		Mockito.when(tc.getTeamsChannels(Mockito.any())).thenReturn(Arrays.asList(
				new TeamsChannel("bhhs", null)
				));
				
		Message m = parser.apply(" <div><div><span itemscope=\"\" itemtype=\"http://schema.skype.com/Mention\" itemid=\"0\">Rob's Echo App</span>&nbsp;ask <span itemscope=\"\" itemtype=\"http://schema.skype.com/Mention\" itemid=\"1\">Suresh Rupnar</span>&nbsp;for <span itemscope=\"\" itemtype=\"http://schema.skype.com/Mention\" itemid=\"2\">General</span></div></div>", pc);
		
		List<TeamsMention> mentions1 = m.only(TeamsMention.class);
		
		Assertions.assertEquals(3, mentions1.size());
		Assertions.assertEquals("Suresh Rupnar", mentions1.get(1).getName());
		Assertions.assertEquals("abc123", mentions1.get(0).getKey());
		Assertions.assertEquals("bhhs", mentions1.get(2).getKey());
		
		
	}

	protected List<Entity> parseEntities(String[] someEntities) {
		ObjectMapper om = new ObjectMapper();
		List<Entity> entities = Arrays.stream(someEntities)
			.map(s -> {
				try {
					return om.readValue(s, Entity.class);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			})
			.collect(Collectors.toList());
		return entities;
	}
	
	@Test
	@Disabled
	// this disabled for now - doesn't seem to be a way for bots to read code snippets.
	public void testCodeSnippet() {
		Message m = parser.apply("<span itemid=\"c0ac3db2bcb94831a4306d676e8679f2\" itemscope=\"\" itemtype=\"http://schema.skype.com/InputExtension\"><span itemprop=\"cardId\"></span></span>", null);
		Assertions.assertEquals(Message.of("blah"), m);
	}
}
