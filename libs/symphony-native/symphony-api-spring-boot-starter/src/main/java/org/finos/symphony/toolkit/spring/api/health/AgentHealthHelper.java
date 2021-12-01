package org.finos.symphony.toolkit.spring.api.health;

import java.util.Map;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.api.agent.SystemApi;
import com.symphony.api.bindings.ApiBuilder;
import com.symphony.api.model.V3Health;
import com.symphony.api.model.V3HealthStatus;


/**
 * Provides a Health Indicator for any symphony APIs being used by the application.
 * 
 * @author robmoffat
 *
 */
public class AgentHealthHelper implements HealthIndicator {

	private SystemApi api;
	private ObjectMapper om;

	public AgentHealthHelper(ApiBuilder ab) {
		this.api = ab.getApi(SystemApi.class);
		this.om = new ObjectMapper();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Health health() {
		V3Health resp = api.v3ExtendedHealth();
		Health.Builder hb = resp.getStatus() == V3HealthStatus.UP ? Health.up() : Health.down();
				
		Map<String, Object> reasons = om.convertValue(resp.getServices(), Map.class);
		hb.withDetails(reasons);

		return hb.build();

	}

}
