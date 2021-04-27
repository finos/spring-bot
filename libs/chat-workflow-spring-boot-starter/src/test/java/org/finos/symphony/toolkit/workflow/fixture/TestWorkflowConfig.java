package org.finos.symphony.toolkit.workflow.fixture;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.Room;
import org.finos.symphony.toolkit.workflow.content.RoomDef;
import org.finos.symphony.toolkit.workflow.content.Tag;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.content.UserDef;
import org.finos.symphony.toolkit.workflow.history.History;
import org.finos.symphony.toolkit.workflow.java.workflow.ClassBasedWorkflow;
import org.finos.symphony.toolkit.workflow.sources.symphony.history.SymphonyHistory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TestWorkflowConfig {

	public static final TestObject INITIAL_TEST_OBJECT = createTestObject();
	
	public static final TestObjects INITIAL_TEST_OBJECTS = createTestObjects();
	
	public static final List<String> ADMIN_EMAILS = Collections.singletonList("robert.moffat@example.com");
	
	public static TestObject createTestObject() {
		return new TestObject("AUD274239874", true, false, "gregb@example.com", 2386, new BigDecimal("234823498.573"));
	}
	
	public static TestObjects createTestObjects() {
		return new TestObjects(
			new ArrayList<>(Arrays.asList(
				new TestObject("83274239874", true, true, "rob@example.com", 234786, 2138),
				new TestObject("AUD274239874", true, false, "gregb@example.com", 2386, new BigDecimal("234823498.573")))));
	}

	public static final User u = new UserDef("123", "Testy McTestFace", "tmt@example.com");
	public static final Room room = new RoomDef("Test Room",  "Test Room Desc", false, null);
	
	@Bean
	public SymphonyHistory symphonyHistory(Workflow wf) {
		SymphonyHistory h = new SymphonyHistory() {
			
			@SuppressWarnings("unchecked")
			@Override
			public <X> Optional<X> getLastFromHistory(Class<X> type, Addressable address) {
				if (type == TestObject.class) {
					return Optional.of((X) INITIAL_TEST_OBJECT);
				} else if (type == TestObjects.class) {
					return Optional.of((X) INITIAL_TEST_OBJECTS);
				} else {
					throw new IllegalArgumentException();
				}
			}
			
			@SuppressWarnings("unchecked")
			@Override
			public <X> List<X> getFromHistory(Class<X> type, Addressable address, Instant since) {
				if (type == TestObject.class) {
					return (List<X>) Collections.singletonList(INITIAL_TEST_OBJECT);
				} else if (type == TestObjects.class) {
					return (List<X>) Collections.singletonList(INITIAL_TEST_OBJECTS);
				} else {
					throw new IllegalArgumentException();
				}
			}

			@SuppressWarnings("unchecked")
			@Override
			public <X> List<X> getFromHistory(Class<X> c, Tag t, Addressable address, Instant since) {
				return Collections.EMPTY_LIST;
			}

			@Override
			public <X> Optional<X> getLastFromHistory(Class<X> type, Tag t, Addressable address) {
				return Optional.empty();
			}

			@Override
			public <X> Optional<EntityJson> getLastEntityJsonFromHistory(Class<X> type, Addressable address) {
				throw new UnsupportedOperationException();
			}

			@Override
			public <X> Optional<EntityJson> getLastEntityJsonFromHistory(Class<X> type, Tag t, Addressable address) {
				throw new UnsupportedOperationException();
			}

			@Override
			public <X> List<EntityJson> getEntityJsonFromHistory(Class<X> type, Addressable address, Instant since) {
				throw new UnsupportedOperationException();
			}

			@Override
			public List<EntityJson> getEntityJsonFromHistory(Tag t, Addressable address, Instant since) {
				throw new UnsupportedOperationException();
			}

			@Override
			public <X> Optional<X> getFromEntityJson(EntityJson ej, Class<X> c) {
				throw new UnsupportedOperationException();
			}

			@Override
			public <X> List<X> getFromEntityJson(List<EntityJson> ej, Class<X> c) {
				throw new UnsupportedOperationException();
			}
		};
		
		return h;
	}
	
	@Bean
	public Workflow testObjectsWorkflow() {
		ClassBasedWorkflow basicWorkflow = new ClassBasedWorkflow("testing-fixture-namespace", Collections.singletonList(u), Collections.singletonList(room));
		basicWorkflow.addClass(TestObjects.class);
		basicWorkflow.addClass(TestObject.class);
		basicWorkflow.addClass(TestOb3.class);
		basicWorkflow.addClass(TestOb4.class);
		basicWorkflow.addClass(TestTemplatedObject.class);
		return basicWorkflow;
	}

}
