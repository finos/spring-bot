package org.finos.symphony.toolkit.spring.app.obo;

import org.finos.symphony.toolkit.spring.api.properties.PodProperties;
import org.finos.symphony.toolkit.spring.app.pods.info.PodInfo;

/**
 * Turns a symphony {@link PodInfo} class into {@link PodProperties} so that we can construct
 * apis from it.
 * 
 * @author rob@kite9.com
 *
 */
public interface PodInfoConverter {

	PodProperties convert(PodInfo podInfo);
	
}
