package org.finos.springbot.workflow.templating;

import java.util.Collections;
import java.util.Map;

public abstract class AbstractTypeConverter<X> implements TypeConverter<X> {
	
	public static final int MED_PRIORITY = 40;
	public static final int LOW_PRIORITY = 50;
	public static final int BOTTOM_PRIORITY = 2000;
	
	public static final Map<String, String> CENTER_ALIGN = Collections.singletonMap("style", "text-align:center;");
	public static final Map<String, String> CENTER_AND_WIDTH_ALIGN = Collections.singletonMap("style", "text-align:center; width:10%;");
	public static final Map<String, String> RIGHT_ALIGN = Collections.singletonMap("style", "text-align: right;");
	

	private final int priority;
	protected final Rendering<X> r;

	public AbstractTypeConverter(int priority, Rendering<X> r) {
		super();
		this.priority = priority;
		this.r = r;
	}

	@Override
	public int getPriority() {
		return priority;
	}
	
	

	
}
