package org.finos.springbot.sources.teams.handlers.adaptivecard;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.finos.springbot.workflow.templating.ElementFormat;

public class MapFormat implements ElementFormat {

	@Override
	public Function<String, String> getSourceFunction() {
		return (location) -> location+"?keys";
	}
	
	@Override
	public Function<String, String> getKeyFunction() {
		return (k) -> k;
	}

	@Override
	public BiFunction<String, String, String> getValueFunction() {
		return (k, location) -> location+"["+k+"]";
	}
	
}