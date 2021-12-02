package org.finos.springbot.workflow.java.mapping;

import java.util.List;

import org.finos.springbot.workflow.actions.Action;
import org.finos.springbot.workflow.actions.consumers.AbstractAddressedActionConsumer;
import org.finos.springbot.workflow.actions.consumers.ActionConsumer;
import org.finos.springbot.workflow.actions.consumers.AddressingChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ErrorHandler;

public class ChatHandlerMappingActionConsumer extends AbstractAddressedActionConsumer implements ActionConsumer {

	private static final Logger LOG = LoggerFactory.getLogger(ChatHandlerMappingActionConsumer.class);

	private List<ChatHandlerMapping<?>> handlerMapping;

	public ChatHandlerMappingActionConsumer(List<ChatHandlerMapping<?>> handlerMapping, ErrorHandler eh, List<AddressingChecker> ac) {
		super(eh, ac);
		this.handlerMapping = handlerMapping;
	}

	@Override
	public void acceptInner(Action t) {
		handlerMapping.stream().forEach(hm -> handle(hm, t));
	}

	private void handle(ChatHandlerMapping<?> hm, Action t) {
		hm.getExecutors(t).stream().forEach(e -> {
			try {
				e.execute();

			} catch (Throwable ex) {
				LOG.error("Couldn't process {}, error {}", t, ex.getLocalizedMessage());
				errorHandler.handleError(ex);
			}

		});
	}

}
