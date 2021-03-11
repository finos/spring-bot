package org.finos.symphony.practice.rsanag.key;

public interface RSAKeyManagement {

	public void updateKey(long userId, String jwtTokenProof, String newPublicKey);
	
	public void revokeKey(long userId);
}
