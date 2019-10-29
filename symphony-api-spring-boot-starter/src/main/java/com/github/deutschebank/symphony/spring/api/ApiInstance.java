package com.github.deutschebank.symphony.spring.api;

/**
 * An ApiInstance is a single {@link SymphonyIdentity} running on a single {@link PodProperties} (pod).
 * 
 * From the ApiInstance, individual Symphony APIs can be created.
 * 
 * For single bot applications, the ApiInstance will be derived from a single bot identity, and the main pod details.
 * For multi-bot-instance applications, the client will be expected to create the bot instance using the ApiInstanceFac or other means.
 * 
 * It is expected that any proxy, metrics or health-checking will be handled by this class.
 * 
 * @author Rob Moffat
 */
public interface ApiInstance {

	public <X> X getPodApi(Class<X> c);
	
	public <X> X getAgentApi(Class<X> c);
	
	public <X> X getSessionAuthApi(Class<X> c);
	
	public <X> X getKeyAuthApi(Class<X> c);
	
	public <X> X getRelayApi(Class<X> c);
	
	public <X> X getLoginApi(Class<X> c);
	
}
