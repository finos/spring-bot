package com.github.deutschebank.symphony.workflow.fixture;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.deutschebank.symphony.workflow.Workflow;
import com.github.deutschebank.symphony.workflow.content.Addressable;
import com.github.deutschebank.symphony.workflow.content.Room;
import com.github.deutschebank.symphony.workflow.content.RoomDef;
import com.github.deutschebank.symphony.workflow.content.Tag;
import com.github.deutschebank.symphony.workflow.content.User;
import com.github.deutschebank.symphony.workflow.content.UserDef;
import com.github.deutschebank.symphony.workflow.history.History;
import com.github.deutschebank.symphony.workflow.java.ClassBasedWorkflow;

@Configuration
public class TestWorkflowConfig {

	public static final TestObjects INITIAL = createTestObjects();
	
	public static final List<String> ADMIN_EMAILS = Collections.singletonList("robert.moffat@example.com");
	
	public static TestObjects createTestObjects() {
		return new TestObjects(
			new ArrayList<>(Arrays.asList(
				new TestObject("83274239874", true, true, "rob@example.com", 234786, 2138),
				new TestObject("AUD274239874", true, false, "gregb@example.com", 2386, new BigDecimal("234823498.573")))));
	}

	public static final User u = new UserDef("123", "Testy McTestFace", "tmt@example.com");
	public static final Room room = new RoomDef("Test Room",  "Test Room Desc", false, null);
	
	@Bean
	public History history(Workflow wf) {
		History h = new History() {
			
			@Override
			public <X> Optional<X> getLastFromHistory(Class<X> type, Addressable address) {
				if (type == TestObject.class) {
					return Optional.of((X) INITIAL.getItems().get(1));
				} else if (type == TestObjects.class) {
					return Optional.of((X) INITIAL);
				} else {
					throw new IllegalArgumentException();
				}
			}
			
			@Override
			public <X> List<X> getFromHistory(Class<X> type, Addressable address, Instant since) {
				if (type == TestObject.class) {
					return (List<X>) INITIAL.getItems();
				} else if (type == TestObjects.class) {
					return (List<X>) Collections.singletonList(INITIAL);
				} else {
					throw new IllegalArgumentException();
				}
			}

			@Override
			public List<Object> getFromHistory(Tag t, Addressable address, Instant since) {
				return Collections.EMPTY_LIST;
			}
		};
		
		wf.registerHistoryProvider(h);
		return h;
	}
	
	@Bean
	public Workflow testObjectsWorkflow() {
		ClassBasedWorkflow basicWorkflow = new ClassBasedWorkflow("testing-fixture-namespace", Collections.singletonList(u), Collections.singletonList(room));
		basicWorkflow.addClass(TestObjects.class);
		basicWorkflow.addClass(TestObject.class);
		basicWorkflow.addClass(TestOb3.class);
		basicWorkflow.addClass(TestOb4.class);
		return basicWorkflow;
	}

}
