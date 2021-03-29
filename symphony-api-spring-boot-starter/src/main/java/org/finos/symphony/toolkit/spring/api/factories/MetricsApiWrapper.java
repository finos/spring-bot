package org.finos.symphony.toolkit.spring.api.factories;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.finos.symphony.toolkit.spring.api.properties.PodProperties;

import com.symphony.api.bindings.ApiWrapper;
import com.symphony.api.id.SymphonyIdentity;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Timer.Sample;

public class MetricsApiWrapper implements ApiWrapper {
	
	private PodProperties pp;
	private String commonName;
	private String host;
	private MeterRegistry mr;
	
	public MetricsApiWrapper(MeterRegistry mr, PodProperties pp, String commonName, String host) {
		this.pp = pp;
		this.commonName = commonName;
		this.host = host;
		this.mr = mr;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <X> X wrap(Class<X> c, X api) {
		InvocationHandler internal = Proxy.getInvocationHandler(api);
		ClassLoader cl = this.getClass().getClassLoader();
		
		return (X) Proxy.newProxyInstance(cl, new Class[] { c }, new InvocationHandler() {
			
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				return mr == null ? internalInvoke(proxy, method, args) : timerInvoke(proxy, method, args);
			}
			
			public Object timerInvoke(Object proxy, Method method, Object[] args) throws Throwable {
				Timer t = mr.timer("symphony.api-call", "pod", safePodId(), "id", safeCommonName(), "method", method.getName(), "url", host);
				Sample s = Timer.start();
				Object out = internalInvoke(proxy, method, args);
				s.stop(t);
				return out;
			}

			private String safeCommonName() {
				return commonName  == null ? "" : commonName;
			}

			private String safePodId() {
				return pp.getId() == null ? "" : pp.getId();
			}
			
			public Object internalInvoke(Object proxy, Method method, Object[] args) throws Throwable {
				Object out = internal.invoke(proxy, method, args);
				return out;
			}
		});
	}

}