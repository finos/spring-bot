package org.finos.springbot.workflow.templating;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.finos.springbot.workflow.annotations.RequiresChatList;
import org.finos.springbot.workflow.annotations.WorkMode;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.content.Chat;
import org.finos.springbot.workflow.content.User;
import org.finos.springbot.workflow.response.WorkResponse;
import org.finos.springbot.workflow.templating.fixture.BooleanWork;
import org.finos.springbot.workflow.templating.fixture.ChatWork;
import org.finos.springbot.workflow.templating.fixture.CollectionBeanWork;
import org.finos.springbot.workflow.templating.fixture.CollectionBeanWork.Inner;
import org.finos.springbot.workflow.templating.fixture.CollectionSingleWork;
import org.finos.springbot.workflow.templating.fixture.DisplayWork;
import org.finos.springbot.workflow.templating.fixture.DropdownWork;
import org.finos.springbot.workflow.templating.fixture.EnumWork;
import org.finos.springbot.workflow.templating.fixture.EnumWork.TrafficLights;
import org.finos.springbot.workflow.templating.fixture.IntegerWork;
import org.finos.springbot.workflow.templating.fixture.NestedWork;
import org.finos.springbot.workflow.templating.fixture.StringWork;
import org.finos.springbot.workflow.templating.fixture.TimeWork;
import org.finos.springbot.workflow.templating.fixture.UserWork;
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
	
	private WorkResponse createChatWorkResponse(WorkMode wm) {
		ChatWork out = new ChatWork();
		out.setS(getChat());

		List<Chat> bigList = createSomeChats(10);
		
		Map<String, Object> data = new HashMap<>();
		data.put(RequiresChatList.CHAT_LIST_KEY, bigList);
		data.put(WorkResponse.OBJECT_KEY, out);
		
		return new WorkResponse(getTo(), data, null, wm, ChatWork.class);
	}
	
	@Test
	public void testChatWorkView() {
		testTemplating(createChatWorkResponse(WorkMode.VIEW), "ChatWorkView");
	}
	
	@Test
	public void testChatWorkEdit() {
		testTemplating(createChatWorkResponse(WorkMode.EDIT), "ChatWorkEdit");
	}
	
	private CollectionBeanWork createCollectionBean() {
		CollectionBeanWork out = new CollectionBeanWork();
		Inner inner1 = new CollectionBeanWork.Inner();
		inner1.setS("first");
		inner1.setB(true);
		Inner inner2 = new CollectionBeanWork.Inner();
		inner2.setS("second");		
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
	
	@Test
	public void testEnumWorkView() {
		EnumWork ew = new EnumWork();
		ew.setS(TrafficLights.RED);
		testTemplating(new WorkResponse(getTo(), ew, WorkMode.VIEW), "EnumWorkView");
	}
	
	@Test
	public void testEnumWorkEdit() {
		EnumWork ew = new EnumWork();
		ew.setS(TrafficLights.GREEN);
		testTemplating(new WorkResponse(getTo(), ew, WorkMode.EDIT), "EnumWorkEdit");
	}
	
	@Test
	public void testIntegerWorkView() {
		IntegerWork iw = new IntegerWork();
		iw.setS(45);
		testTemplating(new WorkResponse(getTo(), iw, WorkMode.VIEW), "IntegerWorkView");
	}
	
	
	@Test
	public void testIntegerWorkEdit() {
		IntegerWork iw = new IntegerWork();
		iw.setS(45);
		testTemplating(new WorkResponse(getTo(), iw, WorkMode.EDIT), "IntegerWorkEdit");
	}
	
	@Test
	public void testNestedWorkView() {
		NestedWork iw = createNestedWork();
		testTemplating(new WorkResponse(getTo(), iw, WorkMode.VIEW), "NestedWorkView");
	}
	
	@Test
	public void testNestedWorkEdit() {
		NestedWork iw = createNestedWork();
		testTemplating(new WorkResponse(getTo(), iw, WorkMode.EDIT), "NestedWorkEdit");
	}

	protected NestedWork createNestedWork() {
		NestedWork iw = new NestedWork();
		NestedWork.Inner a = new NestedWork.Inner();
		NestedWork.Inner b = new NestedWork.Inner();
		a.setS("First");
		b.setS("Second");
		iw.setA(a);
		iw.setB(b);
		return iw;
	}
	
	@Test
	public void testStringWorkView() {
		StringWork ew = new StringWork();
		ew.setS("Some string");
		testTemplating(new WorkResponse(getTo(), ew, WorkMode.VIEW), "StringWorkView");
	}
	
	@Test
	public void testStringWorkEdit() {
		StringWork ew = new StringWork();
		ew.setS("Some too-long string");
		testTemplating(new WorkResponse(getTo(), ew, WorkMode.EDIT), "StringWorkEdit");
	}

	@Test
	public void testTimeWorkView() {
		TimeWork ew = createTimeWork();
		testTemplating(new WorkResponse(getTo(), ew, WorkMode.VIEW), "TimeWorkView");
	}
	
	@Test
	public void testTimeWorkEdit() {
		TimeWork ew = createTimeWork();
		testTemplating(new WorkResponse(getTo(), ew, WorkMode.EDIT), "TimeWorkEdit");
	}

	protected TimeWork createTimeWork() {
		TimeWork ew = new TimeWork();
		ew.setI(Instant.parse("2000-01-01T14:44:44.00Z"));
		ew.setLd(LocalDate.of(2000, 1, 1));
		ew.setLdt(LocalDateTime.of(2000, 1, 1, 11, 11));
		ew.setLt(LocalTime.of(13, 13));
		ew.setZdt(ZonedDateTime.of(LocalDateTime.of(2001, 5, 5, 5, 5), ZoneId.of("Europe/London")));
		ew.setZid(ZoneId.of("America/New_York"));
		return ew;
	}
	
	protected abstract List<User> createSomeUsers(int count);
	
	protected abstract List<Chat> createSomeChats(int count);

	private WorkResponse createUserWorkResponse(WorkMode wm) {
		UserWork out = new UserWork();
		out.setS(getUser());

		List<User> bigList = createSomeUsers(5);
		List<User> smallList = createSomeUsers(3);
		
		Map<String, Object> data = new HashMap<>();
		data.put("biglist", bigList);
		data.put("smalllist", smallList);
		data.put(WorkResponse.OBJECT_KEY, out);
		
		return new WorkResponse(getTo(), data, null, wm, UserWork.class);
	}
	
	@Test
	public void testUserWorkView() {
		testTemplating(createUserWorkResponse(WorkMode.VIEW), "UserWorkView");
	}
	
	@Test
	public void testUserWorkEdit() {
		testTemplating(createUserWorkResponse(WorkMode.EDIT), "UserWorkEdit");
	}

	protected abstract void testTemplating(WorkResponse workResponse, String testName);

	

}
