package org.finos.symphony.toolkit.tools.reminders.alerter;

import com.symphony.api.model.StreamAttributes;
import com.symphony.api.model.StreamFilter;
import com.symphony.api.model.StreamList;
import com.symphony.api.pod.StreamsApi;
import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.cluster.LeaderService;
import org.finos.symphony.toolkit.tools.reminders.ReminderList;
import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.RoomDef;
import org.finos.symphony.toolkit.workflow.history.History;
import org.finos.symphony.toolkit.workflow.response.FormResponse;
import org.finos.symphony.toolkit.workflow.room.Rooms;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.EntityJsonConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.ResponseHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.room.SymphonyRooms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.function.Consumer;

@Component
public class Scheduler {

    public static Logger LOG = LoggerFactory.getLogger(Scheduler.class);

    @Autowired
    ResponseHandler responseHandler;

    @Autowired
    EntityJsonConverter converter;

    @Lazy
    @Autowired
    Workflow w;

    @Autowired
    History h;

    @Autowired
    Rooms rooms;

    @Autowired
    SymphonyRooms symphonyRooms;

    @Autowired
    StreamsApi streams;

    @Autowired
    LeaderService leaderService;

    @Autowired
    Participant self;

    @Autowired
    Workflow workflow;

    @Scheduled(cron = "0 0/2 * * * MON-FRI")
    public void everyFiveMinutesWeekday() {
        onAllStreams(s -> handleFeed(temporaryRoomDef(s)));
    }

    public RoomDef temporaryRoomDef(StreamAttributes s) {

        return new RoomDef("", "", false, s.getId());
    }

    public void onAllStreams(Consumer<StreamAttributes> action) {
        LOG.info("TimedAlerter waking");

        if (leaderService.isLeader(self)) {
            StreamFilter filter = new StreamFilter();
            filter.includeInactiveStreams(false);
            int skip = 0;
            StreamList sl;
            do {
                sl = streams.v1StreamsListPost(null, null, skip, 50);
                sl.forEach(s -> action.accept(s));
                skip += sl.size();
            } while (sl.size() == 50);


            LOG.info("TimedAlerter processed " + skip + " streams ");
        } else {
            LOG.info("Not leader, sleeping");
        }
    }


    public void handleFeed(Addressable a) {
        Optional<ReminderList> fl = h.getLastFromHistory(ReminderList.class, a);
        if (fl.isPresent()) {

            fl.get().getReminders().stream().forEach((currentReminder) -> {
                Instant currentTime = LocalDateTime.now().toInstant(ZoneOffset.UTC);
                Instant timeForReminder = currentReminder.getInstant().minus(30, ChronoUnit.MINUTES);

                if (timeForReminder.isBefore(currentTime)) {

                    EntityJson ej = EntityJsonConverter.newWorkflow(currentReminder);
                    ReminderList updatedList = fl.get();
                    updatedList.getReminders().remove(currentReminder);
                    ej.put("ReminderList", updatedList);


                    responseHandler.accept(new FormResponse(w, a, ej, "Display Reminder", "This is regarding the reminder set by you", currentReminder, false,
                            w.gatherButtons(currentReminder, a)));


                }


            });


        }

    }


}

