package com.github.deutschebank.symphony.json.test;

public class ClassWithEnum {

	public enum Choice { A, B, C }
	
	
	public String name;
	public Choice c;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Choice getC() {
		return c;
	}
	public void setC(Choice c) {
		this.c = c;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClassWithEnum other = (ClassWithEnum) obj;
		if (c != other.c)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	
}
