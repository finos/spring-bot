package org.finos.springbot.sources.teams.handlers;

public interface WorkConverter<MODE, O> {
	
	O convert(Class<?> c, Mode m);

}