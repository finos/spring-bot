package org.finos.symphony.toolkit.quickfix;

import java.lang.reflect.Field;

import quickfix.FieldMap;

public class ReflectionHelpers {


	private static Field fieldOrder;
	
	static {
		try {
			fieldOrder = FieldMap.class.getDeclaredField("fieldOrder");
			fieldOrder.setAccessible(true);
		} catch (Exception e) {
			throw new UnsupportedOperationException("Couldn't initialize Reflection", e);
		}
	}
	
	public static void setFieldOrder(FieldMap fm, int[] fo) {
		try {
			fieldOrder.set(fm, fo);
		} catch (Exception e) {
			throw new UnsupportedOperationException("Couldn't set fieldOrder", e);
		}
	}
}

