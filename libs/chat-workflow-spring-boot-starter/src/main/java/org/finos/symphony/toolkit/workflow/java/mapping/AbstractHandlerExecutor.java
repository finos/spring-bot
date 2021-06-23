package org.finos.symphony.toolkit.workflow.java.mapping;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Optional;

import org.finos.symphony.toolkit.workflow.Action;
import org.finos.symphony.toolkit.workflow.java.resolvers.WorkflowResolvers;
import org.finos.symphony.toolkit.workflow.java.resolvers.WorkflowResolversFactory;
import org.finos.symphony.toolkit.workflow.response.Response;
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
	
	public AbstractHandlerExecutor(WorkflowResolversFactory wrf) {
		super();
		this.wrf = wrf;
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
//			if (out == null) {
//				return Collections.emptyList();
//			}
//			Class<?> cc = out.getClass();
//			
//			if (Response.class.isAssignableFrom(cc)) {
//				return  Collections.singletonList((Response) out);
//			} else if (listOfResponses(m)) {
//				return (List<Response>) out;
//			} else {
//				EntityJson ej = EntityJsonConverter.newWorkflow(out);
//				return  Collections.singletonList(
//					new FormResponse(wf, a, ej, 
//						wf.getName(cc), 
//						wf.getInstructions(cc), out, false, wf.gatherButtons(out, a)));
//			}
			
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
	
	private WorkflowResolvers buildWorkflowResolvers(Action originatingAction) {
		return wrf.createResolvers(this);
	}

	private boolean listOfResponses(Method m) {
		java.lang.reflect.Type out = m.getGenericReturnType();
		if (out instanceof ParameterizedType) {
			if (Collection.class.isAssignableFrom((Class<?>) ((ParameterizedType) out).getRawType())) {
				if (Response.class.isAssignableFrom((Class<?>) ((ParameterizedType) out).getActualTypeArguments()[0])) {
					return true;
				}
			}
		}
		
		return false;
	}

}
