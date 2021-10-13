package org.finos.symphony.toolkit.workflow.content;

public interface Link extends Content {

	@Override
	public default String getText() {
		return "";
	}

	public String getUrl();
	
	public String getAlt();
	
	public static Link of(String url, String alt) {
		return new Link() {

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
