package org.finos.symphony.toolkit.spring.api;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.net.ssl.TrustManager;

import org.finos.symphony.toolkit.spring.api.builders.ApiBuilderFactory;
import org.finos.symphony.toolkit.spring.api.health.AgentHealthHelper;
import org.finos.symphony.toolkit.spring.api.properties.EndpointProperties;
import org.finos.symphony.toolkit.spring.api.properties.PodProperties;
import org.finos.symphony.toolkit.spring.api.properties.PodProperties.AuthMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.HealthIndicatorRegistry;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.api.authenticator.AuthenticationApi;
import com.symphony.api.bindings.ApiBuilder;
import com.symphony.api.bindings.ApiWrapper;
import com.symphony.api.bindings.ConfigurableApiBuilder;
import com.symphony.api.bindings.JWTHelper;
import com.symphony.api.bindings.StreamIDHelp;
import com.symphony.api.bindings.TokenManager;
import com.symphony.api.id.IdentityConfigurationException;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.model.AuthenticateRequest;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Timer.Sample;

/**
 * Creates ApiInstances, and also manages adding them to the health endpoint, if a {@link HealthIndicatorRegistry} is defined.
 * 
 * @author Rob Moffat
 */
public class DefaultApiInstanceFactory implements ApiInstanceFactory {

	private static final Logger LOG = LoggerFactory.getLogger(DefaultApiInstanceFactory.class); 

	protected ApiBuilderFactory apiBuilderFactory;
	protected HealthIndicatorRegistry registry;
	protected ObjectMapper om;
	protected MeterRegistry mr;

	public DefaultApiInstanceFactory(ApiBuilderFactory apiBuilderFactory,  HealthIndicatorRegistry registry, MeterRegistry meter, ObjectMapper om) {
		super();
		this.apiBuilderFactory = apiBuilderFactory;
		this.registry = registry;
		this.om = om;
		this.mr = meter;
	}
	
	static interface HealthCheckingApiInstance extends ApiInstance, HealthIndicator {}

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
	public ApiInstance createApiInstance(SymphonyIdentity id, PodProperties pp, TrustManager[] trustManagers) throws Exception {
		ApiBuilder sessionAuthApiBuilder = createApiBuilder(pp, pp.getSessionAuth(), id, trustManagers, "sessionAuth");
		ApiBuilder keyAuthApiBuilder = createApiBuilder(pp, pp.getKeyAuth(), id, trustManagers, "keyAuth");
		ApiBuilder relayApiBuilder = createApiBuilder(pp, pp.getRelay(), id, trustManagers, "relay");
		ApiBuilder loginApiBuilder = createApiBuilder(pp, pp.getLogin(), id, trustManagers, "login");
		
		TokenManager tm = createTokenManager(id, pp, sessionAuthApiBuilder, keyAuthApiBuilder, relayApiBuilder, loginApiBuilder);

		ApiBuilder podApiBuilder = createApiBuilder(pp, pp.getPod(), tm, id, trustManagers);
		ApiBuilder agentApiBuilder = createApiBuilder(pp, pp.getAgent(), tm, id, trustManagers);
		
		AgentHealthHelper agentHealth = new AgentHealthHelper(agentApiBuilder, om);
		
		HealthCheckingApiInstance out = new HealthCheckingApiInstance() {
			
			@Override
			public Health health() {
				return agentHealth.health();
			}
	
			@Override
			public <X> X getPodApi(Class<X> c) {
				return podApiBuilder.getApi(c);
			}

			@Override
			public <X> X getAgentApi(Class<X> c) {
				return agentApiBuilder.getApi(c);
			}

			@Override
			public <X> X getSessionAuthApi(Class<X> c) {
				return sessionAuthApiBuilder.getApi(c);
			}

			@Override
			public <X> X getKeyAuthApi(Class<X> c) {
				return keyAuthApiBuilder.getApi(c);
			}

			@Override
			public <X> X getRelayApi(Class<X> c) {
				return relayApiBuilder.getApi(c);
			}

			@Override
			public <X> X getLoginApi(Class<X> c) {
				return loginApiBuilder.getApi(c);
			}
		};
		
		if (registry != null) { 
			String healthIndicatorName = "symphony-api-"+id.getCommonName()+"-"+pp.getId();
			if (registry.get(healthIndicatorName) == null) {
				registry.register(healthIndicatorName, out);
			}
		}
		
		return out;
	}

	protected TokenManager createTokenManager(SymphonyIdentity id, PodProperties pp, 
			ApiBuilder sessionAuth, ApiBuilder keyAuth, ApiBuilder relay, ApiBuilder login)  {
		TokenManager tm;
		if (usingCertificates(id, pp)) {
			tm = new TokenManager(() -> sessionAuth.getApi(AuthenticationApi.class).v1AuthenticatePost(),
					() -> keyAuth.getApi(AuthenticationApi.class).v1AuthenticatePost());
		} else {
			tm = new TokenManager(
					() -> login.getApi(com.symphony.api.login.AuthenticationApi.class)
							.pubkeyAuthenticatePost(createAuthenticateRequest(id)),
					() -> relay.getApi(com.symphony.api.login.AuthenticationApi.class)
							.pubkeyAuthenticatePost(createAuthenticateRequest(id)));
		}
		return tm;
	}

	private boolean usingCertificates(SymphonyIdentity botIdentity, PodProperties pp) {
		if (pp.getAuthMethod() == AuthMethod.CERT) {
			LOG.info("Bot "+botIdentity.getCommonName()+" authentication = CERT");
			return true;
		}
		
		if (pp.getAuthMethod() == AuthMethod.RSA) {
			LOG.info("Bot "+botIdentity.getCommonName()+" authentication = RSA");
			return false;
		}
		
		boolean hasCerts = (botIdentity.getCertificateChain() != null) && (botIdentity.getCertificateChain().length > 0);
		LOG.info("Bot "+botIdentity.getCommonName()+" using certs? {} ", hasCerts);

		return hasCerts;
	}

	/**
	 * Override this method to configure your own ApiBuilder implementation.
	 */
	protected ConfigurableApiBuilder createApiBuilder(PodProperties pp, EndpointProperties ep, TokenManager tm, SymphonyIdentity id,
			TrustManager[] trustManagers) throws Exception {
		ConfigurableApiBuilder ab = apiBuilderFactory.getObject();
		ApiWrapper[] wrappers = { tm, new StreamIDHelp(), new MetricsApiWrapper(pp, id, ep.getUrl()) };
		ep.configure(ab, wrappers, trustManagers);
		return ab;
	}

	/**
	 * Override this method to configure your own ApiBuilder implementation.
	 */
	protected ConfigurableApiBuilder createApiBuilder(PodProperties pp, EndpointProperties ep, SymphonyIdentity id, TrustManager[] trustManagers, String apiName) throws Exception {
		if (ep == null) {
			LOG.warn("symphony.apis[{}].{} not set: could cause NPE when doing get{}Api()", 
					pp.getId(), apiName.toLowerCase(), StringUtils.capitalize(apiName));
			return null;
		}
		
		ApiWrapper[] wrappers = { new StreamIDHelp(), new MetricsApiWrapper(pp, id, ep.getUrl()) };
		ConfigurableApiBuilder ab = apiBuilderFactory.getObject();
		ep.configure(ab, wrappers, id, trustManagers);
		return ab;
	}

	private AuthenticateRequest createAuthenticateRequest(SymphonyIdentity botIdentity) {
		try {
			return new AuthenticateRequest()
					.token(JWTHelper.createSignedJwt(botIdentity.getCommonName(), botIdentity.getPrivateKey()));
		} catch (Exception e) {
			throw new IdentityConfigurationException("Couldn't create AuthenticationRequest", e);
		}
	}
}
