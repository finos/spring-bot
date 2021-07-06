package org.finos.symphony.toolkit.tools.reminders;

import com.symphony.api.model.Stream;
import com.symphony.api.model.StreamAttributes;
import com.symphony.api.model.StreamList;
import com.symphony.api.pod.StreamsApi;
import nl.altindag.log.LogCaptor;
import org.apache.juli.logging.Log;
import org.assertj.core.api.Assertions;
import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.cluster.LeaderService;
import org.finos.symphony.toolkit.stream.cluster.messages.SuppressionMessage;
import org.finos.symphony.toolkit.tools.reminders.alerter.Scheduler;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.RoomDef;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.history.History;
import org.finos.symphony.toolkit.workflow.response.FormResponse;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.ResponseHandler;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class SchedulerTests {

    @Mock
    History history;

    @Mock
    ResponseHandler responseHandler;

    @Mock
    RoomDef roomDef;

    @Mock
    SuppressionMessage suppressionMessage;

    @Mock
    LeaderService leaderService;

    @Mock
    Participant self;

    @Mock
    StreamsApi streams;

    @InjectMocks
    Scheduler scheduler = new Scheduler();

    LogCaptor logCaptor = LogCaptor.forClass(Scheduler.class);

    @Test
    public void handleFeedLeaderTest(){
        when(history.getLastFromHistory(Mockito.any(ReminderList.class)),Mockito.any(Addressable.class))).thenReturn(reminderList());
        roomDef = new RoomDef("Test room", "test description", false, Mockito.anyString());
        when(leaderService.isLeader(self)).thenReturn(true);
        when(streams.v1StreamsListPost(null, null, 0, 50)).thenReturn(createStreams());

        scheduler.everyFiveMinutesWeekday();
        //verify(responseHandler).accept(Mockito.any(FormResponse.class));
        Assertions.assertThat(logCaptor.getInfoLogs()).hasSize(2).contains("TimedAlerter waking","Not leader, sleeping");
//        Assertions.assertThat(logCaptor.getDebugLogs()).hasSize(1).contains("Reminder are displayed via Form Response through response Handler");
    }
    @Test
    public void handleFeedNonLeaderTest(){
        when(history.getLastFromHistory(ReminderList.class,getAddressable())).thenReturn(reminderList());
        roomDef = new RoomDef("Test room", "test description", false, Mockito.anyString());
        scheduler.everyFiveMinutesWeekday();
        verify(responseHandler).accept(Mockito.any(FormResponse.class));

    }


      private StreamList createStreams(){
        StreamAttributes streamAttributes = new StreamAttributes();
        StreamList sl = new StreamList();
        streamAttributes.setId("1234");
        sl.add(streamAttributes);
        return sl;
      }

    private Addressable getAddressable(){
        Addressable a = new Addressable() {

            @Override
            public int hashCode() {
                return super.hashCode();
            }

            @Override
            public boolean equals(Object obj) {
                return super.equals(obj);
            }

            @Override
            protected Object clone() throws CloneNotSupportedException {
                return super.clone();
            }

            @Override
            public String toString() {
                return super.toString();
            }

            @Override
            protected void finalize() throws Throwable {
                super.finalize();
            }
        };
        return a;

    }
    private Optional<ReminderList> reminderList(){
        Reminder reminder = new Reminder();
        reminder.setDescription("Check at 9 pm");
        reminder.setLocalTime(LocalDateTime.now());
        reminder.setAuthor(getUser());
        List<Reminder> reminders = new ArrayList<>();
        reminders.add(reminder);
        ReminderList rl = new ReminderList();

        rl.setReminders(reminders);
        Optional<ReminderList> rrl = Optional.of(rl);
        return rrl;
    }

    private User getUser(){
        User user = new User() {
            @Override
            public String getAddress() {
                return "New Address";
            }

            @Override
            public String getId() {
                return "1234";
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
