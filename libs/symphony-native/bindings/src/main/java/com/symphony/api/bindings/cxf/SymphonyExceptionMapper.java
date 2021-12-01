package com.symphony.api.bindings.cxf;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Scanner;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.apache.cxf.jaxrs.client.ResponseExceptionMapper;
import org.apache.cxf.jaxrs.utils.ExceptionUtils;

/**
 * This improves the error messages returned to your code from Symphony.
 * Without this, the messages are swallowed and not reported in the stack-trace.
 * 
 * @author robmoffat
 *
 */
@Provider
public class SymphonyExceptionMapper implements ResponseExceptionMapper<Exception> {

	@Override
	public Exception fromResponse(Response r) {
		try {
			Class<?> exceptionClass = ExceptionUtils.getWebApplicationExceptionClass(r, WebApplicationException.class);
			Constructor<?> ctr = exceptionClass.getConstructor(String.class, Response.class);
			String code = convertStreamToString((InputStream) r.getEntity());
			
			String message = r.getStatusInfo().getReasonPhrase() +" "+ code;
			return (WebApplicationException)ctr.newInstance(message, r);
		} catch (Exception e) {
			return null; // will be handled by normal approach.
		}
	}
	

    @SuppressWarnings("resource") // jaxrs responsible for closing
	static String convertStreamToString(InputStream is) {
		java.util.Scanner s = new Scanner(is, "UTF-8").useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}

	
}
