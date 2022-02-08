package org.finos.springbot.tests.form;

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
	
	
}
