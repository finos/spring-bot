package org.finos.symphony.toolkit.spring.api.factories;

import java.util.List;

import javax.net.ssl.TrustManager;

import org.finos.symphony.toolkit.spring.api.builders.ApiBuilderFactory;
import org.finos.symphony.toolkit.spring.api.health.AgentHealthHelper;
import org.finos.symphony.toolkit.spring.api.properties.EndpointProperties;
import org.finos.symphony.toolkit.spring.api.properties.PodProperties;
import org.springframework.boot.actuate.health.HealthContributorRegistry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.api.bindings.ApiBuilder;
import com.symphony.api.bindings.ApiWrapper;
import com.symphony.api.id.SymphonyIdentity;

import io.micrometer.core.instrument.MeterRegistry;

/**
 * Creates ApiInstances with health endpoint and metrics, if a {@link HealthIndicatorRegistry} is defined.
 * 
 * @author Rob Moffat
 */
public class DefaultApiInstanceFactory extends TokenManagingApiInstanceFactory {

	protected HealthContributorRegistry registry;
	protected MeterRegistry mr;
	protected ObjectMapper om;

	public DefaultApiInstanceFactory(ApiBuilderFactory apiBuilderFactory,  HealthContributorRegistry registry, MeterRegistry meter, ObjectMapper om) {
		super(apiBuilderFactory);
		this.registry = registry;
		this.mr = meter;
		this.om = om;
	}
	
	@Override
	protected List<ApiWrapper> buildApiWrappers(PodProperties pp, SymphonyIdentity id, EndpointProperties ep) {
		List<ApiWrapper> out = super.buildApiWrappers(pp, id, ep);
		out.add(new MetricsApiWrapper(mr, pp, id.getCommonName(), ep.getUrl()));
		return out;
	}
	
	@Override
	public ApiInstance createApiInstance(SymphonyIdentity id, PodProperties pp, TrustManager[] trustManagers) throws Exception {
		ApiInstance parent = super.createApiInstance(id, pp, trustManagers);
		ApiBuilder agentApiBuilder = ((BasicAPIInstance)  parent).agentApiBuilder;
		AgentHealthHelper agentHealth = new AgentHealthHelper(agentApiBuilder, om);
		HealthCheckingApiInstance out = new HealthCheckingApiInstance(parent, agentHealth);
		
		if (registry != null) { 
			String healthIndicatorName = "symphony-api-"+id.getCommonName()+"-"+pp.getId();
			if (registry.getContributor(healthIndicatorName) == null) {
				registry.registerContributor(healthIndicatorName, out);
			}
		}
		
		return out;
	}
}
