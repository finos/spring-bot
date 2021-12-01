package org.finos.springbot.symphony.content;

import java.util.ArrayList;
import java.util.List;

import org.finos.springbot.workflow.annotations.Work;
import org.finos.springbot.workflow.content.Tag;
import org.finos.springbot.workflow.content.User;
import org.symphonyoss.TaxonomyElement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.symphony.user.DisplayName;
import com.symphony.user.EmailAddress;
import com.symphony.user.Mention;
import com.symphony.user.StreamID;
import com.symphony.user.UserId;

@Work(jsonTypeName = { "com.symphony.user.mention", "org.finos.symphony.toolkit.workflow.content.userDef"}, index = false)
public final class SymphonyUser extends Mention implements User, SymphonyContent, SymphonyAddressable, Tag {
		
	public SymphonyUser() {
		super();
	}

	public SymphonyUser(long userId) {
		super(createTaxonomy(userId, null, null));
	}
	
	/**
	 * This ensures that each element of the taxonomy appears in the same place in the list each time
	 */
	private static List<TaxonomyElement> createTaxonomy(Long userId, String displayName, String emailAddress) {
		List<TaxonomyElement> out = new ArrayList<TaxonomyElement>();
		if (userId != null) {
			out.add(new UserId(""+userId));
		} else {
			out.add(null);
		}
		if (displayName != null) {
			out.add(new DisplayName(displayName));
		} else {
			out.add(null);
		}
		if (emailAddress != null) {
			out.add(new EmailAddress(emailAddress));
		} else {
			out.add(null);
		}
		
		return out;
	}

	public SymphonyUser(long userId, String name, String emailAddress) {
		super(createTaxonomy(userId, name, emailAddress));
	}	
	
	public SymphonyUser(String name, String emailAddress) {
		super(createTaxonomy(null, name, emailAddress));
	}	
	
	@JsonIgnore
	public String getEmailAddress() {
		return fromTaxonomy(EmailAddress.class);
	}

	@Override
	public String toString() {
		return "SymphonyUser [getId()=" + getId() + "]";
	}

	@JsonIgnore
	public String getStreamId() {
		return fromTaxonomy(StreamID.class);
	}

	@JsonIgnore
	@Override
	public String getName() {
		return fromTaxonomy(DisplayName.class);
	}

	@JsonIgnore
	@Override
	public Type getTagType() {
		return MENTION;
	}

	
	@JsonIgnore
	public String getUserId() {
		return fromTaxonomy(UserId.class);
	}

	@JsonIgnore
	@Override
	public String getKey() {
		return getUserId();
	}
	

}
