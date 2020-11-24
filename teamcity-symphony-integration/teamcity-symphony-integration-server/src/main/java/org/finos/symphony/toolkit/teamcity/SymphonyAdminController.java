package org.finos.symphony.toolkit.teamcity;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.finos.symphony.toolkit.spring.api.ApiInstance;
import org.finos.symphony.toolkit.spring.api.ApiInstanceFactory;
import org.finos.symphony.toolkit.spring.api.TokenManagingApiInstanceFactory;
import org.finos.symphony.toolkit.spring.api.builders.ApiBuilderFactory;
import org.finos.symphony.toolkit.spring.api.properties.IdentityProperties;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.api.agent.DatafeedApi;
import com.symphony.api.bindings.ApiBuilder;
import com.symphony.api.bindings.ConfigurableApiBuilder;
import com.symphony.api.bindings.cxf.CXFApiBuilder;
import com.symphony.api.bindings.jersey.JerseyApiBuilder;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.id.json.SymphonyIdentityModule;

import jetbrains.buildServer.controllers.BaseController;
import jetbrains.buildServer.controllers.FormUtil;
import jetbrains.buildServer.serverSide.SBuildServer;
import jetbrains.buildServer.serverSide.ServerPaths;
import jetbrains.buildServer.web.openapi.WebControllerManager;

public class SymphonyAdminController extends BaseController {

	private static final Logger log = Logger.getLogger(SymphonyAdminPage.class);
	private static final String CONTROLLER_PATH = "/saveSettings.html";
	private static final String CONFIG_PATH = "/symphony-config.json";
	private ObjectMapper om;
	private String configFile;
	private ResourceLoader rl;

	public SymphonyAdminController(@NotNull SBuildServer server, @NotNull ServerPaths serverPaths,
			@NotNull WebControllerManager manager,
			ResourceLoader rl) throws IOException {
		manager.registerController(CONTROLLER_PATH, this);
		this.om = new ObjectMapper();
		this.om.registerModule(new SymphonyIdentityModule());
		this.configFile = serverPaths.getConfigDir() + CONFIG_PATH;
		this.rl = rl;
	}

	@Override
	protected ModelAndView doHandle(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		Config config = new Config();
		FormUtil.bindFromRequest(arg0, config);
		setConfig(config);
		try {
			// this is to make sure config is ok
			DatafeedApi df = getAPI(DatafeedApi.class);
			df.createDatafeed(null, null);
			return null;
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(new StringWriter()));
			log.error("Problem handling save: ", e);
			View w = new View() {
				
				@Override
				public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response)
						throws Exception {
					response.setStatus(HttpStatus.PARTIAL_CONTENT.value());
					response.setContentType(MediaType.TEXT_PLAIN.toString());
					response.setContentLength(sw.toString().getBytes().length);
					response.getWriter().write(sw.toString());
					response.getWriter().flush();
					response.flushBuffer();
				}
				
				@Override
				public String getContentType() {
					return MediaType.TEXT_PLAIN.toString();
				}
			};
			return new ModelAndView(w);
		}
	}
	
	public Config getConfig() {
		try {
			Config c = om.readValue(new File(this.configFile), Config.class);
			packCerts(c);
			return c;
		} catch (Exception e) {
			log.error("Couldn't load symphony config: "+configFile, e);
			return new Config();
		}
	}
	
	public static Config packCerts(Config c) {
		if (StringUtils.hasText(c.getCertificates())) {
			List<String> brokenCerts = Arrays.asList(c.getCertificates().split("\n\\w*\n"));
			c.getIdentityProperties().setCertificates(brokenCerts);
			log.warn("Found certs: "+brokenCerts.size());
		}
		
		return c;
	}
	
	public void setConfig(Config c) {
		try {
			
			om.writeValue(new File(this.configFile), c);
			log.info("SYMPHONY wrote config: "+new ObjectMapper().writeValueAsString(c));
		} catch (Exception e) {
			log.error("Couldn't save symphony config: "+configFile, e);
		}
	}

	public ObjectMapper getObjectMapper() {
		return om;
	}
	

	protected <X> X getAPI(Class<X> x) throws IOException, Exception {
		Config config = getConfig();
		log.warn(getObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(config));

		SymphonyIdentity identity = IdentityProperties.instantiateIdentityFromDetails(rl,
				config.getIdentityProperties(), getObjectMapper());
		
		log.warn(identity.getCommonName());
		log.warn(identity.getPrivateKey().toString());
		
		ApiBuilderFactory abf = new ApiBuilderFactory() {

			@Override
			public boolean isSingleton() {
				return false;
			}

			@Override
			public Class<?> getObjectType() {
				return ApiBuilder.class;
			}

			@Override
			public ConfigurableApiBuilder getObject() throws Exception {
				return new CXFApiBuilder();
			}
		};
		ApiInstanceFactory apiInstanceFactory = new TokenManagingApiInstanceFactory(abf);
		ApiInstance instance = apiInstanceFactory.createApiInstance(identity, config.getPodProperties(), null);
		X out = instance.getAgentApi(x);
		return out;
	}

}
