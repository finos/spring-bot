package org.finos.springbot.workflow.java.resolvers;

import java.util.Optional;

import org.finos.springbot.workflow.annotations.Work;
import org.finos.springbot.workflow.history.AllHistory;
import org.finos.springbot.workflow.java.mapping.ChatHandlerExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageHistoryWorkflowResolverFactory implements WorkflowResolverFactory {
	
	private static final Logger LOG = LoggerFactory.getLogger(MessageHistoryWorkflowResolverFactory.class);

	
	private final class MessageHistoryWorkflowResolver extends AbstractClassWorkflowResolver {
		private final ChatHandlerExecutor che;

		private MessageHistoryWorkflowResolver(ChatHandlerExecutor che) {
			this.che = che;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Optional<Object> resolve(Class<?> t) {
			try {
				return (Optional<Object>) hist.getLastFromHistory((Class<?>) t, che.action().getAddressable());
			} catch (Exception e) {
				LOG.error("Couldn't deserialize historical object: "+t.getName(), e);
				return Optional.empty();
			}
		}

		@Override
		public boolean canResolve(Class<?> t) {
			Work w = t.getAnnotation(Work.class);
			return (w != null) && (w.index());
		}
	}

	AllHistory hist;
	
	public MessageHistoryWorkflowResolverFactory(AllHistory hist) {
		this.hist = hist;
	}

	@Override
	public int getOrder() {
		return WorkflowResolverFactory.LOW_PRIORITY;
	}

	@Override
	public WorkflowResolver createResolver(ChatHandlerExecutor che) {
		return new MessageHistoryWorkflowResolver(che);
	}

}
