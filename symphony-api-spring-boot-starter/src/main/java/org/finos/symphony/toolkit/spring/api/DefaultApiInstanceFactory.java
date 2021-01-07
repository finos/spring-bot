package org.finos.symphony.toolkit.spring.api;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

import javax.net.ssl.TrustManager;

import org.finos.symphony.toolkit.spring.api.builders.ApiBuilderFactory;
import org.finos.symphony.toolkit.spring.api.health.AgentHealthHelper;
import org.finos.symphony.toolkit.spring.api.properties.EndpointProperties;
import org.finos.symphony.toolkit.spring.api.properties.PodProperties;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthContributorRegistry;
import org.springframework.boot.actuate.health.HealthIndicator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.api.bindings.ApiBuilder;
import com.symphony.api.bindings.ApiWrapper;
import com.symphony.api.id.SymphonyIdentity;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Timer.Sample;

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
	
	static class HealthCheckingApiInstance extends ApiInstanceDelegate implements HealthIndicator {

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

	class MetricsApiWrapper implements ApiWrapper {
		
		private PodProperties pp;
		private SymphonyIdentity id;
		private String host;
		
		public MetricsApiWrapper(PodProperties pp, SymphonyIdentity id, String host) {
			this.pp = pp;
			this.id = id;
			this.host = host;
		}

		@SuppressWarnings("unchecked")
		@Override
		public <X> X wrap(Class<X> c, X api) {
			InvocationHandler internal = Proxy.getInvocationHandler(api);
			ClassLoader cl = this.getClass().getClassLoader();
			
			return (X) Proxy.newProxyInstance(cl, new Class[] { c }, new InvocationHandler() {
				
				@Override
				public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
					return mr == null ? internalInvoke(proxy, method, args) : timerInvoke(proxy, method, args);
				}
				
				public Object timerInvoke(Object proxy, Method method, Object[] args) throws Throwable {
					Timer t = mr.timer("symphony.api-call", "pod", pp.getId(), "id", id.getCommonName(), "method", method.getName(), "url", host);
					Sample s = Timer.start();
					Object out = internalInvoke(proxy, method, args);
					s.stop(t);
					return out;
				}
				
				public Object internalInvoke(Object proxy, Method method, Object[] args) throws Throwable {
					Object out = internal.invoke(proxy, method, args);
					return out;
				}
			});
		}

	}
	
	@Override
	protected List<ApiWrapper> buildApiWrappers(PodProperties pp, SymphonyIdentity id, EndpointProperties ep) {
		List<ApiWrapper> out = super.buildApiWrappers(pp, id, ep);
		out.add(new MetricsApiWrapper(pp, id, ep.getUrl()));
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
