package org.finos.symphony.toolkit.tools.reminders;

import com.symphony.api.model.V4Event;
import com.symphony.api.model.V4MessageSent;
import com.symphony.api.model.V4User;
import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.Timex;
import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.stream.StreamEventConsumer;
import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.content.Message;
import org.finos.symphony.toolkit.workflow.content.Room;
import org.finos.symphony.toolkit.workflow.form.Button;
import org.finos.symphony.toolkit.workflow.form.ButtonList;
import org.finos.symphony.toolkit.workflow.history.History;
import org.finos.symphony.toolkit.workflow.response.FormResponse;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.EntityJsonConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.ResponseHandler;
import org.finos.symphony.toolkit.workflow.sources.symphony.messages.SimpleMessageParser;
import org.finos.symphony.toolkit.workflow.sources.symphony.room.SymphonyRooms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Properties;
import java.util.TimeZone;
import java.util.function.Consumer;

public class StreamEventConsumerImpl implements StreamEventConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(StreamEventConsumerImpl.class);

    @Autowired
    SimpleMessageParser simpleMessageParser;

    @Autowired
    EntityJsonConverter entityJsonConverter;

    @Autowired
    ResponseHandler responseHandler;

    @Autowired
    Workflow workflow;

    @Autowired
    TaskScheduler taskScheduler;

    @Autowired
    SymphonyRooms symphonyRooms;

    @Autowired
    History h;

    StanfordCoreNLP stanfordCoreNLP;
    Properties props;

    public StreamEventConsumerImpl() {
        super();
        initializingStanfordProperties();
    }

    public void initializingStanfordProperties(){
        props = new Properties();
        props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
        props.setProperty("ner.docdate.usePresent", "true");
        props.setProperty("sutime.includeRange", "true");
        props.setProperty("sutime.markTimeRanges", "true");
        stanfordCoreNLP = new StanfordCoreNLP(props);
    }


    @Override
    public void accept(V4Event t) {
        try {
                String messageInString = getMessageFromChatInString(t);
                String currentUser = returnCurrentUser(t);
                V4MessageSent v4MessageSent = t.getPayload().getMessageSent();

                CoreDocument document = new CoreDocument(messageInString);
                stanfordCoreNLP.annotate(document);
                for (CoreEntityMention cem : document.entityMentions()) {
                    System.out.println("temporal expression: " + cem.text());
                    System.out.println("temporal value: " +cem.coreMap().get(TimeAnnotations.TimexAnnotation.class));
                    Timex timex = cem.coreMap().get(TimeAnnotations.TimexAnnotation.class);

                    Reminder reminder = new Reminder();
                    reminder.setDescription(messageInString);
                    reminder.setAuthor(currentUser);
                    //Instant instantTimeForReminder = dateToInstant(timex);
                    reminder.setInstant(dateToInstant(timex));
                    Room r =symphonyRooms.loadRoomById(v4MessageSent.getMessage().getStream().getStreamId());
                    FormResponse formResponse = new FormResponse(workflow, r , new EntityJson(), "Create Reminder",
                            "do you want to be reminded about this time" ,
                            reminder, true, ButtonList.of(new Button("addreminder+0",
                            Button.Type.ACTION,"create Reminder")),null  );
                    responseHandler.accept(formResponse);

               }
        } catch (Exception e) {
            LOG.warn("Couldn't parse message", e);

        }

    }

    @Override
    public Consumer<V4Event> andThen(Consumer<? super V4Event> after) {
        return StreamEventConsumer.super.andThen(after);
    }
    public Instant dateToInstant(Timex timex) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        Instant instantTimeForReminder =sdf.parse(timex.value()).toInstant();
        //Instant displayReminderAt = instantTimeForReminder.minus(10, ChronoUnit.MINUTES);
        return instantTimeForReminder;

    }
    public String getMessageFromChatInString(V4Event t) throws Exception {
        if (t.getPayload().getMessageSent() != null) {
            V4MessageSent v4MessageSent = t.getPayload().getMessageSent();
            String messageText = v4MessageSent.getMessage().getMessage();
            String jsonText = v4MessageSent.getMessage().getData();

            EntityJson entityJson = entityJsonConverter.readValue(jsonText);
            Message m = simpleMessageParser.parseMessage(messageText, entityJson);
            String messageInString = m.getText();
            return messageInString;
        }
        return "No valid message sent by User";

    }

    public String returnCurrentUser(V4Event t){
//        if(t.getPayload().getMessageSent().getMessage().getUser().getFirstName() !=null) {
            V4User v4User = t.getPayload().getMessageSent().getMessage().getUser();

            return v4User.getDisplayName();
        //}
        //return "Invalid user";

    }
}
