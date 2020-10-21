package com.github.deutschebank.symphony.workflow;

import java.util.List;

import com.github.deutschebank.symphony.workflow.response.Response;

/** 
 * The command performer is responsible for taking the user action and converting it into a set of responses.
 *
 * @author moffrob
 *
 */
public interface CommandPerformer {

	public List<Response> applyCommand(String commandName, Action originatingAction);
	
}
