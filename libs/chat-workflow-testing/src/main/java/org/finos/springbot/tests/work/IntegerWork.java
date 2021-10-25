package org.finos.springbot.tests.work;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.finos.springbot.workflow.annotations.Work;

@Work
public class IntegerWork {

	@Min(0)
	@Max(100)
	Integer s;

	public Integer getS() {
		return s;
	}

	public void setS(Integer s) {
		this.s = s;
	}
	
	
}
