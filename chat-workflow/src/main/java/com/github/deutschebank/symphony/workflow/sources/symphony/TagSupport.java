package com.github.deutschebank.symphony.workflow.sources.symphony;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.util.StringUtils;

import com.github.deutschebank.symphony.workflow.Workflow;
import com.github.deutschebank.symphony.workflow.content.Tag;

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
	
	public static Set<String> classHashTags(Object in) {
		if (in != null) {
			return toHashTags(in.getClass());
		} else {
			return Collections.emptySet();
		}
	}

	
	
	private static String formatTag(String in) {
		return in.replace(".", "-").toLowerCase();
	}
	
	public static String toHashTag(Class<?> in) {
		if (in == null) {
			return "";
		}
		
		return toHashTag(in.getCanonicalName());
	}
	
	public static Set<String> toHashTags(Class<?> c) {
		if ((c == Object.class) || (c == null)) {
			return Collections.emptySet();
		} else {
			Set<String> out = new HashSet<>();
			out.add(toHashTag(c));
			for (Class<?> i : c.getInterfaces()) {
				out.add(toHashTag(i));
			}
			
			out.addAll(toHashTags(c.getSuperclass()));
			return out;
		}
		
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
