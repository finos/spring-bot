package org.finos.symphony.toolkit.workflow.message;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.finos.symphony.toolkit.workflow.actions.Action;
import org.finos.symphony.toolkit.workflow.actions.ActionConsumer;
import org.finos.symphony.toolkit.workflow.actions.SimpleMessageAction;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.Word;
import org.finos.symphony.toolkit.workflow.java.mapping.AbstractHandlerExecutor;
import org.finos.symphony.toolkit.workflow.java.mapping.ChatHandlerMapping;
import org.finos.symphony.toolkit.workflow.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class MethodCallMessageConsumer implements ActionConsumer {
	
	private static final Logger LOG = LoggerFactory.getLogger(MethodCallMessageConsumer.class);

	private List<ChatHandlerMapping<?>> handlerMapping;
	private ErrorHandler errors;
	
	public MethodCallMessageConsumer(List<ChatHandlerMapping<?>> handlerMapping) {
		this.handlerMapping = handlerMapping;
	}

	@Override
	public void accept(Action t) {
		handlerMapping.stream()
				.forEach(hm -> handle(hm, t));
	}

	private void handle(ChatHandlerMapping<?> hm, Action t) {
		hm.getExecutors(t)
			.stream()
			.forEach(e -> {
				try {
					e.execute();
					
				} catch (Exception ex) {
					errors.accept(t, ex);
				}
				
				
			});
	}


}
