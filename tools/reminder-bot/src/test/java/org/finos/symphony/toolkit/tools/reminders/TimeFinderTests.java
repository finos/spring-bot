package org.finos.symphony.toolkit.tools.reminders;


import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.content.*;
import org.finos.symphony.toolkit.workflow.history.History;
import org.finos.symphony.toolkit.workflow.response.FormResponse;
import org.finos.symphony.toolkit.workflow.response.Response;
import org.finos.symphony.toolkit.workflow.sources.symphony.messages.SimpleMessageAction;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class TimeFinderTests {

    @Mock
    StanfordCoreNLP stanfordCoreNLP;

    @Mock
    ReminderProperties reminderProperties;

    @Mock
    History history;

    @InjectMocks
    TimeFinder timefinder = new TimeFinder();

    String user = "Finos User 2";
    String Message = "Check at 9 pm";
    Workflow workflow ;
    EntityJson ej;



    private SimpleMessageAction getAction(){
        SimpleMessageAction simpleMessageAction = new SimpleMessageAction(workflow,getAddressable(),getUser(),getMessage(),ej);
        return simpleMessageAction;

    }

    @Test
    public void applyTest(){


        when(history.getLastFromHistory(ReminderList.class,getAddressable())).thenReturn(reminderList());
        timefinder.initializingStanfordProperties();
        List<Response> responses = timefinder.apply(getAction());
        Assert.assertEquals(responses.size(),1);
        FormResponse fr = (FormResponse)responses.get(0);
        Reminder r = (Reminder) fr.getFormObject();
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        Assert.assertEquals(r.getLocalTime(),LocalDateTime.of(year,month+1,day,21,30,0));

    }

    private Optional<ReminderList> reminderList(){
        Reminder reminder = new Reminder();
        reminder.setDescription("Check at 9:30 pm");
        reminder.setLocalTime(LocalDateTime.now());
        reminder.setAuthor(getUser());
        List<Reminder> reminders = new ArrayList<>();
        reminders.add(reminder);
        ReminderList rl = new ReminderList();
        rl.setRemindBefore(10);
        rl.setTimeZone(ZoneId.of("Asia/Calcutta"));

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
    private Message getMessage(){
        Message m = new Message() {
            @Override
            public List<Content> getContents() {
                return null;
            }

            @Override
            public OrderedContent<Content> buildAnother(List<Content> contents) {
                return null;
            }

            @Override
            public String getText() {
                return "check at 9:30 pm";
            }
        };
        return m;
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


}
