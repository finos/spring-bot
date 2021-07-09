package org.finos.symphony.toolkit.workflow.actions.consumers;

import java.util.Optional;

import org.finos.symphony.toolkit.workflow.actions.Action;
import org.finos.symphony.toolkit.workflow.actions.SimpleMessageAction;
import org.finos.symphony.toolkit.workflow.content.Message;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.content.Word;

/**
 * In a room, makes sure the bot is mentioned by name or the command is prefixed with /.
 * @author rob@kite9.com
 *
 */
public class InRoomAddressingChecker implements AddressingChecker {

	protected User theBot;
	protected boolean allowSlash;
	
	public InRoomAddressingChecker(User theBot, boolean allowSlash) {
		super();
		this.theBot = theBot;
		this.allowSlash = allowSlash;
	}
	
	public Action filter(Action a) {
			if (a.getAddressable() instanceof User) {
				// direct message to bot
				return a;
			}
			
			if (a instanceof SimpleMessageAction) {
				SimpleMessageAction sma = (SimpleMessageAction) a;
				
				Optional<User> firstUserMention = sma.getWords().getNth(User.class, 0); 
				
				if ((firstUserMention.isPresent()) && (theBot.matches(firstUserMention.get()))) {
					// bot is mentioned, so return the action, stripping out the bot mention
					Message changedMessage = (Message) ((SimpleMessageAction) a).getWords().removeAtStart(firstUserMention.get());
					return new SimpleMessageAction(a.getAddressable(), a.getUser(), changedMessage, sma.getData());
				}
				
				Optional<Word> firstWord = sma.getWords().getNth(Word.class, 0);
				
				if (allowSlash && firstWord.isPresent() && firstWord.get().getText().startsWith("/")) {
					// we don't actually remove the slash - words will match anyway.
					return a;
				}
				
				return null;
				
			} else {
				// forms, everything else - let them through
				return a;
			}
			
	}
	
}
