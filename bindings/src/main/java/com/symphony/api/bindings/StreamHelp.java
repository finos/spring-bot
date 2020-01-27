package com.symphony.api.bindings;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.PathParam;

/**
 * Converts non-safe stream IDs into URL-safe ones for transmitting on URLs.
 * @author Rob Moffat
 *
 */
public class StreamHelp implements ApiWrapper {

	public static String safeStreamId(String in) {
		if (in == null) {
			return null;
		}
		return in.replace("/", "_").replace("+", "-").replace("=", "");
	}

	@Override
	@SuppressWarnings("unchecked")
	public <X> X wrap(Class<X> c, X api) {
		
		InvocationHandler internal = Proxy.getInvocationHandler(api);
		ClassLoader cl = this.getClass().getClassLoader();
		
		return (X) Proxy.newProxyInstance(cl, new Class[] { c }, new InvocationHandler() {
			
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				checkStreamIds(method, args, false);
				return internal.invoke(proxy, method, args);
			}

			private void checkStreamIds(Method method, Object[] args, boolean refill) {
				Annotation[][] anns = method.getParameterAnnotations();
				if (args != null) {
					for (int i = 0; i < args.length; i++) {
						if (isStreamParam(anns[i])) {
							args[i] = safeStreamId((String) args[i]);
						}
					}
				}
			}

			private boolean isStreamParam(Annotation[] annotations) {
				for (Annotation a : annotations) {
					if (a.annotationType() == PathParam.class) {
						HeaderParam hp = (HeaderParam) a;
						return ("id".equals(hp.value()));
					}
				}
				
				return false;
			}
		});
	}
}
