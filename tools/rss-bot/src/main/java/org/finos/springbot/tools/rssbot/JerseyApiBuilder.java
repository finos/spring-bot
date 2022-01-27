package org.finos.springbot.tools.rssbot;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.apache.connector.ApacheConnectorProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.springframework.http.client.reactive.ReactorResourceFactory;

/**
 * Provides a basic Jersey client creation wrapper which includes:
 * <ul>
 *  <li>Setting up ssl context
 *  <li>Setting proxy details (basic, override if needed)
 *  <li>Using the {@link MultipartWebResourceFactory}
 * </ul>
 * @author Rob Moffat
 *
 */
public class JerseyApiBuilder extends AbstractApiBuilder {
	
	public JerseyApiBuilder() {
	}

	/**
	 * Call this class to create basic jersey-backed apis.  
	 * 
	 */
	public JerseyApiBuilder(String url) {
		super(url);
	}

	/**
	 * Call this class to create a basic jersey client for authenticating using certificates.
	 */
	public JerseyApiBuilder(String url, KeyManager[] keyManagers) {
		super(url, keyManagers);
	}

	@Override
	public <X> X getApi(Class<X> c) {
		WebTarget wt = newWebTarget();
		return buildProxy(c, wt);
	}
	
	protected WebTarget newWebTarget(String url) {
		try {
			JerseyClientBuilder jcb = new JerseyClientBuilder();
			jcb.sslContext(createSSLContext());
		    jcb = jcb.withConfig(createConfig());
			registerFeatures(jcb);
			Client client = jcb.build();
			WebTarget webTarget = client.target(url);
			return webTarget;
		} catch (Exception e) {
			throw new UnsupportedOperationException("Couldn't create jersey client", e);
		}
	}
	
	protected WebTarget newWebTarget() {
		return newWebTarget(this.url);
	}

	protected void registerFeatures(JerseyClientBuilder jcb) {
		jcb.register(MultiPartFeature.class);
		jcb.register(LenientJacksonJsonProvider.class);
	}

	protected SSLContext createSSLContext() throws NoSuchAlgorithmException, KeyManagementException {
		SSLContext ctx = SSLContext.getInstance("SSL");
		ctx.init(keyManagers, trustManagers, null);
		return ctx;
	}

	protected ClientConfig createConfig() {
		ClientConfig config = new ClientConfig();
		if ((this.proxyHost!= null) && (this.proxyHost.length() > 0)) {
				config
				.connectorProvider(new ApacheConnectorProvider())
		    	.property(ClientProperties.PROXY_URI, "http://"+proxyHost+":"+port);
			    config.property(ClientProperties.PROXY_USERNAME,user);
				config.property(ClientProperties.PROXY_PASSWORD,password);
		}
		
		if (connectTimeout != null) {
			config.property(ClientProperties.CONNECT_TIMEOUT, connectTimeout.intValue());
		}
		
		return config;
	}

	protected <X> X buildProxy(Class<X> c, WebTarget wt) {
		X out = WebResourceFactory.newResource(c, wt);
		return out;
	}

	@Override
	public boolean testConnection(String url) {
		try {
			Response response = newWebTarget(url).request().get();
			return response != null;	// any response from the server means we at least connected.
		} catch (Exception e) {
			return false;
		}
	}
}
