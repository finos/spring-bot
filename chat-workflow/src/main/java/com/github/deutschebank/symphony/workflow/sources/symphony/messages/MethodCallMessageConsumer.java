package com.github.deutschebank.symphony.workflow.sources.symphony.messages;

import java.util.List;
import java.util.Optional;

import com.github.deutschebank.symphony.workflow.content.Addressable;
import com.github.deutschebank.symphony.workflow.content.Word;
import com.github.deutschebank.symphony.workflow.response.Response;

public class MethodCallMessageConsumer implements SimpleMessageConsumer {

	@Override
	public List<Response> apply(SimpleMessageAction sma) {
		Addressable a = sma.getAddressable();
		Optional<Word> firstWord = sma.getWords().getNth(Word.class, 0);
		return firstWord.filter(w -> sma.getWorkflow().hasMatchingCommand(w.getIdentifier(), a))
				.map(w -> sma.getWorkflow().applyCommand(sma.getUser(), a, w.getIdentifier(), null, sma.getWords()))
				.orElse(null);
	}


}
