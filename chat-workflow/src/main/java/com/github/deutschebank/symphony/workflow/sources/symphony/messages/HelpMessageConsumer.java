package com.github.deutschebank.symphony.workflow.sources.symphony.messages;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
				
				Map<String, String> commands = new TreeMap<>(sma.getWorkflow().getCommands(sma.getRoom()));
				String descriptions = renderDescriptions(commands);
						
				
				return Collections.singletonList((Response) new MessageResponse(sma.getWorkflow(), sma.getRoom(), null, "Help", "This is what I can do:", descriptions));
				
			}).orElse(null);
	}

	
	private String renderDescriptions(Map<String, String> commands) {
		String out = "<form id=\".\"><table><thead><tr><td>Button</td><td>Or Type...</td><td>Description</td></tr></thead><tbody>" + 
				commands.entrySet().stream()
					.map(e -> "<tr><td><button name=\""+e.getKey()+"\" type=\"action\">"+e.getKey()+"</button></td><td><b> /"+ e.getKey() + "</b></td><td> "+ e.getValue()+"</td></tr>")
					.reduce("", String::concat)
				+"</tbody></table></form>";
		System.out.println(out);
		return out;
	}


}
