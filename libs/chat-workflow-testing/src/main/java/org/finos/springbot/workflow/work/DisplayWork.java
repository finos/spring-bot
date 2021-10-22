package org.finos.springbot.workflow.work;

import org.finos.springbot.workflow.annotations.Display;
import org.finos.springbot.workflow.annotations.Work;

@Work
public class DisplayWork {

	@Display(name = "Some crazy name")
	String s;
	
	@Display(visible = false)
	String invisible;

	public String getS() {
		return s;
	}

	public void setS(String s) {
		this.s = s;
	}

	public String getInvisible() {
		return invisible;
	}

	public void setInvisible(String invisible) {
		this.invisible = invisible;
	}
	
	
	
}
