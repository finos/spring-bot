package org.finos.springbot.tests.form;

import java.util.Objects;

import org.finos.springbot.workflow.annotations.Work;

@Work()
public class Primitives {

	enum Meal { BREAKFAST, LUNCH, DINNER };
	
	private String a;
	private boolean b;
	private int c;
	private Meal m;
	
	public String getA() {
		return a;
	}
	public void setA(String a) {
		this.a = a;
	}
	public boolean isB() {
		return b;
	}
	public void setB(boolean b) {
		this.b = b;
	}
	public Meal getM() {
		return m;
	}
	public void setM(Meal m) {
		this.m = m;
	}
	public int getC() {
		return c;
	}
	public void setC(int c) {
		this.c = c;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(a, b, c, m);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Primitives other = (Primitives) obj;
		return Objects.equals(a, other.a) && b == other.b && c == other.c && m == other.m;
	}
	@Override
	public String toString() {
		return "Primitives [a=" + a + ", b=" + b + ", c=" + c + ", m=" + m + "]";
	}
	
	
	
}
