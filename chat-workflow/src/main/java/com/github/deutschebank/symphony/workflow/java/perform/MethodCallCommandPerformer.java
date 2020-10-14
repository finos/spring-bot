package com.github.deutschebank.symphony.workflow.java.perform;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.deutschebank.symphony.json.EntityJson;
import com.github.deutschebank.symphony.workflow.Action;
import com.github.deutschebank.symphony.workflow.CommandPerformer;
import com.github.deutschebank.symphony.workflow.content.Addressable;
import com.github.deutschebank.symphony.workflow.form.Button;
import com.github.deutschebank.symphony.workflow.form.Button.Type;
import com.github.deutschebank.symphony.workflow.form.ButtonList;
import com.github.deutschebank.symphony.workflow.java.resolvers.WorkflowResolvers;
import com.github.deutschebank.symphony.workflow.java.resolvers.WorkflowResolversFactory;
import com.github.deutschebank.symphony.workflow.java.workflow.ClassBasedWorkflow;
import com.github.deutschebank.symphony.workflow.response.ErrorResponse;
import com.github.deutschebank.symphony.workflow.response.FormResponse;
import com.github.deutschebank.symphony.workflow.response.Response;
import com.github.deutschebank.symphony.workflow.sources.symphony.handlers.EntityJsonConverter;

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
			o = wr.resolve(c, a);
			
			if ((!o.isPresent()) || (o.get().getClass() != c)) {
				return Collections.singletonList(new ErrorResponse(wf, a, "Couldn't find work for "+commandName));
			}
		}
			
	
		Object[] args = new Object[m.getParameterCount()];
		for (int i = 0; i < args.length; i++) {
			Class<?> cl = m.getParameters()[i].getType();
			Optional<Object> oo = wr.resolve(cl, a);
			if (oo.isPresent()) {
				args[i] = oo.get();
			} else {
				// missing parameter
				try {
					return  Collections.singletonList(new FormResponse(wf, a,  new EntityJson(), "Enter "+
						wf.getName(cl), wf.getInstructions(cl), cl.newInstance(), true, 
							ButtonList.of(new Button(commandName+"+0", Type.ACTION, m.getName()))));
				} catch (Exception e) {
					throw new UnsupportedOperationException("Couldn't identity missing parameters:" + cl.getName(), e);
				} 
			}
		}
		

		try {
			Object out = m.invoke(o.orElse(null), args);
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
			
		} catch (Exception e) {
			LOG.error("Couldn't perform command: ", e);
			return Collections.singletonList(new ErrorResponse(wf, a, e.getMessage()));
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
