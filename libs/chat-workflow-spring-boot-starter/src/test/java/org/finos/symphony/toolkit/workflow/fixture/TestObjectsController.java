package org.finos.symphony.toolkit.workflow.fixture;

import java.util.ArrayList;
import java.util.List;

import org.finos.symphony.toolkit.workflow.annotations.Exposed;
import org.finos.symphony.toolkit.workflow.content.Word;
import org.springframework.stereotype.Controller;

@Controller
public class TestObjectsController {

	@Exposed(value = "wrap", description="wraps this testObject into the list")
	public TestObjects wrap(TestObject to) {
		List<TestObject> l = new ArrayList<>();
		l.add(to);
		return new TestObjects(l);
	}
	
	
	@Exposed(value = "show")
	public TestObjects show(TestObjects to) {
		return to;
	}
	

	@Exposed(value = "remove {w1} {w2}", description="removes item by number. e.g. /remove 4")
	public TestObjects remove(Word w1, Word w2, TestObjects to) {
		Integer i = Integer.parseInt(w2.getText());
		to.getItems().remove((int) i);
		return to;
	}
	/*
	@Exposed(description= "creates a new test objects")
	public static TestObjects testObjects() {
		return TestWorkflowConfig.INITIAL_TEST_OBJECTS;
	}
	
	@Exposed(description = "Add another test object")
	public TestObjects add(TestObject o) {
		List<TestObject> chg = new ArrayList<>(items);
		chg.add(o);
		return new TestObjects(chg);
	}

	@Exposed(description="Throwable exception thrown")
	public TestObjects throwable(Word w1, Word w2) throws Throwable {
		throw new Throwable("Throwable exception thrown");
	}

	@Exposed(description="Exception exception thrown")
	public TestObjects exception(Word w1, Word w2) throws Throwable {
		throw new Exception("Exception exception thrown");
	}

	@Exposed(description="RuntimeException exception thrown")
	public TestObjects runtime(Word w1, Word w2) throws Throwable {
		throw new RuntimeException("RuntimeException exception thrown");
	}

	@Exposed(description="Exception thrown with no message")
	public TestObjects exceptionnomessage(Word w1, Word w2) throws Throwable {
		throw new Exception();
	}
	@Exposed(description="Exception thrown with no exception details")
	public TestObjects exceptionnullcause(Word w1, Word w2) throws Throwable {
		throw new Throwable(null, null);
	}

	@Exposed(description="Null pointer exception thrown")
	public TestObjects exceptionnullpointercause(Word w1, Word w2) throws Exception {
		Exception causeException = new Exception();
		throw new Exception("Null pointer exception thrown", causeException);
	} */
}
