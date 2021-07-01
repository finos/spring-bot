package org.finos.symphony.toolkit.workflow.sources.symphony;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.annotations.Work;
import org.finos.symphony.toolkit.workflow.content.HashTag;
import org.finos.symphony.toolkit.workflow.content.HashTagDef;
import org.finos.symphony.toolkit.workflow.content.Tag;
import org.springframework.util.StringUtils;

public class TagSupport {

	public static String toHashTag(String in) {
		if (StringUtils.isEmpty(in)) {
			return "";
		}
		
		return "<hash tag=\""+formatTag(in)+"\" /> ";
	}
	
	public static String toCashTag(String in) {
		if (StringUtils.isEmpty(in)) {
			return "";
		}
		
		return "<cash tag=\""+formatTag(in)+"\" /> ";
	}
	
	public static String toUserTag(String id) {
		if (StringUtils.isEmpty(id)) {
			return "";
		}
		
		return "<mention uid=\"" + id + "\" />";
	}
	
	public static String format(Tag t) {
		if (t == null) {
			return "";
		}
		switch(t.getTagType()) {
		case CASH:
			return toCashTag(t.getName());
		case HASH:
			return toHashTag(t.getName());
		case USER:
			return toUserTag(t.getId());
		default:
			return "";
		}
	}
	
	public static Set<HashTag> classHashTags(Object in) {
		if (in instanceof Class<?>) {
			return toHashTags((Class<?>) in).stream()
					.collect(Collectors.toSet());
		}
		if (in != null) {
			return toHashTags(in.getClass()).stream()
				.collect(Collectors.toSet());
		} else {
			return Collections.emptySet();
		}
	}
	
	public static HashTag toHashTag(Class<?> in) {
		if (in == null) {
			return null;
		}
		
		return new HashTagDef(formatTag(in));
	}
	
	public static Set<HashTag> toHashTags(Class<?> c) {
		if ((c == Object.class) || (c == null)) {
			return Collections.emptySet();
		} else if (c.getAnnotationsByType(Work.class).length== 0) {
			return Collections.emptySet();
		} else {
			Set<HashTag> out = new HashSet<>();
			out.add(toHashTag(c));
			for (Class<?> i : c.getInterfaces()) {
				out.add(toHashTag(i));
			}
			
			out.addAll(toHashTags(c.getSuperclass()));
			return out;
		}
		
	}
	

	public static String formatTag(String in) {
		return in.replace(".", "-").toLowerCase();
	}
	
	public static String formatTag(Class<?> in) {
		return formatTag(in.getCanonicalName());
	}
	
	public static String toHashTag(Workflow wf) {
		if (wf == null) {
			return "";
		}
		return toHashTag(wf.getNamespace());
	}
}
