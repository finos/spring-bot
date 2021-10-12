package org.finos.symphony.toolkit.workflow.content;

import java.util.List;

public abstract class AbstractOrderedContent<C extends Content> implements OrderedContent<C> {
	
	private final List<C> c;

	public AbstractOrderedContent(List<C> c) {
		super();
		this.c = c;
	}

	@Override
	public List<C> getContents() {
		return c;
	}

	@Override
	public int hashCode() {
		return c.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj.getClass()==this.getClass()) {
			return getContents().equals(((OrderedContent<?>) obj).getContents());
		} else {
			return false;
		}
	}

	@Override
	public String getText() {
		return getContents().stream()
			.map(e -> e.getText())
			.reduce("", (a, b) -> {
				if (a.length() == 0 || b.length() == 0) {
					return a+b;
				} else {
					return a+" "+b;
				}
			});
	}
	
}
