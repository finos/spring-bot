package org.finos.springbot.workflow.templating.fixture;

import java.util.List;

import org.finos.springbot.workflow.annotations.Work;

@Work
public class CollectionBeanWork {


	public static class Inner {
		
		String s;

		public String getS() {
			return s;
		}

		public void setS(String s) {
			this.s = s;
		}
		
	}
	
	List<Inner> inners;

	public List<Inner> getInners() {
		return inners;
	}

	public void setInners(List<Inner> inners) {
		this.inners = inners;
	}
	
}
