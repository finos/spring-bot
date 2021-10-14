package org.finos.symphony.toolkit.workflow.java.mapping;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

import org.finos.symphony.toolkit.workflow.actions.Action;
import org.finos.symphony.toolkit.workflow.java.converters.ResponseConverters;
import org.finos.symphony.toolkit.workflow.java.resolvers.WorkflowResolvers;
import org.finos.symphony.toolkit.workflow.java.resolvers.WorkflowResolversFactory;
import org.springframework.core.MethodParameter;

/**
 * Implements command performer by using method calls on workflow classes.
 * 
 * @author moffrob
 *
 */
public abstract class AbstractHandlerExecutor implements ChatHandlerExecutor {
	
	private WorkflowResolversFactory wrf;
	private ResponseConverters converters;
	
	public AbstractHandlerExecutor(WorkflowResolversFactory wrf, ResponseConverters converters) {
		super();
		this.wrf = wrf;
		this.converters = converters;
	}
	
	@Override
	public void execute() throws Throwable {
		ChatHandlerMethod hm = getOriginatingMapping().getHandlerMethod();
		Method m = hm.getMethod(); 
		Object o = hm.getBean();
		
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
		
		if (out != null) {
			converters.accept(out, this);
		}
		
	}

	private WorkflowResolvers buildWorkflowResolvers(Action originatingAction) {
		return wrf.createResolvers(this);
	}

}
