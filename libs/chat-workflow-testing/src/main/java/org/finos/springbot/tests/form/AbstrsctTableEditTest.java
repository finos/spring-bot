package org.finos.springbot.tests.form;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import org.finos.springbot.symphony.content.SymphonyRoom;
import org.finos.springbot.symphony.content.SymphonyUser;
import org.finos.springbot.symphony.json.EntityJsonConverter;
import org.finos.springbot.workflow.actions.FormAction;
import org.finos.springbot.workflow.actions.form.TableAddRow;
import org.finos.springbot.workflow.actions.form.TableDeleteRows;
import org.finos.springbot.workflow.actions.form.TableEditRow;
import org.finos.springbot.workflow.content.Chat;
import org.finos.springbot.workflow.content.User;
import org.finos.springbot.workflow.form.FormSubmission;
import org.finos.springbot.workflow.response.WorkResponse;
import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.fixture.TestObject;
import org.finos.symphony.toolkit.workflow.fixture.TestObjects;
import org.finos.symphony.toolkit.workflow.fixture.TestPrimitives;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;


public class AbstrsctTableEditTest {
	

	public static final User u = new SymphonyUser(123l, "Testy McTestFace", "tmt@example.com");
	public static final Chat room = new SymphonyRoom("Test Room", "abc123");

    private TestObjects to;

    @Autowired
    TableEditRow editRow;

    @Autowired
    TableDeleteRows deleteRows;

    @Autowired
    TableAddRow addRows;
    
    @Autowired
    EntityJsonConverter ejc;


	public static TestObjects createTestObjects() {
		return new TestObjects(
			new ArrayList<>(Arrays.asList(
				new TestObject("83274239874", true, true, "rob@example.com", 234786, 2138),
				new TestObject("AUD274239874", true, false, "gregb@example.com", 2386, new BigDecimal("234823498.573")))));
	}
    
    @BeforeEach
    public void setup() {
        to = createTestObjects();
    }

    @Test
    public void testAddRow() throws Exception {
    	
		// ok, make changes and post back
		TestObject newTo = new TestObject();
		newTo.setCreator("newb@thing.com");
		newTo.setIsin("isiny");
		newTo.setBidAxed(true);
		newTo.setBidQty(324);
  
        
        
        add("testAddRow", to, newTo, "items.");
    }
    
    protected void add(String testStem, Object toChange, Object newRow, String spel) throws Exception {
    	EntityJson ej = new EntityJson();
    	ej.put(WorkResponse.OBJECT_KEY, toChange);
    	
        FormAction ea = new FormAction(room, u, toChange, spel + TableAddRow.ACTION_SUFFIX, ej);
        addRows.accept(ea);
        
        // should get a new form back.
        String jsonStr = testTemplating("abc123", testStem + "1.ml", testStem + "1.json");
        
        EntityJson json = ejc.readValue(jsonStr);
        
        ea = new FormAction(room, u, newRow, spel + TableAddRow.DO_SUFFIX, json);
        Mockito.reset(messagesApi);
        addRows.accept(ea);
        
        testTemplating("abc123", testStem + "2.ml", testStem + "2.json");
        
    }

    @Test
    public void testAddRowOfListOfPrimitiveTypeInteger() throws Exception {
    	TestPrimitives entity = new TestPrimitives();
        entity.getIntegerList().add(1);
        Integer newTo = 4;
        add("testAddRowOfListOfPrimitiveTypeInteger", entity, newTo, "integerList.");
    }

    @Test
    public void testAddRowOfListOfPrimitiveTypeNumber() throws Exception {
        TestPrimitives entity = new TestPrimitives();
        entity.getNumberList().add(1);
        Number newTo = 4;
        add("testAddRowOfListOfPrimitiveTypeNumber", entity, newTo, "numberList.");
    }
    
    @Test
    public void testAddRowOfListOfPrimitiveTypeString() throws Exception {
        TestPrimitives entity = new TestPrimitives();
        entity.getNames().add("Amsidh");
        String newTo = "Suresh";

        add("testAddRowOfListOfPrimitiveTypeString", entity, newTo, "names.");
    }
    
    
    protected void editRow(String testStem, Object toChange, Object newRow, String spel) throws Exception {
    	EntityJson ej = new EntityJson();
    	ej.put(WorkResponse.OBJECT_KEY, toChange);
    	
        FormAction ea = new FormAction(room, u, toChange, spel + TableEditRow.EDIT_SUFFIX, ej);
        editRow.accept(ea);
        
        // should get a new form back.
        String jsonStr = testTemplating("abc123", testStem + "1.ml", testStem + "1.json");
        
        EntityJson json = ejc.readValue(jsonStr);
        
        ea = new FormAction(room, u, newRow, spel + TableEditRow.UPDATE_SUFFIX, json);
        Mockito.reset(messagesApi);
        editRow.accept(ea);
        
        testTemplating("abc123", testStem + "2.ml", testStem + "2.json");
        
    }
    
    @Test
    public void testEditRow() throws Exception {
        TestObject newTo = new TestObject();
        newTo.setCreator("newb@thing.com");
        newTo.setIsin("isiny");
        newTo.setBidAxed(true);
        newTo.setBidQty(324);
        editRow("testEditRow", to, newTo, "items.[0].");
    }

    @Test
    public void testDeleteRows() throws Exception {
    	EntityJson ej = new EntityJson();
    	ej.put(WorkResponse.OBJECT_KEY, to);
    	
        Map<String, Object> selects = Collections.singletonMap("items", Collections.singletonList(Collections.singletonMap("selected", "true")));
        FormSubmission uc = new FormSubmission(TestObjects.class.getCanonicalName(), selects);
        FormAction ea = new FormAction(room, u, uc, "items." + TableDeleteRows.ACTION_SUFFIX, ej);
        
        deleteRows.accept(ea);
        
        testTemplating("abc123", "testDeleteRows.ml", "testDeleteRows.json");
    }

    @Test
    public void testEditRowFromListOfPrimitiveTypeString() throws Exception {
        TestPrimitives entity = new TestPrimitives();
        entity.getNames().add("Amsidh");
        entity.getNames().add("Suresh");
        editRow("testEditRowFromListOfPrimitiveTypeString", entity, "Rob", "names.[0].");
    }



}
