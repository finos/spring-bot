package org.finos.springbot.teams.templating.thymeleaf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.finos.springbot.teams.response.templating.MarkupAndEntities;
import org.finos.springbot.workflow.form.ButtonList;
import org.finos.springbot.workflow.response.Response;
import org.finos.springbot.workflow.response.WorkResponse;
import org.finos.springbot.workflow.response.templating.AbstractResourceTemplateProvider;
import org.finos.springbot.workflow.templating.Mode;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StreamUtils;
import org.thymeleaf.TemplateSpec;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import com.microsoft.bot.schema.ChannelAccount;
import com.microsoft.bot.schema.Entity;
import com.microsoft.bot.schema.Mention;

public class ThymeleafTemplateProvider extends AbstractResourceTemplateProvider<MarkupAndEntities, String, WorkResponse> {

	private final ThymeleafTemplater converter;
	private final SpringTemplateEngine templateEngine;
	
	public ThymeleafTemplateProvider(
			String templatePrefix, 
			String templateSuffix, 
			ResourceLoader rl, 
			ThymeleafTemplater converter
		) {
		super(templatePrefix, templateSuffix, rl);
		this.converter = converter;
		this.templateEngine = new SpringTemplateEngine();
		this.templateEngine.setTemplateResolver(new StringTemplateResolver());
	}

	@Override
	protected String getDefaultTemplate(WorkResponse r) {
		String insert;
		if (WorkResponse.DEFAULT_FORM_TEMPLATE_EDIT.equals(r.getTemplateName())) {
			Class<?> c = ((WorkResponse) r).getFormClass();
			insert = converter.convert(c, Mode.FORM);
		} else if (WorkResponse.DEFAULT_FORM_TEMPLATE_VIEW.equals(r.getTemplateName())) {
			Class<?> c = ((WorkResponse) r).getFormClass();
			boolean needsButtons = needsButtons(r);						
			insert = converter.convert(c, needsButtons ? Mode.DISPLAY_WITH_BUTTONS : Mode.DISPLAY);
		} else {
			throw new UnsupportedOperationException("Don't know how to construct default template for "+r);
		}
		
		String defaultTemplate = getTemplateForName("default");
		String replacedText = defaultTemplate.replace("<!-- Message Content -->", insert);
		return replacedText;
	}
	
	public static boolean needsButtons(Response r) {
		if (r instanceof WorkResponse) {
			ButtonList bl = (ButtonList) ((WorkResponse) r).getData().get(ButtonList.KEY);
			return (bl != null) && (bl.getContents().size() > 0);
		} else {
			return false;
		}
	}

	@Override
	protected String deserializeTemplate(InputStream is) throws IOException {
		String template = StreamUtils.copyToString(is, StandardCharsets.UTF_8);
		return template;
	}

	public static final Pattern ENTITY_FINDER = Pattern.compile("\\<at\\ key=\\\"(.*?)\"\\>(.*?)<\\/at\\>");
	
	@Override
	public MarkupAndEntities applyTemplate(String template, WorkResponse t) {
		// do thymeleaf rendering here
		Context ctx = new Context();
		for (String key : t.getData().keySet()) {
			ctx.setVariable(key, t.getData().get(key));
		}
		String done = templateEngine.process(new TemplateSpec(template, TemplateMode.XML), ctx);
		
		// figure out the entities.
		Matcher m = ENTITY_FINDER.matcher(done);
		List<Entity> entities = new ArrayList<Entity>();
		
		done = replaceAll(done, m, x -> {
			Mention men = new Mention();
			men.setMentioned(new ChannelAccount(x.group(1), x.group(2)));
			Entity out = new Entity();
			out.setAs(men);
			out.setType("mention");
			entities.add(out);	
			return "<at>"+x.group(2)+"</at>";
		});
		
		return new MarkupAndEntities(done, entities);
	}
	
	/**
	 * Added this here since it's only available since Java1.9 in Matcher.
	 */
	public String replaceAll(String in, Matcher m, Function<MatchResult, String> replacer) {
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
