package com.github.deutschebank.symphony.spring.app.pods.info;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class NoopPodInfoStore implements PodInfoStore {

	public static final Logger LOG = LoggerFactory.getLogger(NoopPodInfoStore.class);
	private ObjectMapper om;
	
	public NoopPodInfoStore(ObjectMapper om) {
		this.om = om;
	}
	
	@Override
	public PodInfo getPodInfo(String podId) {
		return null;
	}

	@Override
	public void setPodInfo(PodInfo podInfo) throws JsonProcessingException {
		LOG.warn("Pod Info store received registration: {} ",om.writeValueAsString(podInfo));
	}

	@Override
	public List<String> getKnownPodIds() {
		return Collections.emptyList();
	}

}
