package org.finos.symphony.toolkit.workflow.content;

public class UserDef extends TagDef implements Author {
	
	protected String address;
	
	public UserDef() {
		super();
	}

	public UserDef(String id, String name, String address) {
		super(id, name, Type.USER);
		this.address = address;
	}

	public String getAddress() {
		return address;
	}
	
	@Override
	public Type getTagType() {
		return Type.USER;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((address == null) ? 0 : address.hashCode());
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
		UserDef other = (UserDef) obj;
		if (address == null) {
			if (other.address != null)
				return false;
		} else if (!address.equals(other.address))
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
		return "UserDef [address=" + getAddress() + ", name=" + getName() + ", id=" + getId() + "]";
	}

	

}
