package org.finos.springbot.workflow.templating.fixture;

import javax.validation.constraints.Pattern;

import org.finos.springbot.workflow.annotations.Work;
import org.hibernate.validator.constraints.Length;

@Work
public class StringWork {

	@Length(min = 4, max = 15)
	@Pattern(regexp = "[a-z]*")
	String s;

	public String getS() {
		return s;
	}

	public void setS(String s) {
		this.s = s;
	}
	
	
}
