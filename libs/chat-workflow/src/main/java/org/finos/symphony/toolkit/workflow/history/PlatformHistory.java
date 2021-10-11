package org.finos.symphony.toolkit.workflow.history;

import org.finos.symphony.toolkit.workflow.content.Addressable;

public interface PlatformHistory<A extends Addressable> extends History<A> {

	public boolean isSupported(Addressable a);
}
