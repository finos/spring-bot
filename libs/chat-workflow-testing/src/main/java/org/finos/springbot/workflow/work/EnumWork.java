package org.finos.springbot.workflow.work;

import org.finos.springbot.workflow.annotations.Work;

@Work
public class EnumWork {
	
	public enum TrafficLights { RED, AMBER, GREEN }

	TrafficLights s;

	public TrafficLights getS() {
		return s;
	}

	public void setS(TrafficLights s) {
		this.s = s;
	}
	
	
}
