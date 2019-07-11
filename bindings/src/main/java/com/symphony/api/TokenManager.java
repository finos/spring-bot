package com.symphony.api;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.Supplier;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.NotAuthorizedException;

import com.symphony.api.model.Token;

/**
 * This sets the token parameters automatically so the user doesn't have to
 * worry about it.  Also handles 401 errors when tokens expire, and it has to retry.
 * 
 * Ideally you should have a single token manager per symphony identity to minimize 
 * the number of tokens.
 * 
 * @author Rob Moffat
 */
public class TokenManager implements ApiWrapper {

	public static final String SESSION_TOKEN = "sessionToken";
	public static final String KEY_MANAGER_TOKEN = "keyManagerToken";

	private Supplier<Token> sessionAuthApi;
	private Supplier<Token> keyAuthApi;

	private Token sessionToken;
	private Token keyManagerToken;

	public TokenManager(Supplier<Token> sessionAuthApi, Supplier<Token> keyAuthApi) {
		this.sessionAuthApi = sessionAuthApi;
		this.keyAuthApi = keyAuthApi;
	}

	public Token getSessionToken() {
		return sessionToken;
	}

	public Token getKeyManagerToken() {
		return keyManagerToken;
	}

	public void setSessionToken(Token sessionToken) {
		this.sessionToken = sessionToken;
	}

	public void setKeyManagerToken(Token keyManagerToken) {
		this.keyManagerToken = keyManagerToken;
	}

	public String keyManagerToken() {
		if (keyManagerToken == null) {
			keyManagerToken = keyAuthApi.get();
		}

		return keyManagerToken.getToken();
	}

	public String sessionToken() {
		if (sessionToken == null) {
			sessionToken = sessionAuthApi.get();
		}

		return sessionToken.getToken();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <X> X wrap(Class<X> c, X api) {
		
		InvocationHandler internal = Proxy.getInvocationHandler(api);
		ClassLoader cl = internal.getClass().getClassLoader();
		
		return (X) Proxy.newProxyInstance(cl, new Class[] { c }, new InvocationHandler() {
			
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				try {
					fillTokens(method, args, false);
					return internal.invoke(proxy, method, args);
				} catch (NotAuthorizedException e) {
					// in this case, we want to retry with some new tokens
					setSessionToken(null);
					setKeyManagerToken(null);
					fillTokens(method, args, true);
					return internal.invoke(proxy, method, args);
				}
			}

			private void fillTokens(Method method, Object[] args, boolean refill) {
				Annotation[][] anns = method.getParameterAnnotations();
				for (int i = 0; i < args.length; i++) {
					if ((args[i] == null) || (refill)) {
						
						if (hasHeaderParam(SESSION_TOKEN, anns[i])) {
							args[i] = sessionToken();
						}
						
						if (hasHeaderParam(KEY_MANAGER_TOKEN, anns[i])) {
							args[i] = keyManagerToken();
						}
					}
					
				}
			}

			private boolean hasHeaderParam(String paramName, Annotation[] annotations) {
				for (Annotation a : annotations) {
					if (a.annotationType() == HeaderParam.class) {
						HeaderParam hp = (HeaderParam) a;
						return (paramName.equals(hp.value()));
					}
				}
				
				return false;
			}
		});
	}
}
