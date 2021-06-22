package org.finos.symphony.toolkit.spring.app.obo;

import java.security.Principal;
import java.util.List;
import java.util.Map;

import javax.net.ssl.TrustManager;

import org.finos.symphony.toolkit.spring.api.builders.ApiBuilderFactory;
import org.finos.symphony.toolkit.spring.api.factories.AbstractGenericInstanceTokenManagingFactory;
import org.finos.symphony.toolkit.spring.api.factories.ApiInstance;
import org.finos.symphony.toolkit.spring.api.properties.PodProperties;
import org.finos.symphony.toolkit.spring.api.properties.PodProperties.AuthMethod;
import org.finos.symphony.toolkit.spring.app.pods.info.PodInfo;
import org.finos.symphony.toolkit.spring.app.pods.info.PodInfoStore;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.util.StringUtils;

import com.symphony.api.authenticator.CertificateAuthenticationApi;
import com.symphony.api.bindings.ApiBuilder;
import com.symphony.api.bindings.TokenManager;
import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.login.AuthenticationApi;
import com.symphony.api.model.OboAuthResponse;
import com.symphony.api.model.Token;

public class TokenManagingOboApiInstanceFactory extends AbstractGenericInstanceTokenManagingFactory<OboIdentity>
		implements OboInstanceFactory {

	protected List<PodProperties> podPropertiesList;
	protected PodInfoStore ps;
	protected SymphonyIdentity appId;
	protected TrustManager[] trustManagers;
	protected PodInfoConverter converter;

	public TokenManagingOboApiInstanceFactory(
			ApiBuilderFactory apiBuilderFactory, 
			PodInfoStore podInfo,
			List<PodProperties> podPropertiesList, 
			SymphonyIdentity appId, 
			TrustManager[] trustManagers, 
			PodInfoConverter converter) {
		super(apiBuilderFactory);
		this.podPropertiesList = podPropertiesList;
		this.ps = podInfo;
		this.appId = appId;
		this.trustManagers = trustManagers;
		this.converter = converter;
	}

	protected TokenManager createTokenManager(OboIdentity obo, PodProperties pp, ApiBuilder sessionAuth,
			ApiBuilder keyAuth, ApiBuilder relay, ApiBuilder login) {
		TokenManager tm;
		if (usingCertificates(obo.getTheApp(), pp)) {
			tm = new TokenManager(() -> {
				CertificateAuthenticationApi api = sessionAuth.getApi(CertificateAuthenticationApi.class);
				Token t1 = api.v1AppAuthenticatePost();
				OboAuthResponse r1 = api.v1AppUserUidAuthenticatePost(obo.getOboUserId(), t1.getToken());
				return new Token().token(r1.getSessionToken());
			}, () -> new Token().token(TokenManager.OBO_PLACEHOLDER));
		} else {
			tm = new TokenManager(() -> {
				AuthenticationApi api = login.getApi(AuthenticationApi.class);
				Token t1 = api.pubkeyAuthenticatePost(createAuthenticateRequest(obo.getTheApp()));
				Token oboToken = api.pubkeyAppUserUserIdAuthenticatePost(t1.getToken(), obo.getOboUserId());
				return oboToken;
			}, () -> new Token().token(TokenManager.OBO_PLACEHOLDER));
		}
		return tm;
	}
	
	protected boolean usingCertificates(SymphonyIdentity botIdentity, PodProperties pp) {
		boolean hasCertAuth = (pp.getAuthMethod() != AuthMethod.RSA) &&  
				(StringUtils.hasText(pp.getSessionAuth().getUrl()));
		boolean hasCerts = (botIdentity.getCertificateChain() != null) && (botIdentity.getCertificateChain().length > 0);
		return hasCerts && hasCertAuth;
	}

	@Override
	protected SymphonyIdentity getIdentity(OboIdentity id) {
		return id.getTheApp(); // for obo, we return app's identity
	}

	@Override
	public ApiInstance createApiInstance(Long oboUserId, String companyId) throws Exception {
		OboIdentity oboIdentity = new OboIdentity(appId, oboUserId);
		return createApiInstance(oboIdentity, getPodProperties(companyId), trustManagers);
	}

	@Override
	public ApiInstance createApiInstance(Long oboUserId) throws Exception {
		return createApiInstance(oboUserId, null);
	}

	public PodProperties getPodProperties(String companyId) {
		if (podPropertiesList != null) {
			// default null-argument version - return the first (possibly only) configured
			// pod.
			if (companyId == null) {
				return podPropertiesList.get(0);

			}

			// find matching pod/company id
			for (PodProperties podProperties : podPropertiesList) {
				if (companyId.equals(podProperties.getId())) {
					return podProperties;
				}
			}
		}

		if ((ps != null) && (converter != null)) {
			PodInfo pi = ps.getPodInfo(companyId);
			PodProperties out = converter.convert(pi);
			
			if (out != null) {
				return out;
			}
		}

		throw new IllegalArgumentException(
				"Couldn't find pod properties for company ID: " +
					companyId + ". Maybe set this in application.yml");
	}

	@Override
	public ApiInstance createApiInstance(Principal p) throws Exception {
		Long userId = getSymphonyUserIdFromPrincipal(p);
		String companyid = getSymphonyCompanyIdFromPrincipal(p);
		return createApiInstance(userId, companyid);
	}

	protected Map<String, String> getRequestParameters(Principal p) {
		OAuth2Authentication oa2a = (OAuth2Authentication) p;
		OAuth2Request req = oa2a.getOAuth2Request();
		Map<String, String> requestParameters = req.getRequestParameters();
		return requestParameters;
	}

	@Override
	public Long getSymphonyUserIdFromPrincipal(Principal p) throws Exception {
		Map<String, String> requestParameters = getRequestParameters(p);
		Long userId = Long.parseLong(requestParameters.get("id"));
		return userId;
	}

	@Override
	public String getSymphonyCompanyIdFromPrincipal(Principal p) throws Exception {
		Map<String, String> requestParameters = getRequestParameters(p);
		String companyid = requestParameters.get("companyId");
		return companyid;
	}
	

}
