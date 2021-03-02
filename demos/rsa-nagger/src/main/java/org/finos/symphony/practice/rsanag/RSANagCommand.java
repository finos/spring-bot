package org.finos.symphony.practice.rsanag;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.mail.MailSender;
import org.springframework.util.StringUtils;

import com.symphony.api.agent.AuditTrailApi;
import com.symphony.api.model.UserSystemInfo;
import com.symphony.api.model.V2UserAttributes;
import com.symphony.api.model.V2UserDetail;
import com.symphony.api.model.V2UserDetailList;
import com.symphony.api.model.V2UserKeyRequest;
import com.symphony.api.pod.UserApi;

public class RSANagCommand implements CommandLineRunner {
	
	public static final Logger LOG = LoggerFactory.getLogger(RSANagCommand.class);
	public static final long ONE_DAY_MILLIS = 24*60*60*1000;
	
	boolean list = false;
	boolean mail = false;
	boolean revoke = false;
	
	/**
	 * Allows you to process a single bot, rather than all of them
	 */
	@Value("${bot}")
	String bot;
	
	/**
	 * Max Lifetime of the RSA Key (default = 1 year)
	 */
	@Value("${days:365}")
	int days;
	
	/**
	 * Time within which nag messages need to be sent.
	 */
	@Value("${nagWithin:28}")
	int nagWithin;
	
	@Autowired
	MailSender mailSender;
	
	@Autowired
	UserApi userApi;
	
	@Autowired
	AuditTrailApi auditApi;

	@Override
	public void run(String... args) throws Exception {
		list = argContains("list", args);
		mail = argContains("mail", args);
		revoke = argContains("revoke", args);
		
		List<V2UserDetail> actOn = getAllAccounts();
		
		for (V2UserDetail v2UserDetail : actOn) {
			Report r = getAuditDetail(v2UserDetail);
		}
		
		
	}

	private Report withDetail(V2UserDetail v2UserDetail) {
		UserSystemInfo info = v2UserDetail.getUserSystemInfo();
		V2UserAttributes atts = v2UserDetail.getUserAttributes();
		V2UserKeyRequest ukr = atts.getCurrentKey();
		
	}

	private List<V2UserDetail> getAllAccounts() {
		List<V2UserDetail> out = new ArrayList<>();
		int skip = 0;
		V2UserDetailList udl;
		do {
			udl = userApi.v2AdminUserListGet(null, skip, 3000);
			udl.forEach(c -> {
				if (StringUtils.hasText(bot)) {
					if (c.getUserAttributes().getEmailAddress().equals(bot)) {
						out.add(c);
					}
					
				} else if (c.getUserAttributes().getCurrentKey() != null) {				
					 out.add(c);
				}	
			});
			
			skip += 3000;
		} while (udl.size() == 3000);
		
		LOG.info("Found "+out.size()+" matching RSA Accounts");
		
		return out;
	}

	private boolean argContains(String string, String[] args) {
		for (String string2 : args) {
			if (string.equals(string2.toLowerCase())) {
				return true;
			}
		}
		
		return false;
	}

}
