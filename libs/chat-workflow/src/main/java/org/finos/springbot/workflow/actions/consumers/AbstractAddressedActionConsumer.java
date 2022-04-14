package org.finos.springbot.workflow.actions.consumers;

import java.util.List;

import org.finos.springbot.workflow.actions.Action;
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
	
	List<AddressingChecker> ac;

	public AbstractAddressedActionConsumer(ErrorHandler errorHandler, List<AddressingChecker> ac) {
		super(errorHandler);
		this.ac = ac;
	}

	@Override
	public void accept(Action t) {
		if (ac != null) {
			Action f = performFilters(t);
			if (f != null) {
				acceptInner(f);
			}
		} else {
			acceptInner(t);
		}
	}

	protected Action performFilters(Action in) {
		return addressCheckingFilters(in);
	}
	
	/**
	 * Makes sure at least one address checking filter permits the action
	 */
	protected Action addressCheckingFilters(Action in) {
		for (AddressingChecker addressingChecker : ac) {
			Action out = addressingChecker.filter(in);
			if (out != null) {
				return out;
			}
		}
		
		return null;

	}

	protected abstract void acceptInner(Action t);

	
}

