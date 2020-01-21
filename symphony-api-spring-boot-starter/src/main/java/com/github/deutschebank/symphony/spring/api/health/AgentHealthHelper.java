package com.github.deutschebank.symphony.spring.api.health;

import java.util.Map;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.api.agent.SystemApi;
import com.symphony.api.bindings.ApiBuilder;
import com.symphony.api.model.V2HealthCheckResponse;


/**
 * Provides a Health Indicator for any symphony APIs being used by the application.
 * 
 * @author robmoffat
 *
 */
public class AgentHealthHelper implements HealthIndicator {

	private SystemApi api;
	private ObjectMapper om;

	public AgentHealthHelper(ApiBuilder ab, ObjectMapper om) {
		this.api = ab.getApi(SystemApi.class);
		this.om = om;
	}

	@Override
	public Health health() {
		V2HealthCheckResponse resp = api.v2HealthCheckGet(false, null, null);
		Health.Builder hb = (resp.isisAgentServiceUser() && resp.isisEncryptDecryptSuccess()
				&& resp.isisKeyManagerConnectivity() && resp.isisPodConnectivity()) ? Health.up() : Health.down();
				
		Map<String, Object> reasons = om.convertValue(resp, Map.class);
		hb.withDetails(reasons);

		return hb.build();

	}

}
