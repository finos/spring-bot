package org.finos.springbot.teams.content;

import java.util.Map;
import java.util.function.BiFunction;

import org.finos.springbot.teams.response.templating.MarkupAndEntities;
import org.finos.springbot.workflow.content.Content;
import org.finos.springbot.workflow.content.serialization.MarkupWriter;
import org.springframework.web.util.HtmlUtils;

import com.microsoft.bot.schema.ChannelAccount;
import com.microsoft.bot.schema.Entity;
import com.microsoft.bot.schema.Mention;

public class TeamsMarkupWriter extends MarkupWriter<MarkupAndEntities> {

	public TeamsMarkupWriter() {
		super();
	}

	public TeamsMarkupWriter(Map<Class<? extends Content>, BiFunction<Content, MarkupAndEntities, String>> tagMap) {
		super(tagMap);

	}

	protected Entity createEntity(Content t) {
		Mention m = new Mention();
		TeamsMention tm = (TeamsMention) t;
		m.setText("<at>"+HtmlUtils.htmlEscape(tm.getName())+"</at>");
		m.setMentioned(new ChannelAccount(tm.getKey(), tm.getName()));
		Entity out = new Entity();
		out.setAs(m);
		out.setType("mention");
		return out;
	}

	public class EntityTagWriter extends SimpleTagWriter {
		
		public EntityTagWriter(String tag) {
			super(tag);
		}

		@Override
		protected String getContainedMarkup(Content t) {
			return ((TeamsMention)t).getName();
		}

		@Override
		public String apply(Content t, MarkupAndEntities c) {
			c.getEntities().add(createEntity(t));
			return super.apply(t, c);
		}
	}

	
}

