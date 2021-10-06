package com.symphony.api.bindings.jersey;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.Response.Status.Family;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.ext.Provider;

/**
 * This improves the error messages returned to your code from Symphony.
 * Without this, the messages are swallowed and not reported in the stack-trace.
 * 
 * @author robmoffat
 *
 */
@Provider
public class SymphonyExceptionFilter implements ClientResponseFilter {

	@Override
	public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
		switch (responseContext.getStatusInfo().getFamily()) {
		case CLIENT_ERROR:
		case SERVER_ERROR:
			// improve response
			String code = convertStreamToString(responseContext.getEntityStream());
			StatusType si = responseContext.getStatusInfo();
			
			responseContext.setStatusInfo(new StatusType() {
				
				@Override
				public int getStatusCode() {
					return si.getStatusCode();
				}
				
				@Override
				public String getReasonPhrase() {
					return si.getReasonPhrase() + " " + code;
				}
				
				@Override
				public Family getFamily() {
					return si.getFamily();
				}
			});
			
		default:
			// do nothing.
		}
	}

    @SuppressWarnings("resource") // jaxrs responsible for closing
	static String convertStreamToString(InputStream is) {
		java.util.Scanner s = new Scanner(is, "UTF-8").useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}
}
