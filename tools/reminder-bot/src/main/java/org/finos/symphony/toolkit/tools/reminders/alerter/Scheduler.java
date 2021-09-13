package org.finos.symphony.toolkit.tools.reminders.alerter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.cluster.LeaderService;
import org.finos.symphony.toolkit.tools.reminders.Reminder;
import org.finos.symphony.toolkit.tools.reminders.ReminderList;
import org.finos.symphony.toolkit.tools.reminders.ReminderProperties;
import org.finos.symphony.toolkit.workflow.annotations.WorkMode;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.Chat;
import org.finos.symphony.toolkit.workflow.conversations.Conversations;
import org.finos.symphony.toolkit.workflow.history.History;
import org.finos.symphony.toolkit.workflow.response.ErrorResponse;
import org.finos.symphony.toolkit.workflow.response.WorkResponse;
import org.finos.symphony.toolkit.workflow.response.handlers.ResponseHandlers;
import org.finos.symphony.toolkit.workflow.sources.symphony.conversations.SymphonyConversations;
import org.finos.symphony.toolkit.workflow.sources.symphony.json.EntityJsonConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.symphony.api.model.StreamAttributes;
import com.symphony.api.model.StreamFilter;
import com.symphony.api.model.StreamList;
import com.symphony.api.pod.StreamsApi;

@Component
public class Scheduler {

    public static Logger LOG = LoggerFactory.getLogger(Scheduler.class);

    @Autowired
    ResponseHandlers responseHandlers;

    @Autowired
    History h;

    @Autowired
    Conversations rooms;

    @Autowired
    ReminderProperties rp;

    @Autowired
    SymphonyConversations symphonyRooms;

    @Autowired
    StreamsApi streams;

    @Autowired
    LeaderService leaderService;

    @Autowired
    Participant self;

    @Scheduled(cron = "0 0/5 * * * MON-FRI")
    public void everyFiveMinutesWeekday() {
        onAllStreams(s -> handleFeed(s));
    }
    
    public void onAllStreams(Consumer<Addressable> action) {
        LOG.info("TimedAlerter waking");

        if (leaderService.isLeader(self)) {
            Set<Addressable> allRooms = rooms.getAllConversations();
			allRooms.forEach(s -> action.accept(s));
            LOG.info("TimedAlerter processed " + allRooms.size() + " streams ");
        } else {
            LOG.info("Not leader, sleeping");
        }
    }


    public void handleFeed(Addressable a) {
        try {
			Optional<ReminderList> fl = h.getLastFromHistory(ReminderList.class, a);
			
			if (fl.isPresent()) {
			    ReminderList updatedList = new ReminderList(fl.get());
			    ZoneId zone = updatedList.getTimeZone();
			    Instant currentTime = LocalDateTime.now().toInstant(ZoneOffset.UTC);
			    ZoneOffset zo = zone.getRules().getOffset(currentTime);


			    fl.get().getReminders().stream().forEach((currentReminder) -> {
			        Instant timeForReminder = currentReminder.getLocalTime().toInstant(zo);

			        if (timeForReminder.isBefore(currentTime)) {
			            Map<String, Object> ej = WorkResponse.createEntityMap(currentReminder, null, null);
			            updatedList.getReminders().remove(currentReminder);
			            ej.put("ReminderList", updatedList);
			            
			            WorkResponse wr = new WorkResponse(a, ej, "display-reminder", WorkMode.VIEW, Reminder.class);
			            responseHandlers.accept(wr);

			        }
			    });
			}
		} catch (Exception e) {
			responseHandlers.accept(new ErrorResponse(a, e));
		}
    }
}

