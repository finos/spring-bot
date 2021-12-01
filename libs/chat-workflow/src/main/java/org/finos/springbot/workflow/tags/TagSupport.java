package org.finos.springbot.workflow.tags;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.finos.springbot.workflow.annotations.Work;

public class TagSupport {
	
	public static Set<String> classTags(Object in) {
		if (in instanceof Class<?>) {
			return toTags((Class<?>) in).stream()
					.collect(Collectors.toSet());
		}
		if (in != null) {
			return toTags(in.getClass()).stream()
				.collect(Collectors.toSet());
		} else {
			return Collections.emptySet();
		}
	}

	
	public static Set<String> toTags(Class<?> c) {
		if ((c == Object.class) || (c == null)) {
			return Collections.emptySet();
		} else if (c.getAnnotationsByType(Work.class).length== 0) {
			return Collections.emptySet();
		} else {
			Set<String> out = new HashSet<>();
			out.add(formatTag(c));
			for (Class<?> i : c.getInterfaces()) {
				out.add(formatTag(i));
			}
			
			out.addAll(toTags(c.getSuperclass()));
			return out;
		}
	}
	

	public static String formatTag(String in) {
		return in.replace(".", "-").toLowerCase();
	}
	
	public static String formatTag(Class<?> in) {
		return formatTag(in.getCanonicalName());
	}
	
}
