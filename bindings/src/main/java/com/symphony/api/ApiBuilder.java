package com.symphony.api;

/**
 * A common interface to allow you to construct typed Symphony APIs.
 * 
 * Currently we have two implementations of this, one for jersey and one for cxf.
 * Users are encouraged to extend this class or write their own using this for inspiration.
 * 
 * @author Rob Moffat
 *
 */
public interface ApiBuilder {
	
	public <X> X getApi(Class<X> c);
		
}
