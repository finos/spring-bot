package org.finos.symphony.toolkit.tools.reminders;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.finos.symphony.toolkit.workflow.actions.Action;
import org.finos.symphony.toolkit.workflow.actions.SimpleMessageAction;
import org.finos.symphony.toolkit.workflow.actions.consumers.AbstractActionConsumer;
import org.finos.symphony.toolkit.workflow.annotations.WorkMode;
import org.finos.symphony.toolkit.workflow.content.Addressable;
import org.finos.symphony.toolkit.workflow.content.Message;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.history.History;
import org.finos.symphony.toolkit.workflow.response.WorkResponse;
import org.finos.symphony.toolkit.workflow.response.handlers.ResponseHandlers;
import org.finos.symphony.toolkit.workflow.sources.symphony.room.SymphonyRooms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ErrorHandler;

import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.pod.UsersApi;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.Timex;

@Component
public class TimeFinder extends AbstractActionConsumer  {

	public TimeFinder(ErrorHandler errorHandler) {
		super(errorHandler);
	}

	private static final Logger LOG = LoggerFactory.getLogger(TimeFinder.class);

	@Autowired
	UsersApi usersApi;

	@Autowired
	SymphonyRooms symphonyRooms;

	@Autowired
	SymphonyIdentity identity;

	Properties props;
	
	@Autowired
	History h;
	
	StanfordCoreNLP stanfordCoreNLP;
    ReminderProperties reminderProperties;
    
    @Autowired
    ResponseHandlers rh;

    @PostConstruct
	public void initializingStanfordProperties() {
		props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
		props.setProperty("ner.docdate.usePresent", "true");
		stanfordCoreNLP = new StanfordCoreNLP(props);
	}

    
    
	@Override
	public void accept(Action t) {
		Action.CURRENT_ACTION.set(t);
		try {
			if (t instanceof SimpleMessageAction) {
				Message m = ((SimpleMessageAction) t).getWords();
				User currentUser = t.getUser();
				Addressable a = t.getAddressable();
				String messageInString = m.getText();

				CoreDocument document = new CoreDocument(messageInString);
				stanfordCoreNLP.annotate(document);
				for (CoreEntityMention cem : document.entityMentions()) {
					System.out.println("temporal expression: " + cem.text());
					System.out.println("temporal value: " + cem.coreMap().get(TimeAnnotations.TimexAnnotation.class));
					Timex timex = cem.coreMap().get(TimeAnnotations.TimexAnnotation.class);
					
					LocalDateTime ldt = toLocalTime(timex);

					if (ldt != null) {
						Optional<ReminderList> rl = h.getLastFromHistory(ReminderList.class, a);
						int remindBefore;
						if (rl.isPresent()) {
							remindBefore = rl.get().getRemindBefore();
						} else {
							remindBefore = reminderProperties.getDefaultRemindBefore();
						}
						
						ldt = ldt.minus(remindBefore, ChronoUnit.MINUTES);
						
						Reminder reminder = new Reminder();
						reminder.setDescription(messageInString);
						reminder.setLocalTime(ldt);

						WorkResponse wr = new WorkResponse(a, reminder, WorkMode.EDIT);
						rh.accept(wr);
					}
				}
				
			}
		} catch (Exception e) {
			errorHandler.handleError(e);
		}
	}

	private LocalDateTime toLocalTime(Timex time) {
		if (time == null ) {
			return null;
		}
		
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
			Instant instantTimeForReminder;
			instantTimeForReminder = sdf.parse(time.value()).toInstant();
			return instantTimeForReminder.atZone(ZoneId.systemDefault()).toLocalDateTime();
		} catch (ParseException e) {
			LOG.warn("Couldn't parse timex: " + time.value());
			return null;
		}
	}
}
