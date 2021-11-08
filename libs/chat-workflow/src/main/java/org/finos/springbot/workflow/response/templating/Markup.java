package org.finos.springbot.workflow.response.templating;

public interface Markup {
	
	public static final Markup EMPTY_MARKUP = new Markup() {

		@Override
		public String getContents() {
			return "";
		}
		
		
	};

	public String getContents();

	public static Markup of(String string) {
		return new Markup() {

			@Override
			public String getContents() {
				return string;
			}

		};
	}
}
