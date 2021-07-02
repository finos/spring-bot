package org.finos.symphony.toolkit.tools.reminders;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.content.User;
import org.finos.symphony.toolkit.workflow.form.Button;
import org.finos.symphony.toolkit.workflow.form.ButtonList;
import org.finos.symphony.toolkit.workflow.history.History;
import org.finos.symphony.toolkit.workflow.response.FormResponse;
import org.finos.symphony.toolkit.workflow.response.Response;
import org.finos.symphony.toolkit.workflow.sources.symphony.messages.SimpleMessageAction;
import org.finos.symphony.toolkit.workflow.sources.symphony.messages.SimpleMessageConsumer;
import org.finos.symphony.toolkit.workflow.sources.symphony.room.SymphonyRooms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.symphony.api.id.SymphonyIdentity;
import com.symphony.api.pod.UsersApi;

import edu.stanford.nlp.pipeline.CoreDocument;
import edu.stanford.nlp.pipeline.CoreEntityMention;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.time.TimeAnnotations;
import edu.stanford.nlp.time.Timex;

public class TimeFinder implements SimpleMessageConsumer {

	private static final Logger LOG = LoggerFactory.getLogger(TimeFinder.class);

	@Autowired
	Workflow workflow;

	@Autowired
	UsersApi usersApi;

	@Autowired
	SymphonyRooms symphonyRooms;

	@Autowired
	SymphonyIdentity identity;

	StanfordCoreNLP stanfordCoreNLP;
	Properties props;
	
	@Autowired
	History h;
	
	@Autowired
	ReminderProperties reminderProperties;

	public TimeFinder() {
		super();
		initializingStanfordProperties();
	}

	public void initializingStanfordProperties() {
		props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,pos,lemma,ner");
		props.setProperty("ner.docdate.usePresent", "true");
		props.setProperty("sutime.includeRange", "true");
		props.setProperty("sutime.markTimeRanges", "true");
		stanfordCoreNLP = new StanfordCoreNLP(props);
	}

	/**
	 * Bot listens to everything in the room
	 */
	@Override
	public boolean requiresAddressing() {
		return false;
	}

	@Override
	public List<Response> apply(SimpleMessageAction action) {
		String messageInString = action.getWords().getText();
		User currentUser = action.getUser();

		CoreDocument document = new CoreDocument(messageInString);
		stanfordCoreNLP.annotate(document);
		List<Response> responses = new ArrayList<Response>();
		for (CoreEntityMention cem : document.entityMentions()) {
			System.out.println("temporal expression: " + cem.text());
			System.out.println("temporal value: " + cem.coreMap().get(TimeAnnotations.TimexAnnotation.class));
			Timex timex = cem.coreMap().get(TimeAnnotations.TimexAnnotation.class);

			LocalDateTime ldt = toLocalTime(timex);

			if (ldt != null) {
				Optional<ReminderList> rl = h.getLastFromHistory(ReminderList.class, action.getAddressable());
				int remindBefore;
				if (rl.isPresent()) {
					remindBefore = rl.get().getRemindBefore();
				} else {
					remindBefore = reminderProperties.getDefaultRemindBefore();
				}
				
				ldt = ldt.minus(remindBefore, ChronoUnit.MINUTES);
				
				Reminder reminder = new Reminder();
				reminder.setDescription(messageInString);
				reminder.setAuthor(currentUser);
				reminder.setLocalTime(ldt);

				FormResponse formResponse = new FormResponse(workflow, action.getAddressable(), new EntityJson(),
						"Create Reminder", "do you want to be reminded about this time", reminder, true,
						ButtonList.of(new Button("addreminder+0", Button.Type.ACTION, "create Reminder")), null);

				responses.add(formResponse);
			}
		}

		return responses;
	}

	private LocalDateTime toLocalTime(Timex time) {
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
