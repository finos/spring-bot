package org.finos.symphony.toolkit.spring.app.obo;

import java.util.List;

import javax.net.ssl.TrustManager;

import org.finos.symphony.toolkit.spring.api.builders.ApiBuilderFactory;
import org.finos.symphony.toolkit.spring.api.factories.MetricsApiWrapper;
import org.finos.symphony.toolkit.spring.api.properties.EndpointProperties;
import org.finos.symphony.toolkit.spring.api.properties.PodProperties;
import org.finos.symphony.toolkit.spring.app.pods.info.PodInfoStore;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.symphony.api.bindings.ApiWrapper;
import com.symphony.api.id.SymphonyIdentity;

import io.micrometer.core.instrument.MeterRegistry;

/**
 * Creates OBO ApiInstances with metrics.  Since this could be talking to lots of pods, we 
 * aren't going to add health indicators, since we don't want the status of our application to be dependent 
 * on that.  Also, we generally expect that if there is a health problem on symphony, people won't be able
 * to use the app on that pod anyway.
 * 
 * @author Rob Moffat
 */
public class DefaultOboInstanceFactory extends TokenManagingOboApiInstanceFactory {

	protected MeterRegistry mr;
	protected ObjectMapper om;

	public DefaultOboInstanceFactory(ApiBuilderFactory apiBuilderFactory, 
			PodInfoStore podInfo,
			List<PodProperties> podPropertiesList, 
			SymphonyIdentity appId, 
			TrustManager[] trustManagers, 
			PodInfoConverter converter,
			MeterRegistry meter, 
			ObjectMapper om) {
		super(apiBuilderFactory, podInfo, podPropertiesList, appId, trustManagers, converter);
		this.mr = meter;
		this.om = om;
	}
	
	@Override
	protected List<ApiWrapper> buildApiWrappers(PodProperties pp, OboIdentity id, EndpointProperties ep) {
		List<ApiWrapper> out = super.buildApiWrappers(pp, id, ep);
		out.add(new MetricsApiWrapper(mr, pp, id.getTheApp().getCommonName()+"/"+id.getOboUserId(), ep.getUrl()));
		return out;
	}
	
}
