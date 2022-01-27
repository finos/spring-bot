package org.finos.springbot.symphony.conversations;

import org.finos.springbot.symphony.content.SymphonyAddressable;

public interface StreamResolver {

	String getStreamFor(SymphonyAddressable a);

}