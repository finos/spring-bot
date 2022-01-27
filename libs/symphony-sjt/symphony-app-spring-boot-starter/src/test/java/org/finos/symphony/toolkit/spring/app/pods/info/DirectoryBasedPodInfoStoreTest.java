package org.finos.symphony.toolkit.spring.app.pods.info;


import java.io.File;
import java.util.Arrays;

import org.finos.symphony.toolkit.spring.app.AbstractTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.databind.ObjectMapper;


@TestPropertySource(properties={
		"symphony.app.store.location=target/test-pod-store",
})
public class DirectoryBasedPodInfoStoreTest extends AbstractTest {

	@Autowired
	PodInfoStore store;
	
	@Autowired
	ObjectMapper om;
	
	@Test
	public void testSaveAndLoad() throws Exception {
		new File("target/test-pod-store/9999.json").delete();
		new File("target/test-pod-store").mkdirs();
		
		PodInfo in = om.readValue(DirectoryBasedPodInfoStoreTest.class.getResourceAsStream("/pods/9999.json"), PodInfo.class);
		store.setPodInfo(in);
		
		PodInfo out = store.getPodInfo("9999");
		
		Assertions.assertEquals(om.writeValueAsString(in), om.writeValueAsString(out));
		Assertions.assertEquals(Arrays.asList("9999"), store.getKnownPodIds());
		
		
	}
}
