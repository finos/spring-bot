package org.finos.symphony.toolkit.spring.app.obo;

import com.symphony.api.id.SymphonyIdentity;

public class OboIdentity {

	private final SymphonyIdentity theApp;
	private final Long oboUserId;
	
	public OboIdentity(SymphonyIdentity theApp, Long oboUserId) {
		super();
		this.theApp = theApp;
		this.oboUserId = oboUserId;
	}

	@Override
	public String toString() {
		return "OboIdentity [theApp=" + theApp + ", oboUserId=" + oboUserId + "]";
	}

	public SymphonyIdentity getTheApp() {
		return theApp;
	}

	public Long getOboUserId() {
		return oboUserId;
	}

	
}
