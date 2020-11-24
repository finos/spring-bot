package org.finos.symphony.toolkit.teamcity;

import java.util.Arrays;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import jetbrains.buildServer.controllers.admin.AdminPage;
import jetbrains.buildServer.serverSide.auth.Permission;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.PositionConstraint;

public class SymphonyAdminPage extends AdminPage {

	public static final String PLUGIN_NAME = "teamcity-symphony-integration";

    private static final Logger log = Logger.getLogger(SymphonyAdminPage.class);
	
	private SymphonyAdminController config;
	
	public SymphonyAdminPage(SymphonyAdminController config, PagePlaces pp, PluginDescriptor pd) {
		super(pp);
		this.config = config;
		setPluginName(PLUGIN_NAME);
		setPosition(PositionConstraint.after("email"));
		setTabTitle("Symphony Notifier");
		setIncludeUrl(pd.getPluginResourcesPath("/SymphonyAdminPage.jsp"));
		addCssFile(pd.getPluginResourcesPath("/symphony.css"));
        log.warn("SYMPHONY: Constructed admin page");
        register();
	}

	public String getGroup() {
		return SERVER_RELATED_GROUP;
	}

	@Override
	public void fillModel(Map<String, Object> model, HttpServletRequest request) {
		log.warn("filling model SYMPHONY ADMIN PAGE");
		super.fillModel(model, request);
		Config cfgOb = SymphonyAdminController.packCerts(config.getConfig());
		model.put("podProperties", cfgOb.getPodProperties());
		model.put("identityProperties", cfgOb.getIdentityProperties());
		model.put("trustStoreProperties", cfgOb.getTrustStoreProperties());
		model.put("endpoints", Arrays.asList("pod", "sessionAuth", "keyAuth", "agent", "relay", "login"));
		model.put("certificates", cfgOb.getCertificates());
	}

	@Override
	public boolean isAvailable(HttpServletRequest request) {
		return super.isAvailable(request) && checkHasGlobalPermission(request, Permission.CHANGE_SERVER_SETTINGS);
	}
	
	
	
}

