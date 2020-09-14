package com.github.deutschebank.symphony.workflow.sources.symphony.messages;

import java.util.List;
import java.util.Optional;

import com.github.deutschebank.symphony.workflow.content.Word;
import com.github.deutschebank.symphony.workflow.response.Response;

public class MethodCallMessageConsumer implements SimpleMessageConsumer {

	@Override
	public List<Response> apply(SimpleMessageAction sma) {
		Optional<Word> firstWord = sma.getWords().getNth(Word.class, 0);
		return firstWord.filter(w -> sma.getWorkflow().getCommands(sma.getRoom()).containsKey(w.getIdentifier()))
				.map(w -> sma.getWorkflow().applyCommand(sma.getUser(), sma.getRoom(), w.getIdentifier(), null, sma.getWords()))
				.orElse(null);
	}

}
