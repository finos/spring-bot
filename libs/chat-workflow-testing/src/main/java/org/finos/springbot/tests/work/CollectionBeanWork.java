package org.finos.springbot.tests.work;

import java.util.List;

import org.finos.springbot.workflow.annotations.Display;
import org.finos.springbot.workflow.annotations.Work;

@Work
public class CollectionBeanWork {


	public static class Inner {
		
		@Display(name = "String Field")
		String s;
		
		@Display(name = "Boolean field")
		boolean b;

		public String getS() {
			return s;
		}

		public void setS(String s) {
			this.s = s;
		}

		public boolean isB() {
			return b;
		}

		public void setB(boolean b) {
			this.b = b;
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
