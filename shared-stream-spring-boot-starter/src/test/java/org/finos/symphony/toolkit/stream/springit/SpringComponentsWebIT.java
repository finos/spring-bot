package org.finos.symphony.toolkit.stream.springit;

import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.cluster.ClusterMember;
import org.finos.symphony.toolkit.stream.cluster.ClusterMember.State;
import org.finos.symphony.toolkit.stream.cluster.messages.SuppressionMessage;
import org.finos.symphony.toolkit.stream.filter.SymphonyLeaderEventFilter;
import org.finos.symphony.toolkit.stream.fixture.NoddyCallback;
import org.finos.symphony.toolkit.stream.fixture.TestApplication;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;

import com.symphony.api.agent.MessagesApi;
import com.symphony.api.id.SymphonyIdentity;


/**
 * NOTE: This probably won't work on the local PC due to proxies.  You can override the property to use userproxy if you want.
 * 
 * @author Rob Moffat
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
	properties = { 
			"logging.level.org.finos.symphony.toolkit=debug",
			"server.port=15743",
			"symphony.stream.coordination-stream-id=y3EJYqKMwG7Jn7/YqyYdiX///pR3YrnTdA=="}, 
	webEnvironment = WebEnvironment.DEFINED_PORT, 
	classes={TestApplication.class})
@ActiveProfiles("develop")
public class SpringComponentsWebIT {
	
	private String someLocalConversation = "Cscf+rSZRtGaOUrhkelBaH///o6ry5/5dA==";

	@MockBean
	TaskScheduler taskScheduler;
	
	@Autowired
	MessagesApi api;
	
	@Autowired
	SymphonyIdentity id;
	
	@Autowired
	Participant self;
	
	@Autowired
	NoddyCallback noddyCallback;
	
	@Autowired
	ClusterMember cm;
	
	@Autowired
	SymphonyLeaderEventFilter eventFilter;
	
	@Test
	public void testCallEndpoint() throws Exception {
		int sc = WebClient.create(self.getDetails())
			.post()
			.bodyValue(new SuppressionMessage(self, 102))
			.accept(MediaType.APPLICATION_JSON)
			.exchange()
			.block()
			.rawStatusCode();
		Assert.assertEquals(200, sc);
	}
	
	@Test
	public void testCallbackGetsCalled() throws Exception {
		// wait for this cluster to become leader
		while (cm.getState() != State.LEADER) {
			Thread.sleep(50);
		}

		// wait for the event to say it's leader.
		while (!eventFilter.isActive()) {
			Thread.sleep(50);
		}
		
		// post an event.
		api.v4StreamSidMessageCreatePost(null, someLocalConversation, "<messageML>This is a test</messageML>", null, null, null, null, null);

		// wait for it to arrive
		while (noddyCallback.getReceived().size() == 0) {
			Thread.sleep(50);
		}
	}
}