package org.finos.symphony.toolkit.workflow;

import java.util.Arrays;

import org.finos.springbot.sources.teams.content.TeamsChat;
import org.finos.symphony.toolkit.workflow.annotations.WorkMode;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.fixture.Address;
import org.finos.symphony.toolkit.workflow.fixture.OurController;
import org.finos.symphony.toolkit.workflow.fixture.Person;
import org.finos.symphony.toolkit.workflow.response.WorkResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CollectionsMessageFormTest extends AbstractMockSymphonyTest {
   
    @Autowired
	OurController oc;


    @Test
    public void testCollectionEditMessage() throws Exception {
    	WorkResponse wr = new WorkResponse(createAddressable(), getPerson(), WorkMode.EDIT);
        testTemplating(wr, "abc123", "testCollectionEditMessageML.ml", "testCollectionEditMessage.json");
    }

    private Addressable createAddressable() {
		return new TeamsChat("bobo", "abc123");
	}

	@Test
    public void testCollectionViewMessage() throws Exception {
		WorkResponse wr = new WorkResponse(createAddressable(), getPerson(), WorkMode.VIEW);
		testTemplating(wr, "abc123", "testCollectionViewMessageML.ml", "testCollectionViewMessage.json");
    }

	

    private Person getPerson(){
        Person person = new Person(Arrays.asList("abc","pqr"), Arrays.asList(new Address("Pune"), new Address("Mumbai"), new Address("Bangalore")));
        return person;
    }
    
}

