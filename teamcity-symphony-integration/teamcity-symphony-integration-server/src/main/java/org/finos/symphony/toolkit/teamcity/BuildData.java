package org.finos.symphony.toolkit.teamcity;

import jetbrains.buildServer.Build;

public class BuildData {
	
	private final String project;
	private final String build;
	private final String statusText;
	private final String statusColor;
	private final String url;
	private final String detail;
	
	public BuildData(String project, String build, String statusText, String statusColor) {
		super();
		this.project = project;
		this.build = build;
		this.statusText = statusText;
		this.statusColor = statusColor;
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
	
}
