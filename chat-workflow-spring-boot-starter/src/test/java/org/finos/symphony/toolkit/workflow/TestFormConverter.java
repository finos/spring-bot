package org.finos.symphony.toolkit.workflow;

import java.util.Map;

import org.finos.symphony.toolkit.workflow.content.Author;
import org.finos.symphony.toolkit.workflow.content.UserDef;
import org.finos.symphony.toolkit.workflow.fixture.TestOb4;
import org.finos.symphony.toolkit.workflow.fixture.TestObject;
import org.finos.symphony.toolkit.workflow.fixture.TestObjects;
import org.finos.symphony.toolkit.workflow.sources.symphony.elements.FormConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.room.SymphonyRooms;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.fasterxml.jackson.databind.ObjectMapper;

public class TestFormConverter extends AbstractMockSymphonyTest{
	
	@MockBean
	SymphonyRooms rooms;
	
	private FormConverter fc;
	private ObjectMapper om = new ObjectMapper();

	@SuppressWarnings("unchecked")
	@Test
	public void testSimpleForm() throws Exception {
		Object o = om.readValue("{\"action\":\"add+0\",\"isin.\":\"fd3442\",\"bidAxed.\":\"true\",\"askAxed.\":\"true\",\"creator.\":\"tr\",\"bidQty.\":\"32432\",\"askQty.\":\"234\"}", Map.class);
		TestObject to = (TestObject) fc.convert((Map<String, Object>) o, TestObject.class.getCanonicalName());
		Assertions.assertEquals("fd3442", to.getIsin());
		Assertions.assertEquals("tr", to.getCreator());
		Assertions.assertEquals(32432, to.getBidQty());
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testFormContainingList() throws Exception {
		Object o = om.readValue("{\"action\":\"add+0\",\"items.[0].isin.\":\"fd3442\",\"items.[0].bidAxed.\":\"true\",\"items.[0].askAxed.\":\"true\",\"items.[0].creator.\":\"tr\",\"items.[0].bidQty.\":\"32432\",\"items.[0].askQty.\":\"234\"}", Map.class);
		TestObjects to = (TestObjects) fc.convert((Map<String, Object>) o, TestObjects.class.getCanonicalName());
		Assertions.assertEquals("fd3442", to.getItems().get(0).getIsin());
		Assertions.assertEquals("tr", to.getItems().get(0).getCreator());
		Assertions.assertEquals(32432, to.getItems().get(0).getBidQty());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testUsersAndAuthors() throws Exception {
		before();
		Object o = om.readValue("{\"action\": \"ob4+0\", \"c.\": \"B\", \"b.\": true, \"someUser.\": [345315370602462]}", Map.class);
		Author.CURRENT_AUTHOR.set(new UserDef("123", "johnny bignose", "jb@nose.com"));
		TestOb4 to = (TestOb4) fc.convert((Map<String, Object>) o, TestOb4.class.getCanonicalName());
		Assertions.assertEquals(Author.CURRENT_AUTHOR.get(), to.getA());
		Assertions.assertTrue(to.isB());
		Assertions.assertEquals("345315370602462", to.getSomeUser().getId());
		Assertions.assertEquals(TestOb4.Choice.B, to.getC());
		
		
	}

	@BeforeEach
	public void before() {
		Mockito.when(rooms.loadUserById(Mockito.eq(345315370602462l))).thenReturn(new UserDef("345315370602462", "Some Guy", "sg@example.com"));
		fc = new FormConverter(rooms);
	}
	
}
