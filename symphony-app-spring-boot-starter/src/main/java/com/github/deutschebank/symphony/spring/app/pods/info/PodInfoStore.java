package com.github.deutschebank.symphony.spring.app.pods.info;

import java.util.List;

/**
 * Stores podInfo objects for circle-of-trust.  When a pod registers an app, the registration endpoint
 * will call back to the configured store so that we can look up the pod endpoints by the pod ID to 
 * authenticate users later.
 * 
 * @author robmoffat
 *
 */
public interface PodInfoStore {

	public PodInfo getPodInfo(String podId);
	
	public void setPodInfo(PodInfo podInfo) throws Exception;
	
	public List<String> getKnownPodIds();
	
}
