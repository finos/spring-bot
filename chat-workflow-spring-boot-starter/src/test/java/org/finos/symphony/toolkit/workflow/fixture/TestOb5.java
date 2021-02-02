package org.finos.symphony.toolkit.workflow.fixture;

import org.finos.symphony.toolkit.workflow.java.Work;

@Work(name="Test Object 5", instructions="sundry other fields")
public class TestOb5 {

	TestOb4 ob4;

	public TestOb4 getOb4() {
		return ob4;
	}

	public void setOb4(TestOb4 ob4) {
		this.ob4 = ob4;
	}
	
}

