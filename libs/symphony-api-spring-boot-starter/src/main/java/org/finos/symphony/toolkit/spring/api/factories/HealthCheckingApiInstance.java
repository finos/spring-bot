package org.finos.symphony.toolkit.spring.api.factories;

import org.finos.symphony.toolkit.spring.api.health.AgentHealthHelper;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

class HealthCheckingApiInstance extends ApiInstanceDelegate implements HealthIndicator {

	private AgentHealthHelper agentHealth;
	
	public HealthCheckingApiInstance(ApiInstance delegate, AgentHealthHelper agentHealth) {
		super(delegate);
		this.agentHealth = agentHealth;
	}

	@Override
	public Health health() {
		return agentHealth.health();
	}
	
	
}