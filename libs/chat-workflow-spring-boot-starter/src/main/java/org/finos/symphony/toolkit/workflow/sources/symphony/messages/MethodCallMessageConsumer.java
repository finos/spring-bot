package org.finos.symphony.toolkit.workflow.sources.symphony.messages;

import java.util.List;
import java.util.Optional;

import org.finos.symphony.toolkit.workflow.CommandPerformer;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.Word;
import org.finos.symphony.toolkit.workflow.response.Response;

public class MethodCallMessageConsumer implements SimpleMessageConsumer {

	private CommandPerformer cp;
	
	public MethodCallMessageConsumer(CommandPerformer cp) {
		this.cp = cp;
	}
	
	@Override
	public List<Response> apply(SimpleMessageAction sma) {
		Addressable a = sma.getAddressable();
		Optional<Word> firstWord = sma.getWords().getNth(Word.class, 0);
		return firstWord.filter(w -> sma.getWorkflow().hasMatchingCommand(w.getIdentifier(), a))
				.map(w -> cp.applyCommand(w.getIdentifier(), sma))
				.orElse(null);
	}


}
