package org.finos.springbot.tools.reminders.alerter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import org.finos.springbot.symphony.conversations.SymphonyConversations;
import org.finos.springbot.symphony.stream.Participant;
import org.finos.springbot.symphony.stream.cluster.LeaderService;
import org.finos.springbot.tools.reminders.Reminder;
import org.finos.springbot.tools.reminders.ReminderList;
import org.finos.springbot.tools.reminders.ReminderProperties;
import org.finos.springbot.workflow.annotations.WorkMode;
import org.finos.springbot.workflow.content.Addressable;
import org.finos.springbot.workflow.conversations.AllConversations;
import org.finos.springbot.workflow.history.AllHistory;
import org.finos.springbot.workflow.response.ErrorResponse;
import org.finos.springbot.workflow.response.WorkResponse;
import org.finos.springbot.workflow.response.handlers.ResponseHandlers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.symphony.api.pod.StreamsApi;

@Component
public class Scheduler {

    public static Logger LOG = LoggerFactory.getLogger(Scheduler.class);

    @Autowired
    ResponseHandlers responseHandlers;

    @Autowired
    AllHistory h;

    @Autowired
    AllConversations rooms;

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
            Set<Addressable> allRooms = rooms.getAllAddressables();
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

