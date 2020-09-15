package com.github.deutschebank.symphony.workflow.content;

public interface Tag extends Content {

	public enum Type { CASH("$"), HASH("#"), USER("@");
		
		Type(String symbol) {
			this.symbol = symbol;
		}

		public String getSymbol() {
			return symbol;
		}
		
		String symbol;
	}
	
	/**
	 * Underlying system id.
	 */
	public String getId();
	
	public Type getTagType();
	
	/**
	 * Screen, display name
	 */
	public String getName();
	
}
