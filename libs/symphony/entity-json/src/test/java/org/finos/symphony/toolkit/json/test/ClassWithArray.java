package org.finos.symphony.toolkit.json.test;

import java.util.List;
import java.util.Objects;

public class ClassWithArray {
	
	public static class SubClass {
		
		String a;
		String b;
		
		public String getA() {
			return a;
		}
		public void setA(String a) {
			this.a = a;
		}
		public String getB() {
			return b;
		}
		public void setB(String b) {
			this.b = b;
		}
		public SubClass() {
			super();
		}
		
		public SubClass(String a, String b) {
			super();
			this.a = a;
			this.b = b;
		}
		@Override
		public int hashCode() {
			return Objects.hash(a, b);
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SubClass other = (SubClass) obj;
			return Objects.equals(a, other.a) && Objects.equals(b, other.b);
		}
		
		
	}
	
	
	Object l;


	public Object getL() {
		return l;
	}


	public void setL(Object l) {
		this.l = l;
	}


	List<? extends Object> m;


	public List<? extends Object> getM() {
		return m;
	}


	public void setM(List<? extends Object> m) {
		this.m = m;
	}


	@Override
	public int hashCode() {
		return Objects.hash(l, m);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClassWithArray other = (ClassWithArray) obj;
		return Objects.equals(l, other.l) && Objects.equals(m, other.m);
	}



	
}
