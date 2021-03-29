package org.finos.symphony.toolkit.spring.app.tokens.app;

public interface AppTokenStrategy {

	public String generateAppToken();
	
	public void storeAppToken(String appToken, String podToken);
	
	public boolean checkTokens(String appToken, String podToken);

}
	
