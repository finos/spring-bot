package org.finos.symphony.webhookbot;

import java.math.BigInteger;
import java.security.MessageDigest;

import com.symphony.api.bindings.StreamIDHelp;

public class Helpers {
	
	private String baseUrl;
	
	public Helpers(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	
	public String createHookUrl(String streamId, String hookId) {
		return baseUrl+"/hook/"+StreamIDHelp.safeStreamId(streamId)+"/"+hookId;
		
	}

	public String createHookId(String hashTag, String display) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			digest.reset();
			digest.update((hashTag+" "+display).getBytes("utf8"));
			String sha1 = String.format("%040x", new BigInteger(1, digest.digest()));
			return sha1.substring(0, 8);
		} catch (Exception e) {
			throw new RuntimeException("Couldn't create digest", e);
		}
	}

}
