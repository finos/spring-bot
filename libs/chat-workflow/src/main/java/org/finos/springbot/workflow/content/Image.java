package org.finos.springbot.workflow.content;

import java.util.Objects;

public interface Image extends Content {

	@Override
	public default String getText() {
		return "";
	}

	public String getUrl();
	
	public String getAlt();
	
	public static class ImageImpl implements Image {
		
		private final String url, alt;

		public ImageImpl(String url, String alt) {
			super();
			this.url = url;
			this.alt = alt;
		}
		
		@Override
		public String getUrl() {
			return url;
		}

		@Override
		public String getAlt() {
			return alt;
		}

		@Override
		public int hashCode() {
			return Objects.hash(alt, url);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			} else if (obj instanceof Image) {
				Image other = (Image) obj;
				return Objects.equals(alt, other.getAlt()) && Objects.equals(url, other.getUrl());
			} else {
				return false;
			}
		}
		
	}
	
	public static Image of(String url, String alt) {
		return new ImageImpl(url, alt);
	}
	
}
