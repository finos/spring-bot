package org.finos.springbot.workflow.history;

import org.finos.springbot.workflow.content.Addressable;

public interface PlatformHistory<A extends Addressable> extends History<A> {

	public boolean isSupported(Addressable a);
}
