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
import java.util.Optional;
import java.util.function.Consumer;

@Component
public class TimedAlerter {

    public static Logger LOG =  LoggerFactory.getLogger(TimedAlerter.class);

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


//    @Autowired
//    FeedLoader loader;

    @Scheduled(cron="0 0/5 * * * ?")
    public void everyWeekdayHour() {
        onAllStreams(s -> handleFeed(temporaryRoomDef(s)));
    }

    public RoomDef temporaryRoomDef(StreamAttributes s) {

        return new RoomDef("", "", false, s.getId());
    }

    public void onAllStreams(Consumer<StreamAttributes> action) {
        LOG.info("TimedAlerter waking");

//        if (leaderService.isLeader(self)) {
            StreamFilter filter = new StreamFilter();
            filter.includeInactiveStreams(false);
            int skip = 0;
            StreamList sl;
            do {
                sl = streams.v1StreamsListPost(null, null, skip, 50);
                sl.forEach(s -> action.accept(s));
                skip += sl.size();
            } while (sl.size() == 50);


            LOG.info("TimedAlerter processed "+skip+" streams ");
//        } else {
//            LOG.info("Not leader, sleeping");
//        }
    }


    public void handleFeed(Addressable a) {
        Optional<ReminderList> fl = h.getLastFromHistory(ReminderList.class, a);
        if (fl.isPresent()) {

            fl.get().getRemList().stream().forEach((currentReminder)->{
                Instant currentTime = LocalDateTime.now().toInstant(ZoneOffset.UTC);
                Instant timeForReminder = currentReminder.getInstant();
                if(timeForReminder.isBefore(currentTime)){

                    EntityJson ej = EntityJsonConverter.newWorkflow(currentReminder);

                    responseHandler.accept(new FormResponse(w, a, ej, "Display Reminder", "This is regarding the reminder set by you", currentReminder, false,
                            w.gatherButtons(currentReminder, a)));
                    //fl.get().getRemList().remove(currentReminder);
                    //fl.get().setRemList(fl.get().getRemList());
//                    ReminderList reminderList = new ReminderList();
//                    reminderList.deleteReminder(fl, currentReminder);
                    ReminderList.deleteReminder(fl,currentReminder);

                }


            });

            }
        //return fl;
        }


//    public int allItems(Addressable a, ReminderList fl) {
//        Optional<Article> lastArticle = h.getLastFromHistory(Article.class, a);
//        Instant startTime = Instant.now();
//        int count = 0;
//
//        for (Feed f : fl.getFeeds()) {
//            try {
//                Instant since = feedCovered(lastArticle, f) ? lastArticle.get().getStartTime() :
//                        LocalDateTime.now().minusYears(1).toInstant(ZoneOffset.UTC);
//
//                count += allItemsSince(startTime, f, a, since, fl);
//            } catch (Exception e) {
//                LOG.error("AllItems failed: ", e);
//                responseHandler.accept(new ErrorResponse(w, a, "Problem with feed: "+f.getName()+": "+e.getMessage()));
//            }
//        }
//
//        return count;
//    }
//
//    public boolean feedCovered(Optional<Article> lastArticle, Feed f) {
//        if (lastArticle.isPresent()) {
//            List<String> urls = lastArticle.get().getFeedUrls();
//
//            if ((urls != null) && (urls.contains(f.getUrl()))) {
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//    private int allItemsSince(Instant startTime, Feed f, Addressable a, Instant since, FeedList fl) throws Exception {
//        int count = 0;
//        for (SyndEntry e : loader.createSyndFeed(f.getUrl()).getEntries()) {
//            if (e.getPublishedDate().toInstant().isAfter(since)) {
//                EntityJson ej = new EntityJson();
//                HashTag ht = createHashTag(f);
//                Article article = new Article(e.getTitle(), e.getAuthor(), e.getPublishedDate().toInstant(), e.getLink(), startTime, fl, ht);
//                ej.put(EntityJsonConverter.WORKFLOW_001, article);
//                responseHandler.accept(new FormResponse(w, a, ej, f.getName(), e.getAuthor(), article, false, w.gatherButtons(article, a)));
//                count ++;
//            }
//
//        }
//
//        return count;
//    }
//
//    Pattern p = Pattern.compile("[^\\w]");
//
//    private HashTag createHashTag(Feed f) {
//        String simplified = f.getName().replaceAll("[^\\w]","");
//        return new HashTagDef(simplified);
//    }

}

