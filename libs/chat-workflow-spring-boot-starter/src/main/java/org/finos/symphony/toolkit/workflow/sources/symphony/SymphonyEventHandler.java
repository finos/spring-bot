package org.finos.symphony.toolkit.workflow.sources.symphony;

import java.util.function.Consumer;

import com.symphony.api.model.V4Event;

public interface SymphonyEventHandler extends Consumer<V4Event> {

}
