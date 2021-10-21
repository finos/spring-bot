package org.finos.springbot.teams.turns;

import com.microsoft.bot.builder.TurnContext;

public class CurrentTurnContext {
	
	public static final ThreadLocal<TurnContext> CURRENT_CONTEXT = new ThreadLocal<TurnContext>();

}
