package org.finos.symphony.toolkit.workflow.sources.symphony.content;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.finos.symphony.toolkit.workflow.content.User;
import org.symphonyoss.TaxonomyElement;

import com.symphony.user.DisplayName;
import com.symphony.user.EmailAddress;
import com.symphony.user.Mention;
import com.symphony.user.UserId;

public class SymphonyUser extends Mention implements User, SymphonyContent, SymphonyAddressable {
	
	private transient String streamId;
	private transient Supplier<String> streamIdSupplier;
	
	public SymphonyUser() {
		super();
	}

	public SymphonyUser(long userId) {
		super(createTaxonomy(userId, null, null));
	}
	
	private static List<TaxonomyElement> createTaxonomy(Long userId, String displayName, String emailAddress) {
		List<TaxonomyElement> out = new ArrayList<TaxonomyElement>();
		if (userId != null) {
			out.add(new UserId(""+userId));
		}
		if (displayName != null) {
			out.add(new DisplayName(displayName));
		}
		if (emailAddress != null) {
			out.add(new EmailAddress(emailAddress));
		}
		
		return out;
	}

	public SymphonyUser(String streamId, String name, String emailAddress) {
		super(createTaxonomy(null, name, emailAddress));
		this.streamId = streamId;
	}	
	
	public SymphonyUser(Supplier<String> streamIdSupplier, String name, String emailAddress) {
		super(createTaxonomy(null, name, emailAddress));
		this.streamIdSupplier = streamIdSupplier;
	}	
	
	public String getEmailAddress() {
		return fromTaxonomy(EmailAddress.class);
	}

	private String fromTaxonomy(Class<?> class1) {
		return getId().stream()
			.filter(t -> class1.isAssignableFrom(t.getClass()))
			.findFirst()
			.map(te -> te.getValue())
			.orElse(null);
	}


	@Override
	public String toString() {
		return "SymphonyUser [getId()=" + getId() + "]";
	}

	@Override
	public String getStreamId() {
		streamId = streamId == null ? streamIdSupplier.get() : streamId;
		return streamId;
	}

	@Override
	public String getName() {
		return fromTaxonomy(DisplayName.class);
	}

	@Override
	public Type getTagType() {
		return USER;
	}

	
	public String getUserId() {
		return fromTaxonomy(UserId.class);
	}
	

}
