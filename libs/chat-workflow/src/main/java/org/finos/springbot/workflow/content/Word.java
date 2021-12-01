package org.finos.springbot.workflow.content;

public interface Word extends Content {

	/**
	 * This allows us to do relaxed word matching, where the user is free to change
	 * case or punctuate between the words. 
	 */
	public default String getIdentifier() {
		return getText().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
	}
	
	public static Word of(String s) {
		return new Word() {

			@Override
			public String getText() {
				return s;
			}

			@Override
			public int hashCode() {
				return getIdentifier().hashCode();
			}

			@Override
			public boolean equals(Object obj) {
				if (obj instanceof Word) {
					return this.getIdentifier().equals(((Word) obj).getIdentifier());
				} else {
					return false;
				}
			}

			@Override
			public String toString() {
				return "Word ["+s+"]";
			}

		};
	}
}
