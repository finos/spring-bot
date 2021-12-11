package org.finos.symphony.toolkit.workflow.actions.consumers;

import org.finos.symphony.toolkit.workflow.actions.Action;
import org.springframework.util.ErrorHandler;

/**
 * An action consumer where the message has to be addressed to the bot.
 * 
 * To address the bot, you must either prefix with the bot's name
 * 
 * @author rob@kite9.com
 *
 */
public abstract class AbstractAddressedActionConsumer extends AbstractActionConsumer {
	
	AddressingChecker ac;

	public AbstractAddressedActionConsumer(ErrorHandler errorHandler, AddressingChecker ac) {
		super(errorHandler);
		this.ac = ac;
	}

	@Override
	public void accept(Action t) {
		if (ac != null) {
			Action f = ac.filter(t);
			if (f != null) {
				acceptInner(f);
			}
		} else {
			acceptInner(t);
		}
	}

	protected abstract void acceptInner(Action t);

	
}

