package com.github.deutschebank.symphony.stream;

import com.github.deutschebank.symphony.json.EntityJsonTypeResolverBuilder.VersionSpace;


public class MessagingVersionSpace {

	public static final VersionSpace THIS = 
			new VersionSpace(MessagingVersionSpace.class.getPackage().getName(), "1.0", "1.0");

}
