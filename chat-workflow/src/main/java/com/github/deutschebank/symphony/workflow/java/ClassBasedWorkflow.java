package com.github.deutschebank.symphony.workflow.java;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.github.deutschebank.symphony.workflow.AbstractWorkflow;
import com.github.deutschebank.symphony.workflow.Workflow;
import com.github.deutschebank.symphony.workflow.content.Addressable;
import com.github.deutschebank.symphony.workflow.content.Content;
import com.github.deutschebank.symphony.workflow.content.Message;
import com.github.deutschebank.symphony.workflow.content.Paragraph;
import com.github.deutschebank.symphony.workflow.content.PastedTable;
import com.github.deutschebank.symphony.workflow.content.Room;
import com.github.deutschebank.symphony.workflow.content.Tag;
import com.github.deutschebank.symphony.workflow.content.User;
import com.github.deutschebank.symphony.workflow.content.Word;
import com.github.deutschebank.symphony.workflow.form.Button;
import com.github.deutschebank.symphony.workflow.form.Button.Type;
import com.github.deutschebank.symphony.workflow.response.ErrorResponse;
import com.github.deutschebank.symphony.workflow.response.FormResponse;
import com.github.deutschebank.symphony.workflow.response.Response;

public class ClassBasedWorkflow extends AbstractWorkflow implements ConfigurableWorkflow {

	public static final String WF_EDIT = "wf-edit";
	private List<Class<?>> workflowClasses = new ArrayList<>();
	private Map<String, Method> methods = new HashMap<>();
	
	public ClassBasedWorkflow(String namespace, List<User> admins, List<Room> keyRooms) {
		super(namespace, admins, keyRooms); 
	}

	@Override
	public Map<String, String> getCommands(Room r) {
		return methods.entrySet().stream()
			.filter(e -> validRoom(e.getValue(), r))
			.collect(Collectors.toMap(e -> e.getKey(), e -> getDescription(e.getValue())));
	}
	
	public void addClass(Class<?> e) {
		matchingMethods(e, null).stream()
			.forEach(m -> {
				if (methods.containsKey(m.getName())) {
					throw new UnsupportedOperationException("Methods clash: "+m+" with "+methods.get(m.getName()));					
				} 
				methods.put(m.getName(), m);
			});
		workflowClasses.add(e);
	}
	
	private List<Method> matchingMethods(Class<?> c, Room r) {
		if ((c == null) || (c == Object.class)) {
			return Collections.emptyList();
		}
		
		List<Method> out = new ArrayList<>();
		
		for (Method m : c.getDeclaredMethods()) {
			if (m.getAnnotation(Exposed.class) != null) {
				if (checkParameters(m)) {
					throw new UnsupportedOperationException("Methods annotated with @Exposed must have 1 or 0 parameters (excluding room, workflow, user etc): "+m.getClass()+"::"+m.getName());
				}
				
				if (validRoom(m, r)) {
					out.add(m);
				}
			}
		}
		
		out.addAll(matchingMethods(c.getSuperclass(), r));
		return out;
	}
	
	private long countParameters(Method m) {
		return Arrays.stream(m.getParameters())
				.filter(p -> !isWorkflowParameter(p))
				.count();
	}

	private boolean checkParameters(Method m) {
		long count = countParameters(m);
		
		return count > 1;
	}

	private boolean isWorkflowParameter(Parameter p) {
		Class<?> c = p.getType();
		if (Workflow.class.isAssignableFrom(c)) {
			return true;
		} else if (Addressable.class.isAssignableFrom(c)) {
			return true;
		}
		
		return false;
	}

	@Override
	public List<Response> applyCommand(User u, Room r, String commandName, Object argument, Message msg) {
		Method m = methods.get(commandName);
		
		if (m == null) {
			return null;
		}
		
		if (!validRoom(m, r)) {
			return Collections.singletonList(new ErrorResponse(this, r, "'"+commandName+"' can't be used in this room"));
		}
		
		Class<?> c = m.getDeclaringClass();
		Optional<?> o = Optional.empty();
		if (!Modifier.isStatic(m.getModifiers())) {
			// load the current object
			o = getHistoryApi().getLastFromHistory(c, r);
			
			if ((!o.isPresent()) || (o.get().getClass() != c)) {
				return Collections.singletonList(new ErrorResponse(this, r, "Couldn't find work for "+commandName));
			}
		}
			
		Object[] args = new Object[m.getParameterCount()];
		Map<Class<?>, Deque<Object>> parameterBuckets = setupParameterBuckets(u, r, msg);
		
		for (int i = 0; i < args.length; i++) {
			Class<?> cl = m.getParameters()[i].getType();
			if (parameterBuckets.containsKey(cl)) {
				args[i] = parameterBuckets.get(cl).pop();
			} else if (argument != null) {
				args[i] = argument;
			} else {
				// missing parameter
				return  Collections.singletonList(new FormResponse(this, r,  null, "Enter "+getName(cl), getInstructions(cl), cl, true, 
					Collections.singletonList(new Button(commandName+"+0", Type.ACTION, m.getName()))));
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
				return  Collections.singletonList(new FormResponse(this, r, out, getName(cc), getInstructions(cc), out, false, gatherButtons(out, r)));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return Collections.singletonList(new ErrorResponse(this, r, e.getCause().getMessage()));
		}
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

	private Map<Class<?>, Deque<Object>> setupParameterBuckets(User u, Room r, Message m) {
		Map<Class<?>, Deque<Object>> out = new HashMap<>();
		for (Class<?> class1 : new Class[] { Room.class, User.class, Workflow.class, Message.class, Paragraph.class, PastedTable.class, Word.class, Tag.class}) {
			Deque<Object> l = new LinkedList<Object>();
			
			if (class1 == Workflow.class) {
				l.add(this);
			} else if (class1 == User.class) {
				l.add(u);
			} else if (class1 == Room.class) {
				l.add(r);
			} else if ((class1 == Message.class) && (m!=null)) {
				l.add(m);
			}
			
			if ((Content.class.isAssignableFrom(class1)) && (m!=null)) {
				l.addAll(m.only((Class<Content>) class1));
			}
			
			
			out.put(class1, l);
		}
		
		return out;
	}

	@Override
	public List<Button> gatherButtons(Object out, Room r) {
		List<Button> buttons = new ArrayList<>();
		if (out.getClass().isAnnotationPresent(Work.class)) {
			Work w = out.getClass().getAnnotation(Work.class);
			if (w.editable()) {
				buttons.add(new Button(WF_EDIT, Type.ACTION, "Edit"));
			}
		}
		
		List<Method> cms = matchingMethods(out.getClass(), r);
		if (cms != null) {
			for (Method method : cms) {
				if (!Modifier.isStatic(method.getModifiers())) {
					if(countParameters(method) == 1) {
						buttons.add(new Button(method.getName()+"+1", Type.ACTION, method.getName()));
					} else {
						buttons.add(new Button(method.getName(), Type.ACTION, method.getName()));
					}
				}
			}
		}
		
		return buttons;
	}

	public static String getName(Class<?> c) {
		Work w = c.getAnnotation(Work.class);
		if (w != null) {
			return w.name();
		} else {
			return c.getName();
		}
	}
	
	public static String getInstructions(Class<?> c) {
		Work w = c.getAnnotation(Work.class);
		return w.instructions();
	}
	
	public static String getDescription(Method m) {
		Exposed e = m.getAnnotation(Exposed.class);
		return e.description();
	}
	
	public static boolean validRoom(Method value, Room r) {
		if (r == null) {
			return true;
		} else {
			Exposed e = value.getAnnotation(Exposed.class);
			if (e.rooms().length == 0) {
				return true;
			} else {
				for (int i = 0; i < e.rooms().length; i++) {
					if (e.rooms()[i].equals(r.getRoomName())) {
						return true;
					}
				}
			}
			
			return false;
		}
	}

	@Override
	public List<Class<?>> getDataTypes() {
		return workflowClasses;
	}
	
}
