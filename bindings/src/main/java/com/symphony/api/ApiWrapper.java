package com.symphony.api;

/**
 * This interface allows you to specify a proxy-based wrapper around an implementation class.
 * This is implemented by {@link TokenManager}, but could also be used for other wrappers, 
 * such as metrics recording or leader election/failover.
 * 
 * @author Rob Moffat
 *
 */
public interface ApiWrapper {

	/**
	 * Returns a wrapped implementation of the interface.
	 * @param c interface class.
	 * @param api instance of the interface
	 * @return wrapped instance.
	 */
	public <X> X wrap(Class<X> c, X api);

}