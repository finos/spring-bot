package org.finos.symphony.practice.rsanag;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Report {

	private Instant reportDate = Instant.now();
	
	public Instant getReportDate() {
		return reportDate;
	}

	public void setReportDate(Instant reportDate) {
		this.reportDate = reportDate;
	}

	private List<UserRecord> records = new ArrayList<UserRecord>();

	public List<UserRecord> getRecords() {
		return records;
	}

	public void setRecords(List<UserRecord> records) {
		this.records = records;
	}
	
}
