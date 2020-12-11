package org.finos.symphony.toolkit.workflow.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class ButtonList {
	
	private Collection<Button> contents;

	public Collection<Button> getContents() {
		return contents;
	}

	public void setContents(Collection<Button> contents) {
		this.contents = contents;
	}

	public ButtonList() {
		this.contents = new ArrayList<Button>();
	}

	public ButtonList(Collection<Button> arg0) {
		this.contents = arg0;
	}

	public static ButtonList of(Button... button) {
		return new ButtonList(Arrays.asList(button));
	}

	public void add(Button button) {
		contents.add(button);
	}

	public int size() {
		return contents.size();
	}

	
}
