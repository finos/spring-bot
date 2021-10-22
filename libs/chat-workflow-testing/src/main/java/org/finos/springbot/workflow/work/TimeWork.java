package org.finos.springbot.workflow.work;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.finos.springbot.workflow.annotations.Work;

@Work
public class TimeWork {

	Instant i;
	
	LocalDateTime ldt;
	
	ZoneId zid;
	
	LocalDate ld;
	
	LocalTime lt;
	
	ZonedDateTime zdt;

	public Instant getI() {
		return i;
	}

	public void setI(Instant i) {
		this.i = i;
	}

	public LocalDateTime getLdt() {
		return ldt;
	}

	public void setLdt(LocalDateTime ldt) {
		this.ldt = ldt;
	}

	public ZoneId getZid() {
		return zid;
	}

	public void setZid(ZoneId zid) {
		this.zid = zid;
	}

	public LocalDate getLd() {
		return ld;
	}

	public void setLd(LocalDate ld) {
		this.ld = ld;
	}

	public LocalTime getLt() {
		return lt;
	}

	public void setLt(LocalTime lt) {
		this.lt = lt;
	}

	public ZonedDateTime getZdt() {
		return zdt;
	}

	public void setZdt(ZonedDateTime zdt) {
		this.zdt = zdt;
	}
	

	
}
