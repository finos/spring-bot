package org.finos.springbot.tests.form;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

import org.finos.springbot.workflow.annotations.Work;

@Work
public class Temporal {

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

	@Override
	public String toString() {
		return "Temporal [i=" + i + ", ldt=" + ldt + ", zid=" + zid + ", ld=" + ld + ", lt=" + lt + ", zdt=" + zdt
				+ "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(i, ld, ldt, lt, zdt, zid);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Temporal other = (Temporal) obj;
		return Objects.equals(i, other.i) && Objects.equals(ld, other.ld) && Objects.equals(ldt, other.ldt)
				&& Objects.equals(lt, other.lt) && Objects.equals(zdt, other.zdt) && Objects.equals(zid, other.zid);
	}
	

	
}
