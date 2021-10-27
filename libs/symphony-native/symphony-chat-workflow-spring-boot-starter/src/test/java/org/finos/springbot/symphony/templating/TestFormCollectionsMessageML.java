package org.finos.springbot.symphony.templating;

import java.util.Arrays;

import org.finos.springbot.symphony.content.HashTag;
import org.finos.springbot.workflow.annotations.WorkMode;
import org.finos.springbot.workflow.response.WorkResponse;
import org.finos.symphony.toolkit.workflow.AbstractMockSymphonyTest;
import org.finos.symphony.toolkit.workflow.fixture.TestCollections;
import org.finos.symphony.toolkit.workflow.fixture.TestCollections.Choice;
import org.finos.symphony.toolkit.workflow.fixture.TestCollections.MiniBean;
import org.junit.jupiter.api.Test;

public class TestFormCollectionsMessageML extends AbstractMockSymphonyTest {

	private TestCollections createTestCollections() {
		MiniBean mb1 = new MiniBean("A String", 4973, Arrays.asList("Amsidh", "Rob"));
		MiniBean mb2 = new MiniBean("Another String", 45, Arrays.asList("Terry", "James"));
		MiniBean mb3 = new MiniBean("Thing 3", 8787, null);
		
		TestCollections out = new TestCollections(
				Arrays.asList("a", "b", "c"), 
				Arrays.asList(Choice.A, Choice.B), 
				Arrays.asList(mb1, mb2, mb3),
				Arrays.asList(new HashTag("abc"), new HashTag("def")));
		return out;
	}
	

	@Test
	public void testCollectionsEditMessageML() throws Exception {
		TestCollections c = createTestCollections();
		WorkResponse wr = createWorkAddSubmit(WorkMode.EDIT, c);
		testTemplating(wr, "abc123", "testCollectionsEditMessageML.ml", "testCollectionsEditMessageML.json");
	}

	@Test
	public void testCollectionsViewMessageML() throws Exception {
		TestCollections c = createTestCollections();
		WorkResponse wr = createWorkAddSubmit(WorkMode.VIEW, c);
		testTemplating(wr, "abc123", "testCollectionsViewMessageML.ml", "testCollectionsViewMessageML.json");
	}
}
