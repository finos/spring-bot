package org.finos.springbot.workflow.templating;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.finos.springbot.workflow.annotations.WorkMode;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.content.Chat;
import org.finos.springbot.workflow.content.User;
import org.finos.springbot.workflow.response.WorkResponse;
import org.finos.springbot.workflow.templating.fixture.BooleanWork;
import org.finos.springbot.workflow.templating.fixture.ChatWork;
import org.finos.springbot.workflow.templating.fixture.CollectionBeanWork;
import org.finos.springbot.workflow.templating.fixture.CollectionSingleWork;
import org.finos.springbot.workflow.templating.fixture.CollectionBeanWork.Inner;
import org.finos.springbot.workflow.templating.fixture.DisplayWork;
import org.finos.springbot.workflow.templating.fixture.DropdownWork;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class AbstractTemplatingTest {


	protected abstract Addressable getTo();
	
	protected abstract Chat getChat();
	
	protected abstract User getUser();
	
	@Test
	public void testBooleanWorkView() {
		BooleanWork out = new BooleanWork();
		out.setS(true);
		testTemplating(new WorkResponse(getTo(), out, WorkMode.VIEW), "BooleanWorkView");
	}
	
	@Test
	public void testBooleanWorkEdit() {
		BooleanWork out = new BooleanWork();
		out.setS(false);
		testTemplating(new WorkResponse(getTo(), out, WorkMode.EDIT), "BooleanWorkEdit");
	}
	
	@Test
	public void testChatWorkView() {
		ChatWork out = new ChatWork();
		out.setS(getChat());
		testTemplating(new WorkResponse(getTo(), out, WorkMode.VIEW), "ChatWorkView");
	}
	
	@Test
	public void testChatWorkEdit() {
		ChatWork out = new ChatWork();
		out.setS(getChat());
		testTemplating(new WorkResponse(getTo(), out, WorkMode.EDIT), "ChatWorkEdit");
	}
	
	private CollectionBeanWork createCollectionBean() {
		CollectionBeanWork out = new CollectionBeanWork();
		Inner inner1 = new CollectionBeanWork.Inner();
		inner1.setS("first");
		Inner inner2 = new CollectionBeanWork.Inner();
		inner1.setS("second");		
		out.setInners(Arrays.asList(inner1, inner2));
		return out;
	}

	private CollectionSingleWork createCollectionSingle() {
		CollectionSingleWork out = new CollectionSingleWork();
		out.setInts(Arrays.asList(5, 3,2 ));
		out.setStrings(Arrays.asList("do","re","mi"));
		return out;
	}

	@Test
	public void testCollectionBeanWorkView() {
		CollectionBeanWork out = createCollectionBean();
		testTemplating(new WorkResponse(getTo(), out, WorkMode.VIEW), "CollectionBeanView");
	}
	
	@Test
	public void testCollectionBeanWorkEdit() {
		CollectionBeanWork out = createCollectionBean();
		testTemplating(new WorkResponse(getTo(), out, WorkMode.EDIT), "CollectionBeanEdit");
	}
	
	@Test
	public void testCollectionSingleWorkView() {
		CollectionSingleWork out = createCollectionSingle();
		testTemplating(new WorkResponse(getTo(), out, WorkMode.VIEW), "CollectionSingleView");
	}
	
	@Test
	public void testCollectionSingleWorkEdit() {
		CollectionSingleWork out = createCollectionSingle();
		testTemplating(new WorkResponse(getTo(), out, WorkMode.EDIT), "CollectionSingleEdit");
	}
	
	@Test
	public void testDisplayWorkView() {
		DisplayWork out = new DisplayWork();
		out.setInvisible("You didn't see me");
		out.setS("with a crazy name");
		testTemplating(new WorkResponse(getTo(), out, WorkMode.VIEW), "DisplayWorkView");
	}
	
	@Test
	public void testDisplayWorkEdit() {
		DisplayWork out = new DisplayWork();
		out.setInvisible("You still didn't see me");
		out.setS("See me");
		testTemplating(new WorkResponse(getTo(), out, WorkMode.EDIT), "DisplayWorkEdit");
	}
	
	private WorkResponse createDropdownWork(WorkMode wm) {
		DropdownWork out = new DropdownWork();
		out.setS("one");
		JsonNodeFactory jnf = new JsonNodeFactory(true);
		ObjectNode one = jnf.objectNode();
		one.put("key", "one");
		one.put("value", "One Value");
		ObjectNode two = jnf.objectNode();
		two.put("key", "two");
		two.put("value", "Two Value");
		ObjectNode three = jnf.objectNode();
		three.put("key", "three");
		three.put("value", "Three Value");
		ArrayNode an = jnf.arrayNode();
		an.add(one);
		an.add(two);
		an.add(three);
		Map<String, Object> vals = new HashMap<>();
		vals.put("options", an);
		vals.put(WorkResponse.OBJECT_KEY, out);
		return new WorkResponse(getTo(), vals, null, wm, DropdownWork.class);
	}
	
	@Test
	public void testDropdownWorkView() {
		testTemplating(createDropdownWork(WorkMode.VIEW), "DropdownWorkView");
	}
	
	@Test
	public void testDropdownWorkEdit() {
		testTemplating(createDropdownWork(WorkMode.EDIT), "DropdownWorkEdit");
	}
	
	protected abstract void testTemplating(WorkResponse workResponse, String testName);

	

}
