package com.symphony.spring.app.pods.info;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Stores PodInfos in individual files in a directory.
 * This is most useful if you are running the application in Symphony's app store, and many
 * pods will be using it.  
 * 
 * Note that if you are load balancing, the directory used here will need to be shared across
 * all the instances of the application.
 * 
 * This relies on the locking semantics of the underlying file-system to ensure there aren't 
 * contention issues.
 * 
 * @author Rob Moffat
 *
 */
public class DirectoryBasedPodInfoStore implements PodInfoStore {

	private File directory;
	private ObjectMapper om;
	
	public DirectoryBasedPodInfoStore(File dir, ObjectMapper om) throws IOException {
		this.directory = dir;
		this.om = om;
		
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				throw new IOException("Couldn't create directory at "+dir);
			}
		}
	}
	
	@Override
	public PodInfo getPodInfo(String podId) {
		try {
			File podFile = new File(directory, podId+".json");
			return om.readValue(podFile, PodInfo.class);
		} catch (Exception e) {
			throw new PodStoreException("Couldn't load pod details for "+podId, e);
		}
	}

	@Override
	public void setPodInfo(PodInfo podInfo) {
		try {
			File podFile = new File(directory, podInfo.getCompanyId()+".json");
			om.writeValue(podFile, podInfo);
		} catch (Exception e) {
			throw new PodStoreException("Couldn't write pod details for "+podInfo.getCompanyId(), e);
		}
	}

	@Override
	public List<String> getKnownPodIds() {
		return Arrays.stream(directory.listFiles())
			.filter(f -> f.getName().endsWith(".json"))
			.map(f -> f.getName().replace(".json", ""))
			.collect(Collectors.toList());
	}

	
}
