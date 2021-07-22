package org.finos.symphony.toolkit.workflow;

import java.util.Collections;
import java.util.Map;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.actions.FormAction;
import org.finos.symphony.toolkit.workflow.actions.form.TableAddRow;
import org.finos.symphony.toolkit.workflow.actions.form.TableDeleteRows;
import org.finos.symphony.toolkit.workflow.actions.form.TableEditRow;
import org.finos.symphony.toolkit.workflow.content.Chat;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.fixture.TestOb6;
import org.finos.symphony.toolkit.workflow.fixture.TestObject;
import org.finos.symphony.toolkit.workflow.fixture.TestObjects;
import org.finos.symphony.toolkit.workflow.fixture.TestWorkflowConfig;
import org.finos.symphony.toolkit.workflow.form.FormSubmission;
import org.finos.symphony.toolkit.workflow.response.WorkResponse;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyRoom;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyUser;
import org.finos.symphony.toolkit.workflow.sources.symphony.json.EntityJsonConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


public class TestTableEdit extends AbstractMockSymphonyTest {
	

	public static final User u = new SymphonyUser(123l, "Testy McTestFace", "tmt@example.com");
	public static final Chat room = new SymphonyRoom("Test Room", "abc123");

    private TestObjects to;

    @Autowired
    TableEditRow editRow;

    @Autowired
    TableDeleteRows deleteRows;

    @Autowired
    TableAddRow addRows;

    @BeforeEach
    public void setup() {
        to = TestWorkflowConfig.createTestObjects();
    }

    @Test
    public void testAddRow() throws Exception {
    	EntityJson ej = new EntityJson();
    	ej.put(WorkResponse.OBJECT_KEY, to);
    	
        FormAction ea = new FormAction(room, u, to, "items." + TableAddRow.ACTION_SUFFIX, ej);
        addRows.accept(ea);
        
        // should get a new form back.
        testTemplating("abc123", "testAddRow1.ml", "testAddRow1.json");
        
        
//        
//        Assertions.assertEquals(TestObject.class, fr.getFormClass());
//        Assertions.assertEquals("New Test Object", fr.getName());
//
//        // ok, make changes and post back
//        TestObject newTo = (TestObject) fr.getFormObject();
//        newTo.setCreator("newb@thing.com");
//        newTo.setIsin("isiny");
//        newTo.setBidAxed(true);
//        newTo.setBidQty(324);
//        ea = new FormAction(wf, room, u, newTo, "items." + TableAddRow.DO_SUFFIX, toWrapper);
//        fr = (WorkResponse) addRows.apply(ea).get(0);
//        TestObjects to = (TestObjects) ejc.readWorkflow(fr.getData());
//
//        Assertions.assertEquals(3, to.getItems().size());
//        Assertions.assertEquals(newTo, to.getItems().get(2));
    }

    @Test
    public void testEditRow() {
        FormAction ea = new FormAction(wf, room, u, null, "items.[0]." + TableEditRow.EDIT_SUFFIX, toWrapper);
        WorkResponse fr = (WorkResponse) editRow.apply(ea).get(0);
        Assertions.assertEquals(TestObject.class, fr.getFormClass());
        Assertions.assertEquals("Edit Test Object", fr.getName());
        TestObject formObject2 = (TestObject) fr.getFormObject();
        Assertions.assertEquals(to.getItems().get(0), formObject2);

        TestObject newTo = new TestObject();
        newTo.setCreator("newb@thing.com");
        newTo.setIsin("isiny");
        newTo.setBidAxed(true);
        newTo.setBidQty(324);

        ea = new FormAction(wf, room, u, newTo, "items.[0]." + TableEditRow.UPDATE_SUFFIX, toWrapper);
        fr = (WorkResponse) editRow.apply(ea).get(0);
        Assertions.assertEquals(TestObjects.class, fr.getFormClass());
        TestObjects out = (TestObjects) fr.getFormObject();
        Assertions.assertEquals(out.getItems().get(0), newTo);
    }

    @Test
    public void testDeleteRows() {
        Map<String, Object> selects = Collections.singletonMap("items", Collections.singletonList(Collections.singletonMap("selected", "true")));
        FormSubmission uc = new FormSubmission(TestObjects.class, selects);
        FormAction ea = new FormAction(wf, room, u, uc, "items." + TableDeleteRows.ACTION_SUFFIX, toWrapper);
        WorkResponse fr = (WorkResponse) deleteRows.apply(ea).get(0);
        Assertions.assertEquals(TestObjects.class, fr.getFormClass());
        TestObjects formObject2 = (TestObjects) fr.getFormObject();
        Assertions.assertEquals(1, formObject2.getItems().size());
    }


    @Test
    public void testAddRowOfListOfPrimitiveTypeString() {
        TestOb6 entity = new TestOb6();
        entity.getNames().add("Amsidh");

        toWrapper = EntityJsonConverter.newWorkflow(entity);
        ejc = new EntityJsonConverter(wf);
        Integer initialSize = entity.getNames().size();
        FormAction ea = new FormAction(wf, room, u, null, "names." + TableAddRow.ACTION_SUFFIX, toWrapper);
        WorkResponse fr = (WorkResponse) addRows.apply(ea).get(0);

        Assertions.assertEquals(String.class, fr.getFormClass());
        Assertions.assertEquals("New java.lang.String", fr.getName());

        // ok, make changes and post back
        String newTo = (String) fr.getFormObject();
        newTo = "Suresh";

        ea = new FormAction(wf, room, u, newTo, "names." + TableAddRow.DO_SUFFIX, toWrapper);
        fr = (WorkResponse) addRows.apply(ea).get(0);
        TestOb6 to = (TestOb6) ejc.readWorkflow(fr.getData());

        Assertions.assertEquals(initialSize + 1, to.getNames().size());
        Assertions.assertTrue(to.getNames().contains(newTo));

    }
    
    /** 
	 * Used in tests 
	 */
	public String toWorkflowJson(Object o) {
		try {
			if (o == null) {
				return null;
			}
			EntityJson out = new EntityJson();
			out.put(WORKFLOW_001, o);
			return om.writeValueAsString(out);
		} catch (Exception e) {
			throw new UnsupportedOperationException("Map Fail", e);
		}
	}

    @Test
    public void testAddRowOfListOfPrimitiveTypeInteger() {

        TestOb6 entity = new TestOb6();
        entity.getIntegerList().add(1);
        toWrapper = EntityJsonConverter.newWorkflow(entity);
        ejc = new EntityJsonConverter(wf);
        Integer initialSize = entity.getIntegerList().size();
        FormAction ea = new FormAction(wf, room, u, null, "integerList." + TableAddRow.ACTION_SUFFIX, toWrapper);
        WorkResponse fr = (WorkResponse) addRows.apply(ea).get(0);

        Assertions.assertEquals(Integer.class, fr.getFormClass());
        Assertions.assertEquals("New java.lang.Integer", fr.getName());

        // ok, make changes and post back
        Integer newTo = (Integer) fr.getFormObject();
        newTo = 4;

        ea = new FormAction(wf, room, u, newTo, "integerList." + TableAddRow.DO_SUFFIX, toWrapper);
        fr = (WorkResponse) addRows.apply(ea).get(0);
        TestOb6 to = (TestOb6) ejc.readWorkflow(fr.getData());

        Assertions.assertEquals(initialSize + 1, to.getIntegerList().size());
        Assertions.assertTrue(to.getIntegerList().contains(newTo));

    }

    @Test
    public void testAddRowOfListOfPrimitiveTypeNumber() {

        TestOb6 entity = new TestOb6();
        entity.getNumberList().add(1);
        toWrapper = EntityJsonConverter.newWorkflow(entity);
        ejc = new EntityJsonConverter(wf);
        Integer initialSize = entity.getNumberList().size();
        FormAction ea = new FormAction(wf, room, u, null, "numberList." + TableAddRow.ACTION_SUFFIX, toWrapper);
        WorkResponse fr = (WorkResponse) addRows.apply(ea).get(0);

        Assertions.assertEquals(Number.class, fr.getFormClass());
        Assertions.assertEquals("New java.lang.Number", fr.getName());

        // ok, make changes and post back
        Number newTo = (Number) fr.getFormObject();
        newTo = 4;

        ea = new FormAction(wf, room, u, newTo, "numberList." + TableAddRow.DO_SUFFIX, toWrapper);
        fr = (WorkResponse) addRows.apply(ea).get(0);
        TestOb6 to = (TestOb6) ejc.readWorkflow(fr.getData());

        Assertions.assertEquals(initialSize + 1, to.getNumberList().size());
        Assertions.assertTrue(to.getNumberList().contains(newTo));

    }

    @Test
    public void testDeleteRowFromListOfPrimitiveTypeString() {
        TestOb6 entity = new TestOb6();
        entity.getNames().add("Amsidh");
        entity.getNames().add("Suresh");
        toWrapper = EntityJsonConverter.newWorkflow(entity);
        ejc = new EntityJsonConverter(wf);

        Map<String, Object> selects = Collections.singletonMap("names", Collections.singletonList(Collections.singletonMap("selected", "true")));
        FormSubmission uc = new FormSubmission(TestOb6.class, selects);
        FormAction ea = new FormAction(wf, room, u, uc, "names." + TableDeleteRows.ACTION_SUFFIX, toWrapper);
        WorkResponse fr = (WorkResponse) deleteRows.apply(ea).get(0);
        Assertions.assertEquals(TestOb6.class, fr.getFormClass());
        TestOb6 testOb6 = (TestOb6) fr.getFormObject();
        Assertions.assertEquals(1, testOb6.getNames().size());
    }

    @Test
    public void testDeleteRowFromListOfPrimitiveTypeInteger() {
        TestOb6 entity = new TestOb6();
        entity.getIntegerList().add(10);
        entity.getIntegerList().add(20);
        entity.getIntegerList().add(30);
        toWrapper = EntityJsonConverter.newWorkflow(entity);
        ejc = new EntityJsonConverter(wf);

        Map<String, Object> selects = Collections.singletonMap("integerList", Collections.singletonList(Collections.singletonMap("selected", "true")));
        FormSubmission uc = new FormSubmission(TestOb6.class, selects);
        FormAction ea = new FormAction(wf, room, u, uc, "integerList." + TableDeleteRows.ACTION_SUFFIX, toWrapper);
        WorkResponse fr = (WorkResponse) deleteRows.apply(ea).get(0);
        Assertions.assertEquals(TestOb6.class, fr.getFormClass());
        TestOb6 testOb6 = (TestOb6) fr.getFormObject();
        Assertions.assertEquals(2, testOb6.getIntegerList().size());
    }

    @Test
    public void testDeleteRowFromListOfPrimitiveTypeNumber() {
        TestOb6 entity = new TestOb6();
        entity.getNumberList().add(10);
        entity.getNumberList().add(20);
        entity.getNumberList().add(30);
        toWrapper = EntityJsonConverter.newWorkflow(entity);
        ejc = new EntityJsonConverter(wf);

        Map<String, Object> selects = Collections.singletonMap("numberList", Collections.singletonList(Collections.singletonMap("selected", "true")));
        FormSubmission uc = new FormSubmission(TestOb6.class, selects);
        FormAction ea = new FormAction(wf, room, u, uc, "numberList." + TableDeleteRows.ACTION_SUFFIX, toWrapper);
        WorkResponse fr = (WorkResponse) deleteRows.apply(ea).get(0);
        Assertions.assertEquals(TestOb6.class, fr.getFormClass());
        TestOb6 testOb6 = (TestOb6) fr.getFormObject();
        Assertions.assertEquals(2, testOb6.getNumberList().size());

    }

    @Test
    public void testEditRowFromListOfPrimitiveTypeString() {
        TestOb6 entity = new TestOb6();
        entity.getNames().add("Amsidh");
        entity.getNames().add("Suresh");
        toWrapper = EntityJsonConverter.newWorkflow(entity);
        ejc = new EntityJsonConverter(wf);

        FormAction ea = new FormAction(wf, room, u, null, "names.[0]." + TableEditRow.EDIT_SUFFIX, toWrapper);
        WorkResponse fr = (WorkResponse) editRow.apply(ea).get(0);
        Assertions.assertEquals(String.class, fr.getFormClass());
        Assertions.assertEquals("Edit java.lang.String", fr.getName());
        String formObject2 = (String) fr.getFormObject();
        Assertions.assertEquals(entity.getNames().get(0), formObject2);

        String updateName = "Rob";
        ea = new FormAction(wf, room, u, updateName, "names.[0]." + TableEditRow.UPDATE_SUFFIX, toWrapper);
        fr = (WorkResponse) editRow.apply(ea).get(0);
        Assertions.assertEquals(TestOb6.class, fr.getFormClass());
        TestOb6 out = (TestOb6) fr.getFormObject();
        Assertions.assertEquals(out.getNames().get(0), updateName);
    }

    @Test
    public void testEditRowFromListOfPrimitiveTypeInteger() {
        TestOb6 entity = new TestOb6();
        entity.getIntegerList().add(10);
        entity.getIntegerList().add(20);
        toWrapper = EntityJsonConverter.newWorkflow(entity);
        ejc = new EntityJsonConverter(wf);

        FormAction ea = new FormAction(wf, room, u, null, "integerList.[0]." + TableEditRow.EDIT_SUFFIX, toWrapper);
        WorkResponse fr = (WorkResponse) editRow.apply(ea).get(0);
        Assertions.assertEquals(Integer.class, fr.getFormClass());
        Assertions.assertEquals("Edit java.lang.Integer", fr.getName());
        Integer formObject2 = (Integer) fr.getFormObject();
        Assertions.assertEquals(entity.getIntegerList().get(0), formObject2);

        Integer updateNumber = 500;
        ea = new FormAction(wf, room, u, updateNumber, "integerList.[0]." + TableEditRow.UPDATE_SUFFIX, toWrapper);
        fr = (WorkResponse) editRow.apply(ea).get(0);
        Assertions.assertEquals(TestOb6.class, fr.getFormClass());
        TestOb6 out = (TestOb6) fr.getFormObject();
        Assertions.assertEquals(out.getIntegerList().get(0), updateNumber);
    }

    @Test
    public void testEditRowFromListOfPrimitiveTypeNumber() {
        TestOb6 entity = new TestOb6();
        Number number1= 10;
        Number number2= 20;
        entity.getNumberList().add(number1);
        entity.getNumberList().add(number2);
        toWrapper = EntityJsonConverter.newWorkflow(entity);
        ejc = new EntityJsonConverter(wf);

        FormAction ea = new FormAction(wf, room, u, null, "numberList.[0]." + TableEditRow.EDIT_SUFFIX, toWrapper);
        WorkResponse fr = (WorkResponse) editRow.apply(ea).get(0);
        Assertions.assertEquals(Integer.class, fr.getFormClass());
        Assertions.assertEquals("Edit java.lang.Integer", fr.getName());
        Number formObject2 = (Number) fr.getFormObject();
        Assertions.assertEquals(entity.getNumberList().get(0), formObject2);

        Number updateNumber = 5;
        ea = new FormAction(wf, room, u, updateNumber, "numberList.[0]." + TableEditRow.UPDATE_SUFFIX, toWrapper);
        fr = (WorkResponse) editRow.apply(ea).get(0);
        Assertions.assertEquals(TestOb6.class, fr.getFormClass());
        TestOb6 out = (TestOb6) fr.getFormObject();
        Assertions.assertEquals(out.getNumberList().get(0), updateNumber);
    }

}
