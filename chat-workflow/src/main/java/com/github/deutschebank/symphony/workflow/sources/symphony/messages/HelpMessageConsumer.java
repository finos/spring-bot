package com.github.deutschebank.symphony.workflow.sources.symphony.messages;

import java.util.Collections;
import java.util.List;

import com.github.deutschebank.symphony.workflow.Workflow;
import com.github.deutschebank.symphony.workflow.Workflow.CommandDescription;
import com.github.deutschebank.symphony.workflow.content.Word;
import com.github.deutschebank.symphony.workflow.response.MessageResponse;
import com.github.deutschebank.symphony.workflow.response.Response;

/**
 * Provides help to the user on what the bot can do.
 * 
 * @author Rob Moffat
 *
 */
public class HelpMessageConsumer implements SimpleMessageConsumer {

	@Override
	public List<Response> apply(SimpleMessageAction sma) {
		return sma.getWords().getNth(Word.class, 0)
			.filter(w -> w.getIdentifier().equals("help"))
			.map(w -> {
				
				List<Workflow.CommandDescription> commands = sma.getWorkflow().getCommands(sma.getAddressable());
				String descriptions = renderDescriptions(commands);
						
				
				return Collections.singletonList((Response) new MessageResponse(sma.getWorkflow(), sma.getAddressable(), null, "Help", "This is what I can do:", descriptions));
				
			}).orElse(null);
	}

	private String renderButton(CommandDescription cd) {
		return cd.isShowButton() ? "<button name=\""+cd.getName()+"\" type=\"action\">"+cd.getName()+"</button>": "";
	}
	
	private String renderDescriptions(List<CommandDescription> commands) {
		String out = "<form id=\".\"><table><thead><tr><td>Button</td><td>Or Type...</td><td>Description</td></tr></thead><tbody>" + 
				commands.stream()
					.map(c -> "<tr><td>"+renderButton(c)+"</td><td><b> /"+ c.getName() + "</b></td><td> "+ c.getDescription()+"</td></tr>")
					.reduce("", String::concat)
				+"</tbody></table></form>";
		return out;
	}


}
