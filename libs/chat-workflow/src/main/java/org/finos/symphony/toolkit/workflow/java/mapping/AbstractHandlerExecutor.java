package org.finos.symphony.toolkit.workflow.java.mapping;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.finos.symphony.toolkit.workflow.actions.Action;
import org.finos.symphony.toolkit.workflow.java.converters.ResponseConverter;
import org.finos.symphony.toolkit.workflow.java.resolvers.WorkflowResolvers;
import org.finos.symphony.toolkit.workflow.java.resolvers.WorkflowResolversFactory;
import org.finos.symphony.toolkit.workflow.response.Response;
import org.finos.symphony.toolkit.workflow.response.handlers.ResponseHandlers;
import org.springframework.core.MethodParameter;

/**
 * Implements command performer by using method calls on workflow classes.
 * 
 * @author moffrob
 *
 */
public abstract class AbstractHandlerExecutor implements ChatHandlerExecutor {
	
	private WorkflowResolversFactory wrf;
	private ResponseHandlers rh;
	private List<ResponseConverter> converters;
	
	public AbstractHandlerExecutor(WorkflowResolversFactory wrf, ResponseHandlers  rh, List<ResponseConverter> converters) {
		super();
		this.wrf = wrf;
		this.rh = rh;
		this.converters = converters;
	}
	
	@Override
	public void execute() throws Throwable {
		ChatHandlerMethod hm = getOriginatingMapping().getHandlerMethod();
		Method m = hm.getMethod(); 
		Object o = hm.getBean();
		
		Action.CURRENT_ACTION.set(action());
		
		WorkflowResolvers wr = buildWorkflowResolvers(action());
		Object[] args = new Object[hm.getMethodParameters().length];
		for (int i = 0; i < args.length; i++) {
			MethodParameter mp = hm.getMethodParameters()[i];
			Optional<Object> oo = wr.resolve(mp); 
			if (oo.isPresent()) {
				args[i] = oo.get();
			} 
		}

		Object out;

		try {
			out = m.invoke(o, args);
		} catch (InvocationTargetException ite) {
			throw ite.getTargetException();
		}
		
		if (out instanceof Response) {
			rh.accept((Response) out);
		} else if (out instanceof Collection) {
			for (Object object : (List<?>) out) {
				if (object instanceof Response) {
					rh.accept((Response) object);
				} else {
					Response r = convert(object);
					if (r != null) {
						rh.accept(r);
					}
				}
			}
		} else {
			Response r = convert(out);
			if (r != null) {
				rh.accept(r);
			}
		}
	}
	
	private Response convert(Object object) {
		for (ResponseConverter responseConverter : converters) {
			if (responseConverter.canConvert(object)) {
				return responseConverter.convert(object, this);
			}
		}
		
		return null;
	}

	private WorkflowResolvers buildWorkflowResolvers(Action originatingAction) {
		return wrf.createResolvers(this);
	}

}
