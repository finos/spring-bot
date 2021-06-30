package org.finos.symphony.toolkit.workflow.java.mapping;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.finos.symphony.toolkit.workflow.Action;
import org.finos.symphony.toolkit.workflow.java.converters.ResponseConverter;
import org.finos.symphony.toolkit.workflow.java.resolvers.WorkflowResolvers;
import org.finos.symphony.toolkit.workflow.java.resolvers.WorkflowResolversFactory;
import org.finos.symphony.toolkit.workflow.response.Response;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.ResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;

/**
 * Implements command performer by using method calls on workflow classes.
 * 
 * @author moffrob
 *
 */
public abstract class AbstractHandlerExecutor implements ChatHandlerExecutor {
	
	private static final Logger LOG = LoggerFactory.getLogger(AbstractHandlerExecutor.class);

	
	private WorkflowResolversFactory wrf;
	private ResponseHandler rh;
	private List<ResponseConverter> converters;
	
	public AbstractHandlerExecutor(WorkflowResolversFactory wrf, ResponseHandler rh, List<ResponseConverter> converters) {
		super();
		this.wrf = wrf;
		this.rh = rh;
		this.converters = converters;
	}
	
	@Override
	public void execute() {
		ChatHandlerMethod hm = getChatHandlerMethod();
		Method m = hm.getMethod(); 
		
		Object o = hm.getBean();
		
		WorkflowResolvers wr = buildWorkflowResolvers(action());
		Object[] args = new Object[hm.getMethodParameters().length];
		for (int i = 0; i < args.length; i++) {
			MethodParameter mp = hm.getMethodParameters()[i];
			Optional<Object> oo = wr.resolve(mp); 
			if (oo.isPresent()) {
				args[i] = oo.get();
			} else {
				// missing parameter
//				try {
//					return  Collections.singletonList(new FormResponse(wf, a,  new EntityJson(), "Enter "+
//						wf.getName(cl), wf.getInstructions(cl), cl.newInstance(), true, 
//							ButtonList.of(new Button(commandName+"+0", Type.ACTION, m.getName()))));
//				} catch (Exception e) {
//					if (cl.isPrimitive()) {
//						throw new UnsupportedOperationException("Couldn't identity missing parameters:" + cl.getName(), e);
//					}
//				} 
			}
		}
		

		try {
			Object out = m.invoke(o, args);
			
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
			
		} catch (Exception exception) {
			LOG.error("Couldn't perform command: ", exception);
			String exceptionMessage = Optional.ofNullable(exception.getMessage())
					.orElse(Optional.ofNullable(exception.getCause())
							.map(cause -> Optional.ofNullable(cause.getMessage())
									.orElse("Exception thrown with no message"))
							.orElse("Exception thrown with no exception details"));
			//return Collections.singletonList(new ErrorResponse(wf, a, exceptionMessage));
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
