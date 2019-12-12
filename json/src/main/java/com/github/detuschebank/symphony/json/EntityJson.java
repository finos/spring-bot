package com.github.detuschebank.symphony.json;

import java.util.LinkedHashMap;
import java.util.Map;

public final class EntityJson extends LinkedHashMap<String, Object>{

	public EntityJson() {
		super();
	}

	public EntityJson(int initialCapacity, float loadFactor, boolean accessOrder) {
		super(initialCapacity, loadFactor, accessOrder);
	}

	public EntityJson(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	public EntityJson(int initialCapacity) {
		super(initialCapacity);
	}

	public EntityJson(Map<? extends String, ? extends Object> m) {
		super(m);
	}

	
}
