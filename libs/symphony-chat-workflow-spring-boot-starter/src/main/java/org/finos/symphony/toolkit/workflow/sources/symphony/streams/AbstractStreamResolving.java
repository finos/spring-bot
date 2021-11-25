package org.finos.symphony.toolkit.workflow.sources.symphony.streams;

import java.util.Collections;

import org.finos.symphony.toolkit.spring.api.properties.SymphonyApiProperties;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyAddressable;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyRoom;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyUser;

import com.symphony.api.model.Stream;
import com.symphony.api.model.UserV2;
import com.symphony.api.pod.StreamsApi;
import com.symphony.api.pod.UsersApi;
import com.symphony.user.StreamID;
import com.symphony.user.UserId;

/**
 * Allows you to find out the Symphony Stream ID from an {@link Addressable}.
 * 
 * @author rob@kite9.com
 *
 */
public class AbstractStreamResolving {

	protected StreamsApi streamsApi;
	protected UsersApi usersApi;
	protected SymphonyApiProperties symphonyApiProperties;
	
	public AbstractStreamResolving(StreamsApi streamsApi, UsersApi usersApi, SymphonyApiProperties symphonyApiProperties) {
		this.streamsApi = streamsApi;
		this.usersApi = usersApi;
		this.symphonyApiProperties = symphonyApiProperties;
	}

	public String getStreamFor(SymphonyAddressable a) {
		if (a instanceof SymphonyUser) {
			return getStreamIdForUser((SymphonyUser) a);
		} else if (a instanceof SymphonyRoom) {
			return ((SymphonyRoom) a).getStreamId();
		} else {
			throw new UnsupportedOperationException("What is this? "+a);
		}
	}

	protected String getStreamIdForUser(SymphonyUser a) {
		if (((SymphonyUser) a).getStreamId() != null) {
			return ((SymphonyUser) a).getStreamId();
		} else {
			long userId = getUserIdForUser(a);
			Stream s = streamsApi.v1ImCreatePost(Collections.singletonList(userId), null);
			a.getId().add(new StreamID(s.getId()));
			return s.getId();
		}
	}

	protected long getUserIdForUser(SymphonyUser a) {
		if (a.getUserId() != null) {
			return Long.parseLong(a.getUserId());
		} else {
			UserV2 u = usersApi.v2UserGet(null, null, a.getEmailAddress(), null, symphonyApiProperties.isLocalPod());
			if (u == null) {
				throw new IllegalArgumentException("Couldn't find user: "+a);
			} else {
				a.getId().add(new UserId(""+u.getId()));
				return u.getId();
			}
		}
	}
}
