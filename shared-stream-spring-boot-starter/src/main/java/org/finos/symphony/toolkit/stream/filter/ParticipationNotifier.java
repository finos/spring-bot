package org.finos.symphony.toolkit.stream.filter;

import java.util.concurrent.TimeUnit;

import org.finos.symphony.toolkit.stream.Participant;
import org.finos.symphony.toolkit.stream.log.SharedLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;

public class ParticipationNotifier implements InitializingBean {
	
	private Logger LOG = LoggerFactory.getLogger(ParticipationNotifier.class);

	private final SharedLog sl;
	private final Participant p;
	private final TaskScheduler scheduler;
	private final long interval;
	
	public ParticipationNotifier(SharedLog sl, Participant p, TaskScheduler ts, long interval) {
		this.sl = sl;
		this.p = p;
		this.scheduler = ts;
		this.interval = interval;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		writeMessage();
		scheduler.schedule(() -> writeMessage(), new PeriodicTrigger(interval, TimeUnit.MILLISECONDS));
		LOG.info("Scheduled Participation Message every {} ms", interval);
	}

	protected void writeMessage() {
		LOG.debug("Writing participation message to {} for {}", sl, p);
		sl.writeParticipantMessage(p);
	}
	
	
}
