package org.finos.symphony.toolkit.spring.app;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.net.ssl.TrustManager;

import org.finos.symphony.toolkit.spring.api.SymphonyApiConfig;
import org.finos.symphony.toolkit.spring.api.SymphonyApiTrustManagersConfig;
import org.finos.symphony.toolkit.spring.api.builders.ApiBuilderFactory;
import org.finos.symphony.toolkit.spring.api.properties.SymphonyApiProperties;
import org.finos.symphony.toolkit.spring.app.auth.AppAuthController;
import org.finos.symphony.toolkit.spring.app.auth.PodAuthController;
import org.finos.symphony.toolkit.spring.app.bundle.MarketBundleController;
import org.finos.symphony.toolkit.spring.app.bundle.UrlParamBundleController;
import org.finos.symphony.toolkit.spring.app.config.SymphonyAppResourceServerConfigurer;
import org.finos.symphony.toolkit.spring.app.config.SymphonyAppWebMvcConfigurer;
import org.finos.symphony.toolkit.spring.app.config.SymphonyAppWebSecurityConfigurer;
import org.finos.symphony.toolkit.spring.app.controller.ControllerPageController;
import org.finos.symphony.toolkit.spring.app.controller.ThymeleafPageController;
import org.finos.symphony.toolkit.spring.app.id.GeneratingAppIdentityProvider;
import org.finos.symphony.toolkit.spring.app.jwt.UserDetailsController;
import org.finos.symphony.toolkit.spring.app.pods.info.DirectoryBasedPodInfoStore;
import org.finos.symphony.toolkit.spring.app.pods.info.NoopPodInfoStore;
import org.finos.symphony.toolkit.spring.app.pods.info.PodInfo;
import org.finos.symphony.toolkit.spring.app.pods.info.PodInfoController;
import org.finos.symphony.toolkit.spring.app.pods.info.PodInfoStore;
import org.finos.symphony.toolkit.spring.app.tokens.app.AppTokenStrategy;
import org.finos.symphony.toolkit.spring.app.tokens.app.NoopAppTokenStrategy;
import org.finos.symphony.toolkit.spring.app.tokens.pod.ConfiguredPodTokenStrategy;
import org.finos.symphony.toolkit.spring.app.tokens.pod.PodInfoStoreTokenStrategy;
import org.finos.symphony.toolkit.spring.app.tokens.pod.PodTokenStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.id.json.SymphonyIdentityModule;

@Configuration
@EnableConfigurationProperties(SymphonyAppProperties.class)
@AutoConfigureAfter({WebMvcAutoConfiguration.EnableWebMvcConfiguration.class})
@Import({SymphonyAppResourceServerConfigurer.class, SymphonyAppWebMvcConfigurer.class, SymphonyAppWebSecurityConfigurer.class})
public class SymphonyAppConfig  {
	
	public static final String APP_IDENTITY_BEAN = "appIdentity";

	@Bean
	@ConfigurationProperties("symphony.app")
	public SymphonyAppProperties appProperties() {
		return new SymphonyAppProperties();
	};
	
	@Autowired
	private SymphonyApiProperties apiProperties;
	
	@Autowired
	private ResourceLoader loader;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private ApiBuilderFactory apiBuilderFactory;
	
	@Qualifier(SymphonyApiTrustManagersConfig.SYMPHONY_TRUST_MANAGERS_BEAN)
	@Autowired(required=false)
	private TrustManager[] trustManagers;
	
	@Bean(name=APP_IDENTITY_BEAN)
	@ConditionalOnMissingBean(name=APP_IDENTITY_BEAN)
	public SymphonyIdentity appIdentity() throws Exception {
		GeneratingAppIdentityProvider provider = new GeneratingAppIdentityProvider(appProperties(), loader, objectMapper);
		SymphonyIdentity out = provider.getIdentity();
		return out;
	}
	
	private View symphonyJsonOutputView(boolean singleKeyExtract) {
		MappingJackson2JsonView out = new MappingJackson2JsonView(objectMapper);
		out.setPrettyPrint(true);
		out.setExtractValueFromSingleKeyModel(singleKeyExtract);
		return out;
	}

	@Bean
	@ConditionalOnMissingBean
	public MarketBundleController marketBundleController(@Qualifier(APP_IDENTITY_BEAN) SymphonyIdentity appId) {
		return new MarketBundleController(appProperties(), appId, symphonyJsonOutputView(false));
	}
	
	@Bean
	@ConditionalOnMissingBean
	public UrlParamBundleController urlParamBundleController(@Qualifier(APP_IDENTITY_BEAN) SymphonyIdentity appId) {
		return new UrlParamBundleController(appProperties(), symphonyJsonOutputView(false), appId);
	}
		
	@Bean
	@ConditionalOnMissingBean
	public PodAuthController podAuthController(@Qualifier(APP_IDENTITY_BEAN) SymphonyIdentity appId, List<PodTokenStrategy> strategies, AppTokenStrategy appTokenStrategy) {
		return new PodAuthController(appProperties(), symphonyJsonOutputView(false), appId, appTokenStrategy, strategies);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public AppAuthController appAuthController(@Qualifier(APP_IDENTITY_BEAN) SymphonyIdentity appId, AppTokenStrategy appTokenStrategy) {
		return new AppAuthController(appProperties(), symphonyJsonOutputView(false), appId, appTokenStrategy);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public PodInfoController podInfoController(@Qualifier(APP_IDENTITY_BEAN) SymphonyIdentity appId, PodInfoStore store) {
		return new PodInfoController(appProperties(), symphonyJsonOutputView(false), appId, store, objectMapper);
	}

	@Bean
	@ConditionalOnMissingBean
	public UserDetailsController userDetailsController(@Qualifier(APP_IDENTITY_BEAN) SymphonyIdentity appId, PodInfoStore store) {
		return new UserDetailsController(appProperties(), symphonyJsonOutputView(false), appId, objectMapper);
	}
	
	@Bean
	@ConditionalOnMissingBean
	public ConfiguredPodTokenStrategy configuredPodTokenStrategy(@Qualifier(APP_IDENTITY_BEAN) SymphonyIdentity appId) {
		return new ConfiguredPodTokenStrategy(apiProperties, appId, apiBuilderFactory, trustManagers);
	}

	@Bean
	@ConditionalOnMissingBean
	public PodInfoStoreTokenStrategy podInfoStoreTokenStrategy(@Qualifier(APP_IDENTITY_BEAN) SymphonyIdentity appId, PodInfoStore store) {
		return new PodInfoStoreTokenStrategy(appProperties(), appId, apiBuilderFactory, trustManagers, store);
	}
	
	/**
	 * This is a 'null' implementation which doesn't actually check that the tokens match.  Users are expected to provide their 
	 * own implementations of this bean which store the tokens and check them.  This will depend on factors like sticky sessions, 
	 * backing stores etc.
	 */
	@Bean
	@ConditionalOnMissingBean
	public AppTokenStrategy noopAppTokenStrategy(@Qualifier(APP_IDENTITY_BEAN) SymphonyIdentity appId) {
		return new NoopAppTokenStrategy(appId);
	}
	
	
	@Bean
	@ConditionalOnProperty("symphony.app.store.location")
	public PodInfoStore directoryBasedPodInfoStore(@Value("${symphony.app.store.location}") String location) throws IOException {
		File f = new File(location);
		return new DirectoryBasedPodInfoStore(f, objectMapper);
	}
	
	/**
	 * Override this bean by setting up a {@link DirectoryBasedPodInfoStore} bean instead (or something else)
	 * @return a pod store that does nothing (doesn't record {@link PodInfo} or return it.
	 */
	@Bean
	@ConditionalOnMissingBean
	public PodInfoStore noopPodStore() {
		return new NoopPodInfoStore(objectMapper);
	}
	
	/**
	 * Provides a test page for the user so at least they know their app works (html part)
	 * @return the default app page.
	 */
	@Bean(name="appPageHtml")
	@ConditionalOnMissingBean(name="appPageHtml")
	public ThymeleafPageController starterPageController() {
		return new ThymeleafPageController(appProperties(), appProperties().getAppPath()+"/starter-app-page.html", "symphony-app/starter-app-page.html", Collections.emptyMap());
	}
	
	/**
	 * Provides a test page for the user so at least they know their app works (html part)
	 * @return controller javascript include
	 */
	@Bean(name="appControllerJs")
	@ConditionalOnMissingBean(name="appControllerJs")
	public ThymeleafPageController controllerPageJs() {
		return new ThymeleafPageController(appProperties(), appProperties().getAppPath()+"/starter-include.js", "symphony-app/starter-include.js", Collections.emptyMap());
	}
	
	@Bean
	@ConditionalOnMissingBean
	public ControllerPageController controllerPageController() {
		return new ControllerPageController(appProperties());
	}
	
	public static class SymphonyAppUrlMapping extends SimpleUrlHandlerMapping {}
	
	@Bean
	@ConditionalOnMissingBean
	public SymphonyAppUrlMapping symphonyAppMapping(List<SymphonyController> controllers) {
		SymphonyAppUrlMapping out = new SymphonyAppUrlMapping();
		out.setOrder(1);
		Map<String, Object> map = controllers.stream().collect(Collectors.toMap(e -> e.getPath(), e -> e));
		out.setUrlMap(map);
		return out;
	}
	
	
	@Bean
	public Module symphonyIdentityModule() {
		return new SymphonyIdentityModule();
	}
}
