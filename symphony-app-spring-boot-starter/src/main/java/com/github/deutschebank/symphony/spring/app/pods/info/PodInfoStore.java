package com.github.deutschebank.symphony.spring.app.pods.info;

import java.util.List;

public interface PodInfoStore {

	public PodInfo getPodInfo(String podId);
	
	public void setPodInfo(PodInfo podInfo) throws Exception;
	
	public List<String> getKnownPodIds();
	
}
