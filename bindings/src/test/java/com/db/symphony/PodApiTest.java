package com.db.symphony;

import java.util.Collections;

import org.junit.Assert;
import org.junit.experimental.theories.Theory;

import com.symphony.api.model.RoomSearchCriteria.SortOrderEnum;
import com.symphony.api.model.UserV2;
import com.symphony.api.model.V2RoomSearchCriteria;
import com.symphony.api.model.V3RoomSearchResults;
import com.symphony.api.pod.StreamsApi;
import com.symphony.api.pod.UsersApi;


/**
 * Uses the TokenProvider to automatically configure token fields.
 * 
 * @author moffrob
 *
 */
public class PodApiTest extends AbstractTest {

	@Theory
	public void testUserLookup(TestClientStrategy client) throws Exception {
		UsersApi uApi = client.getPodApi(UsersApi.class);
		UserV2 u = uApi.v2UserGet(null, null, client.getIdentity().getEmail(), null, false);
		System.out.println(u);
		Assert.assertTrue(u.getDisplayName().contains("Symphony Practice"));
	}
	
	@Theory
	public void testRoomSearch(TestClientStrategy client) throws Exception {
		StreamsApi sApi = client.getPodApi(StreamsApi.class);
		V2RoomSearchCriteria criteria = new V2RoomSearchCriteria();
		criteria.setQuery("Demo");
		criteria.setPrivate(true);
		criteria.setActive(true);
		criteria.setLabels(Collections.emptyList());
		criteria.setSortOrder(SortOrderEnum.BASIC);
		V3RoomSearchResults res = sApi.v3RoomSearchPost(criteria, null, 0, 100);
		System.out.println(res);
	}
}
