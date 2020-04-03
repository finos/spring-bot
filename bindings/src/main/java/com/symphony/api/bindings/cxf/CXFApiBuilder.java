package com.symphony.api.bindings.cxf;

import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.KeyManager;
import javax.ws.rs.core.Response;

import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.jaxrs.client.ClientConfiguration;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.symphony.api.agent.MessagesApi;
import com.symphony.api.authenticator.AuthenticationApi;
import com.symphony.api.bindings.AbstractApiBuilder;
import com.symphony.api.bindings.jackson.LenientJacksonJsonProvider;

/**
 * You should write your own version of this, or modify the returned webclient
 * if you want to make use of proxies, or set different characteristics on the
 * webclient.
 * 
 * The URL should be:
 * os
 * <ul>
 * <li>https://&lt;your-pod&gt;.symphony.com:443/sessionauth
 * <li>https://&lt;your agent&gt;:8444/agent
 * <li>https://&lt;your key manager&gt;:8444/keyauth
 * </ul>
 * 
 * 
 * @author Rob Moffat
 *
 */
public class CXFApiBuilder extends AbstractApiBuilder {

	public CXFApiBuilder() {
	}

	/**
	 * Call this class to create a basic xcf webclient.
	 * 
	 */
	public CXFApiBuilder(String url) {
		super(url);
	}

	/**
	 * Call this class to create a basic xcf webclient for authenticating using
	 * certificates.
	 */
	public CXFApiBuilder(String url, KeyManager[] keyManagers) {
		super(url, keyManagers);
	}

	/**
	 * Call this with an api, e.g. {@link MessagesApi}.class if you have constructed
	 * with the /agent endpoint, or {@link AuthenticationApi} if you have
	 * constructed with keyauth or sessionauth endpoints.
	 */
	@Override
	public <X> X getApi(Class<X> c) {
		WebClient wc = createWebClient();
		return buildProxy(c, wc);
	}

	/**
	 * Sets the list of providers, by default will be {@link JacksonJsonProvider}
	 * {@link ContentDispositionMultipartProvider} and {@link SymphonyExceptionMapper}.
	 * 
	 * @return
	 */
	protected List<Object> getProviders() {
		List<Object> providers = new ArrayList<>();
		providers.add(new LenientJacksonJsonProvider());
		providers.add(new ContentDispositionMultipartProvider());
		providers.add(new SymphonyExceptionMapper());
		return providers;
	}

	protected <X> X buildProxy(Class<X> c, WebClient wc) {
		X out = JAXRSClientFactory.fromClient(wc, c);

		for (int i = 0; i < wrappers.length; i++) {
			out = wrappers[i].wrap(c, out);
		}

		return out;
	}
	
	protected WebClient createWebClient(String url) {
		List<Object> providers = getProviders();
		WebClient wc = WebClient.create(url, providers);
		setProxy(wc);
		ClientConfiguration config = WebClient.getConfig(wc);
		setupClientConfiguration(config);
		return wc;
	}
	
	protected WebClient createWebClient() {
		return createWebClient(this.url);
	}

	protected void setupClientConfiguration(ClientConfiguration config) {
		HTTPConduit conduit = config.getHttpConduit();

		TLSClientParameters params = conduit.getTlsClientParameters();

		if (params == null) {
			params = new TLSClientParameters();
			conduit.setTlsClientParameters(params);
		}

		if (connectTimeout != null) {
			conduit.getClient().setConnectionTimeout(connectTimeout);
		}
		
		setupTLSParameters(params);
	}

	protected void setupTLSParameters(TLSClientParameters params) {
		params.setKeyManagers(keyManagers);

		if (trustManagers != null) {
			params.setTrustManagers(trustManagers);
		}
	}

	public void setProxy(WebClient wc) {
		HTTPConduit conduit = WebClient.getConfig(wc).getHttpConduit();
		HTTPClientPolicy policy = conduit.getClient();
		if ((this.proxyHost != null) && (this.proxyHost.length() > 0)) {
			policy.setProxyServer(proxyHost);
			policy.setProxyServerPort(port);
		}
	}

	@Override
	public boolean testConnection(String url) {
		try {
			Response response = createWebClient(url).get();
			return response != null;
		} catch (Exception e) {
			return false;
		}
	}
}
