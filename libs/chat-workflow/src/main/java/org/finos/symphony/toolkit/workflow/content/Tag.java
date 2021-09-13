package org.finos.symphony.toolkit.workflow.content;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface Tag extends Content {
	
	public interface Type {
		
		public char getPrefix();
		
		public String getTypeName();
		
	}
	
	public static Type CASH = new Type() {

		@Override
		public char getPrefix() {
			return '$';
		}

		@Override
		public String getTypeName() {
			return "CASH";
		}
		
	};
	
	public static Type HASH = new Type() {

		@Override
		public char getPrefix() {
			return '#';
		}

		@Override
		public String getTypeName() {
			return "HASH";
		}
		
	};
	
	
	public static Type USER = new Type() {

		@Override
		public char getPrefix() {
			return '@';
		}

		@Override
		public String getTypeName() {
			return "USER";
		}
		
	};
	
	@JsonIgnore
	public Type getTagType();
	
	/**
	 * Screen, display name
	 */
	@JsonIgnore
	public String getName();
	
	@JsonIgnore
	public default String getText() {
		return getTagType().getPrefix() + getName();
	}
	
}
