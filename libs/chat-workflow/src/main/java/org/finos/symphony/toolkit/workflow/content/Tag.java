package org.finos.symphony.toolkit.workflow.content;

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
	
	public Type getTagType();
	
	/**
	 * Screen, display name
	 */
	public String getName();
	
	public default String getText() {
		return getTagType().getPrefix() + getName();
	}
	
}
