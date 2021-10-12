package org.finos.symphony.toolkit.workflow.content;

public interface Image extends Content {

	@Override
	public default String getText() {
		return "";
	}

	public String getUrl();
	
	public String getAlt();
	
	public static Image of(String url, String alt) {
		return new Image() {

			@Override
			public String getUrl() {
				return url;
			}

			@Override
			public String getAlt() {
				return alt;
			}
		};
	}
	
}
