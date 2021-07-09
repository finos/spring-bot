package org.finos.symphony.toolkit.workflow.sources.symphony.content;

import java.util.function.Supplier;

import org.finos.symphony.toolkit.workflow.content.User;

public class SymphonyUser extends TagDef implements User, SymphonyContent, SymphonyAddressable {
	
	protected String emailAddress;
	private transient String streamId;
	private Supplier<String> streamIdSupplier;
	
	public SymphonyUser() {
		super();
	}

	public SymphonyUser(String id, String name, String emailAddress, Supplier<String> streamIdSupplier) {
		super(id, name, Type.USER);
		this.emailAddress = emailAddress;
		this.streamIdSupplier = streamIdSupplier;
	}	
	
	public SymphonyUser(long id, String name, String emailAddress, Supplier<String> streamIdSupplier) {
		this(Long.toString(id), name, emailAddress, streamIdSupplier);
	}
	
	public SymphonyUser(long id, String name, String emailAddress, String streamId) {
		this(id, name, emailAddress, () -> streamId);
	}

	public String getEmailAddress() {
		return emailAddress;
	}
	
	@Override
	public Type getTagType() {
		return Type.USER;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((emailAddress == null) ? 0 : emailAddress.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SymphonyUser other = (SymphonyUser) obj;
		if (emailAddress == null) {
			if (other.emailAddress != null)
				return false;
		} else if (!emailAddress.equals(other.emailAddress))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "UserDef [emailAddress=" + getEmailAddress() + ", name=" + getName() + ", id=" + getId() + "]";
	}

	@Override
	public String getStreamId() {
		streamId = streamId == null ? streamIdSupplier.get() : streamId;
		return streamId;
	}

	

}
