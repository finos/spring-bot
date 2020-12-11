package org.finos.symphony.toolkit.stream;

import org.finos.symphony.toolkit.json.EntityJsonTypeResolverBuilder.VersionSpace;

public class MessagingVersionSpace {

	public static final VersionSpace THIS = 
			new VersionSpace(MessagingVersionSpace.class.getPackage().getName(), "1.0", "1.0");

}
