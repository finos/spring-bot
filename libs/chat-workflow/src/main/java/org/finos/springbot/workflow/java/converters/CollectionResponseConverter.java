package org.finos.springbot.workflow.java.converters;

import java.util.Collection;

import org.finos.springbot.workflow.java.mapping.ChatHandlerExecutor;
import org.finos.springbot.workflow.response.Response;
import org.finos.springbot.workflow.response.handlers.ResponseHandlers;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class CollectionResponseConverter extends AbstractResponseConverter implements ApplicationContextAware {

	private ApplicationContext ctx;
	
	public CollectionResponseConverter(ResponseHandlers rh) {
		super(rh);
	}

	@Override
	public int getOrder() {
		return LOWEST_PRECEDENCE;
	}

	@Override
	public void accept(Object t, ChatHandlerExecutor u) {
		if (t instanceof Collection) {
			ResponseConverters rcs = ctx.getBean(ResponseConverters.class);
			((Collection<?>) t).stream().forEach(item -> rcs.accept(item, u));
		} else if (t instanceof Response){
			rh.accept((Response) t);
		}	
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.ctx = applicationContext;
	}

}
