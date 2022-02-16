package org.finos.symphony.toolkit.tools.reminders;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.finos.springbot.testing.content.TestRoom;
import org.finos.springbot.tool.reminders.Reminder;
import org.finos.springbot.tool.reminders.ReminderList;
import org.finos.springbot.tool.reminders.alerter.Scheduler;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.conversations.AllConversations;
import org.finos.springbot.workflow.history.AllHistory;
import org.finos.springbot.workflow.response.WorkResponse;
import org.finos.springbot.workflow.response.handlers.ResponseHandlers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SchedulerTests {

	@Mock
	AllHistory history;

	@Mock
	ResponseHandlers responseHandlers;

	@Mock
	AllConversations rooms;


	Scheduler scheduler;

	LocalDateTime expectedTime = LocalDateTime.now();

	@SuppressWarnings("unchecked")
	@Test
	public void handleFeedLeaderTest() {
		scheduler = new Scheduler(responseHandlers, history, rooms);
		when(history.getLastFromHistory(Mockito.any(Class.class), Mockito.any(Addressable.class)))
				.thenReturn(reminderList());

		when(rooms.getAllAddressables()).thenReturn(createStreams());
		scheduler.everyFiveMinutesWeekday();
		verify(responseHandlers).accept(Mockito.any(WorkResponse.class));
		ArgumentCaptor<WorkResponse> argumentCaptor = ArgumentCaptor.forClass(WorkResponse.class);
		verify(responseHandlers).accept(argumentCaptor.capture());
		WorkResponse fr = argumentCaptor.getValue();
		Reminder r = (Reminder) fr.getFormObject();
		Assertions.assertEquals(r.getLocalTime(), expectedTime);

		// reminder timefinder tests to chck formresponse

	}

	@Test
	@Disabled("We don't have leadership election at the moment-all bots are leaders")
	public void handleFeedNonLeaderTest() {
		scheduler.everyFiveMinutesWeekday();
		verify(responseHandlers, VerificationModeFactory.noInteractions()).accept(Mockito.any(WorkResponse.class));

	}

	private Set<Addressable> createStreams() {
		return Collections.singleton(new TestRoom("test", "1234"));
	}

	private Optional<ReminderList> reminderList() {
		Reminder reminder = new Reminder();
		reminder.setDescription("Check at 9 pm");
		reminder.setLocalTime(expectedTime);
		List<Reminder> reminders = new ArrayList<>();
		reminders.add(reminder);
		ReminderList rl = new ReminderList();
		rl.setTimeZone(ZoneId.of("Europe/London"));

		rl.setReminders(reminders);
		Optional<ReminderList> rrl = Optional.of(rl);
		return rrl;
	}
}
