package org.finos.springbot.workflow.content;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface Tag extends Content {
	
	public interface Type {
		
		public char getPrefix();
				
	}
	
	public static Type CASH = new Type() {

		@Override
		public char getPrefix() {
			return '$';
		}

	};
	
	public static Type HASH = new Type() {

		@Override
		public char getPrefix() {
			return '#';
		}
		
	};
	
	
	public static Type MENTION = new Type() {

		@Override
		public char getPrefix() {
			return '@';
		}

	};
	
	@JsonIgnore
	public Type getTagType();
	
	/**
	 * Screen, display name
	 */
	public String getName();
	
	@JsonIgnore
	public default String getText() {
		return getTagType().getPrefix() + getName();
	}
	
}
