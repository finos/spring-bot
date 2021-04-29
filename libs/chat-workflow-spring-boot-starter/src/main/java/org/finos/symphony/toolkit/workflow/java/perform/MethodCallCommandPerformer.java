package org.finos.symphony.toolkit.workflow.java.perform;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.Action;
import org.finos.symphony.toolkit.workflow.CommandPerformer;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.form.Button;
import org.finos.symphony.toolkit.workflow.form.Button.Type;
import org.finos.symphony.toolkit.workflow.form.ButtonList;
import org.finos.symphony.toolkit.workflow.java.resolvers.WorkflowResolvers;
import org.finos.symphony.toolkit.workflow.java.resolvers.WorkflowResolversFactory;
import org.finos.symphony.toolkit.workflow.java.workflow.ClassBasedWorkflow;
import org.finos.symphony.toolkit.workflow.response.ErrorResponse;
import org.finos.symphony.toolkit.workflow.response.FormResponse;
import org.finos.symphony.toolkit.workflow.response.Response;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.EntityJsonConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Implements command performer by using method calls on workflow classes.
 * 
 * @author moffrob
 *
 */
public class MethodCallCommandPerformer implements CommandPerformer {
	
	private static final Logger LOG = LoggerFactory.getLogger(MethodCallCommandPerformer.class);

	
	private WorkflowResolversFactory wrf;
	
	public MethodCallCommandPerformer(WorkflowResolversFactory wrf) {
		super();
		this.wrf = wrf;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Response> applyCommand(String commandName, Action originatingAction) {
		ClassBasedWorkflow wf = originatingAction.getWorkflow() instanceof ClassBasedWorkflow ? 
			(ClassBasedWorkflow) originatingAction.getWorkflow() : null;
		
		if (wf == null) {
			// this instance can only work with ClassBasedWorkflow instances.
			return Collections.emptyList();
		}
		
		Method m = wf.getMethodFor(commandName);
		
		if (m == null) {
			return Collections.emptyList();
		}
		
		Addressable a = originatingAction.getAddressable();
		
		if (!wf.validCommandInAddressable(m, a)) {
			return Collections.singletonList(new ErrorResponse(wf, a, "'"+commandName+"' can't be used in this room"));
		}
		
		WorkflowResolvers wr = buildWorkflowResolvers(originatingAction);
		
		Class<?> c = m.getDeclaringClass();
		Optional<?> o = Optional.empty();
		if (!Modifier.isStatic(m.getModifiers())) {
			// load the current object
			o = wr.resolve(c, a, true);
			
			if ((!o.isPresent()) || (o.get().getClass() != c)) {
				return Collections.singletonList(new ErrorResponse(wf, a, "Couldn't find work for "+commandName));
			}
		}
			
	
		Object[] args = new Object[m.getParameterCount()];
		for (int i = 0; i < args.length; i++) {
			Class<?> cl = m.getParameters()[i].getType();
			Optional<Object> oo = wr.resolve(cl, a, false);
			if (oo.isPresent()) {
				args[i] = oo.get();
			} else {
				// missing parameter
				try {
					return  Collections.singletonList(new FormResponse(wf, a,  new EntityJson(), "Enter "+
						wf.getName(cl), wf.getInstructions(cl), cl.newInstance(), true, 
							ButtonList.of(new Button(commandName+"+0", Type.ACTION, m.getName()))));
				} catch (Exception e) {
					if (cl.isPrimitive()) {
						throw new UnsupportedOperationException("Couldn't identity missing parameters:" + cl.getName(), e);
					}
				} 
			}
		}
		

		try {
			Object out = m.invoke(o.orElse(null), args);
			if (out == null) {
				return Collections.emptyList();
			}
			Class<?> cc = out.getClass();
			
			if (Response.class.isAssignableFrom(cc)) {
				return  Collections.singletonList((Response) out);
			} else if (listOfResponses(m)) {
				return (List<Response>) out;
			} else {
				EntityJson ej = EntityJsonConverter.newWorkflow(out);
				return  Collections.singletonList(
					new FormResponse(wf, a, ej, 
						wf.getName(cc), 
						wf.getInstructions(cc), out, false, wf.gatherButtons(out, a)));
			}
			
		} catch (Throwable throwable) {
			LOG.error("Couldn't perform command: ", throwable);
			String exceptionMessage = Optional.ofNullable(throwable.getMessage())
					.orElse(Optional.ofNullable(throwable.getCause())
							.map(cause -> Optional.ofNullable(cause.getMessage())
									.orElse("Exception thrown with no message"))
							.orElse("Exception thrown with no exception details"));
			return Collections.singletonList(new ErrorResponse(wf, a, exceptionMessage));
		}
	}
	
	private WorkflowResolvers buildWorkflowResolvers(Action originatingAction) {
		return wrf.createResolvers(originatingAction);
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
