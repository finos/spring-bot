package org.finos.symphony.toolkit.teamcity;

public class BuildData {
	
	private final String project;
	private final String build;
	private final String statusText;
	private final String statusColor;
	private final String url;
	private final String detail;
	
	public BuildData(String project, String build, String statusText, String statusColor, String url, String detail) {
		super();
		this.project = project;
		this.build = build;
		this.statusText = statusText;
		this.statusColor = statusColor;
		this.url = url;
		this.detail = detail;
	}

	public String getProject() {
		return project;
	}

	public String getBuild() {
		return build;
	}

	public String getStatusText() {
		return statusText;
	}

	public String getStatusColor() {
		return statusColor;
	}

	public String getUrl() {
		return url;
	}

	public String getDetail() {
		return detail;
	}
	
}
