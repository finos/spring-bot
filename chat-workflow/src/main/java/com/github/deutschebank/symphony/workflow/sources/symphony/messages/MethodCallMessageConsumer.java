package com.github.deutschebank.symphony.workflow.sources.symphony.messages;

import java.util.List;
import java.util.Optional;

import com.github.deutschebank.symphony.workflow.CommandPerformer;
import com.github.deutschebank.symphony.workflow.content.Addressable;
import com.github.deutschebank.symphony.workflow.content.Word;
import com.github.deutschebank.symphony.workflow.response.Response;

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
