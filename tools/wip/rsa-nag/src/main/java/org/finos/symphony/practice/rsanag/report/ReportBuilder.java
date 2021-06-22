package org.finos.symphony.practice.rsanag.report;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.mail.internet.MimeMessage;

import org.finos.symphony.practice.rsanag.Config;
import org.finos.symphony.practice.rsanag.RsaNagProperties;
import org.finos.symphony.practice.rsanag.report.UserRecord.Action;
import org.finos.symphony.toolkit.workflow.content.Room;
import org.finos.symphony.toolkit.workflow.history.History;
import org.finos.symphony.toolkit.workflow.java.Exposed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import com.symphony.api.agent.AuditTrailApi;
import com.symphony.api.model.V1AuditTrailInitiatorList;
import com.symphony.api.model.V1AuditTrailInitiatorResponse;
import com.symphony.api.model.V2UserDetail;
import com.symphony.api.model.V2UserDetailList;
import com.symphony.api.pod.UserApi;

public class ReportBuilder {
	
	public static final Logger LOG = LoggerFactory.getLogger(ReportBuilder.class);
	public static final long ONE_DAY_MILLIS = 24*60*60*1000;

	@Autowired
	UserApi userApi;
	
	@Autowired
	AuditTrailApi auditApi;
	
	@Autowired
	History history;
	
	@Autowired
	RsaNagProperties properties;
	
	@Autowired
	JavaMailSender mailSender;
	
	boolean sendEmails;
	boolean expireBots;

	public ReportBuilder(boolean sendEmails, boolean expireBots) {
		super();
		this.sendEmails = sendEmails;
		this.expireBots = expireBots;
	}

	@Exposed(description = "Update the report of RSA Usage on the pod", rooms = Config.RSA_KEY_AUDIT_ROOM)
	public Report report(Room in) throws Exception {
		return generateReportInner(in, Instant.now());
	}

	public Report generateReportInner(Room in, Instant atTime) {
		Report previous = history.getLastFromHistory(Report.class, in).orElse(null);
		Report newReport = new Report();
		newReport.setReportDate(atTime);
		
		List<V2UserDetail> actOn = getAllAccountsWithRsaKeys();
		
		for (V2UserDetail v2UserDetail : actOn) {
			UserRecord old = getOldRecord(previous, v2UserDetail);
			UserRecord r = withDetail(v2UserDetail, old);
			newReport.getRecords().add(r);
		}
		
		streamAuditItemsInRange(previous, newReport);
				
		Collections.sort(newReport.getRecords(), ALPHABETICAL);
		
		calculateExpiryRules(newReport);
		
		if (sendEmails) {
			sendNagAndExpiryMessages(newReport);
		}
		
		if (expireBots) {
			expireBots(newReport);
		}
		
		return newReport;
	}
	
	private void expireBots(Report newReport) {
		// TODO Auto-generated method stub
		
	}

	private void sendNagAndExpiryMessages(Report newReport) {
		for (UserRecord ur : newReport.getRecords()) {
			String template = null;
			String subject = null; 
			switch (ur.action) {
			case EXPIRE:
				template = properties.getExpiryTemplate();
				subject = "SYMPHONY BOT  - RSA KEY EXPIRED: "+ur.botDisplayName+"("+ur.email+")";
				break;
			case WARN:
				template = properties.getNagTemplate();
				subject = "SYMPHONY BOT  - RSA KEY EXPIRY WARNING: "+ur.botDisplayName+"("+ur.email+")";
				break;
			case OK:
			default:
			}
			
			if (template != null) {
				
				MimeMessagePreparator mail = mm -> 
				new SimpleMailMessage();
				mail.setTo(ur.getEmail());
				mail.setSubject(subject);
				mail.setFrom(properties.getEmailFrom());
				mail.setText(subject);
				mail.
				  MimeMessage message = new MimeMessage(mailSender.);
				    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
				    helper.setTo(ur.getEmail());
				    helper.setSubject(subject);
				    helper.setText(htmlBody, true);
				    emailSender.send(message);
			}
			
		}
		
	}

	private void calculateExpiryRules(Report newReport) {
		Instant until = newReport.getReportDate();
		for (UserRecord ur : newReport.getRecords()) {
			Duration d = Duration.between(ur.getKeyLastUpdated(), until);
			long days = d.getSeconds() / (60*60*24);
			ur.setKeyAgeDays((int) days);
			
			if (days > properties.getMaxKeyLifetime()) {
				ur.action = Action.EXPIRE;
			} else if (days > (properties.getMaxKeyLifetime() - properties.getNagWithin())) {
				ur.action = Action.WARN;
			} else {
				ur.action = Action.OK;
			}
		}
	}

	private void streamAuditItemsInRange(Report previous, Report newReport) {
		Instant from = getFromDate(previous, newReport);
		Instant to = newReport.getReportDate();
		
		// retrieve entries one week at a time, from oldest to newest.
		while (from.compareTo(to) == -1) {
			Instant nextWeek = from.plus(24 * 7, ChronoUnit.HOURS);
			
			if (nextWeek.isAfter(to)) {
				nextWeek = to;
			}
			
			String after = null;
			do {
				V1AuditTrailInitiatorList list = auditApi.v1AudittrailPrivilegeduserGet(null, null, 
					from.toEpochMilli(), to.toEpochMilli(), 
					null, after, 500, null, null);
			
				for (V1AuditTrailInitiatorResponse r : list.getItems()) {
					if (r.getActionName().equals("rsaKeyAdded")) {
						handleKeyUpdate(r, newReport);
					}
				}
				
				
				after = list.getPagination().getCursors().getAfter();
			
			} while (after != null);
			
			from = nextWeek;
		}
	}

	private void handleKeyUpdate(V1AuditTrailInitiatorResponse r, Report newReport) {
		Instant changeAt = Instant.ofEpochMilli(Long.parseLong(r.getTimestamp()));
		//long botId = r.get
		
		// todo  - figure out what happens here and write it.
	}

	private Instant getFromDate(Report previous, Report newReport) {
		// oldest possible from-date.
		Instant outOfDate = LocalDateTime.ofInstant(newReport.getReportDate(), ZoneOffset.UTC)
				.minus(properties.getMaxKeyLifetime(), ChronoUnit.DAYS)
				.toInstant(ZoneOffset.UTC);
		
		if (previous != null) {
			// we only need to get from the last report.
			Instant lastReportDate = previous.getReportDate();
			if (lastReportDate.isAfter(outOfDate)) {
				outOfDate = lastReportDate;
			}
		}
		
		return outOfDate;
	}

	private static Comparator<UserRecord> ALPHABETICAL = new Comparator<UserRecord>() {

		@Override
		public int compare(UserRecord o1, UserRecord o2) {
			return o1.getBotDisplayName().compareTo(o2.getBotDisplayName());
		}
	
	};

	private UserRecord getOldRecord(Report previous, V2UserDetail v2UserDetail) {
		if (previous != null) {
			for (UserRecord ur : previous.getRecords()) {
				if (ur.getBotId() == v2UserDetail.getUserSystemInfo().getId()) {
					return ur;
				}
			}
		} 
		
		return null;
	}

	private UserRecord withDetail(V2UserDetail v2UserDetail, UserRecord old) {
		UserRecord out = new UserRecord();
		out.setBotDisplayName(v2UserDetail.getUserAttributes().getDisplayName());
		out.setBotId(v2UserDetail.getUserSystemInfo().getId());
		out.setEmail(v2UserDetail.getUserAttributes().getEmailAddress());
		out.setStatus(v2UserDetail.getUserSystemInfo().getStatus());

		// user created info
		Instant userCreated = Instant.ofEpochMilli(v2UserDetail.getUserSystemInfo().getCreatedDate());
		out.setKeyLastUpdated(userCreated);
		
		// user last updated info - never null
		Long lastUpdatedDate = v2UserDetail.getUserSystemInfo().getLastUpdatedDate();
		if (lastUpdatedDate != null) {
			out.setUserLastUpdated(Instant.ofEpochMilli(lastUpdatedDate));
			out.setUserLastUpdated(userCreated);
		}
		
		// key last updated - also never null
		if (old != null) {
			out.setKeyLastUpdated(old.getKeyLastUpdated());
		} else {
			out.setKeyLastUpdated(userCreated);
		}
		
		return out;
	}

	private List<V2UserDetail> getAllAccountsWithRsaKeys() {
		List<V2UserDetail> out = new ArrayList<>();
		int skip = 0;
		V2UserDetailList udl;
		do {
			udl = userApi.v2AdminUserListGet(null, skip, 3000);
			udl.forEach(c -> {
				if (c.getUserAttributes().getCurrentKey() != null) {				
					 out.add(c);
				}	
			});
			
			skip += 3000;
		} while (udl.size() == 3000);
		
		LOG.info("Found "+out.size()+" matching RSA Accounts");
		
		return out;
	}

}
