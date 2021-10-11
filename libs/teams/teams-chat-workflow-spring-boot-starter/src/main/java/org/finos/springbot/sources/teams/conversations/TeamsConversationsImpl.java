package org.finos.springbot.sources.teams.conversations;

import org.finos.springbot.sources.teams.streams.AbstractStreamResolving;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

/**
 * Basic implementation of symphony rooms with no caching.
 * @author Rob Moffat
 *
 */
public class TeamsConversationsImpl extends AbstractStreamResolving implements TeamsConversations, InitializingBean {
	
	private static final Logger LOG = LoggerFactory.getLogger(TeamsConversationsImpl.class);

}
