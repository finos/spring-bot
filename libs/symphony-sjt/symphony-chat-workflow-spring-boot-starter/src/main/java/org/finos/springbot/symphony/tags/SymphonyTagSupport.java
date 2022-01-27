package org.finos.springbot.symphony.tags;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import org.finos.springbot.symphony.content.HashTag;
import org.finos.springbot.symphony.content.SymphonyUser;
import org.finos.springbot.workflow.content.Tag;
import org.finos.springbot.workflow.tags.TagSupport;
import org.springframework.util.StringUtils;

public class SymphonyTagSupport extends TagSupport {

	public static String toHashTag(String in) {
		if (!StringUtils.hasText(in)) {
			return "";
		}
		
		return "<hash tag=\""+formatTag(in)+"\" /> ";
	}
	
	public static String toCashTag(String in) {
		if (!StringUtils.hasText(in)) {
			return "";
		}
		
		return "<cash tag=\""+formatTag(in)+"\" /> ";
	}
	
	public static String toUserTag(String id) {
		if (!StringUtils.hasText(id)) {
			return "";
		}
		
		return "<mention uid=\"" + id + "\" />";
	}
	
	public static String format(Tag t) {
		if (t.getTagType() == Tag.CASH) {
			return toCashTag(t.getName());
		} else if (t.getTagType() == Tag.HASH) {
			return toHashTag(t.getName());
		} else if (t.getTagType() == Tag.MENTION) {
			return toUserTag(((SymphonyUser) t).getUserId());
		}
		
		return "";
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
	
	public static Set<HashTag> toHashTags(Class<?> c) {
		return toTags(c).stream()
				.map(e -> new HashTag(e))
				.collect(Collectors.toSet());
	}
	
}
