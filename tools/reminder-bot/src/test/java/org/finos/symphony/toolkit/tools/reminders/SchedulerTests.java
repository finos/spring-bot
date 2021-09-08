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

import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.cluster.LeaderService;
import org.finos.symphony.toolkit.tools.reminders.alerter.Scheduler;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.Chat;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.conversations.Conversations;
import org.finos.symphony.toolkit.workflow.history.History;
import org.finos.symphony.toolkit.workflow.response.WorkResponse;
import org.finos.symphony.toolkit.workflow.response.handlers.ResponseHandlers;
import org.finos.symphony.toolkit.workflow.sources.symphony.content.SymphonyRoom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.verification.VerificationModeFactory;
import org.mockito.junit.jupiter.MockitoExtension;

import com.symphony.api.model.StreamAttributes;
import com.symphony.api.model.StreamList;
import com.symphony.api.pod.StreamsApi;

@ExtendWith(MockitoExtension.class)
public class SchedulerTests {

	@Mock
	History history;

	@Mock
	ResponseHandlers responseHandlers;

	@Mock
	LeaderService leaderService;

	@Mock
	Conversations rooms;

	@InjectMocks
	Scheduler scheduler = new Scheduler();

	LocalDateTime expectedTime = LocalDateTime.now();

	@SuppressWarnings("unchecked")
	@Test
	public void handleFeedLeaderTest() {
		when(history.getLastFromHistory(Mockito.any(Class.class), Mockito.any(Addressable.class)))
				.thenReturn(reminderList());

		when(leaderService.isLeader(Mockito.any())).thenReturn(true);
		when(rooms.getAllConversations()).thenReturn(createStreams());

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
	public void handleFeedNonLeaderTest() {
		when(leaderService.isLeader(Mockito.any())).thenReturn(false);
		scheduler.everyFiveMinutesWeekday();
		verify(responseHandlers, VerificationModeFactory.noInteractions()).accept(Mockito.any(WorkResponse.class));

	}

	private Set<Addressable> createStreams() {
		return Collections.singleton(new SymphonyRoom("test", "1234"));
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

	private User getUser() {
		User user = new User() {
			@Override
			public String getEmailAddress() {
				return "New Address";
			}

			@Override
			public Type getTagType() {
				return null;
			}

			@Override
			public String getName() {
				return "Sherlock Holmes";
			}

			@Override
			public String getText() {
				return null;
			}
		};
		return user;

	}
}
