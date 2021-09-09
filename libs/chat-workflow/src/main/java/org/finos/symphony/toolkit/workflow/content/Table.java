package org.finos.symphony.toolkit.workflow.content;

import java.util.List;

/**
 * Represents a table pasted into a chat.
 * 
 * @author Rob Moffat
 *
 */
public interface Table extends Content {

	public List<Content> getColumnNames();
	
	public List<List<Content>> getData();

	public static Table of(List<? extends Content> headers, List<List<? extends Content>> data) {
		return new Table() {
			
			@Override
			public String getText() {
				return getColumnNames().stream()
					.map(e -> e.getText())
					.reduce("", (a, b) -> a + " " + b) + 
					getData().stream()
					.flatMap(e -> e.stream())
					.map(e -> e.getText())
					.reduce("", (a, b) -> a + " " + b);
			}
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public List<List<Content>> getData() {
				return (List<List<Content>>) (List) data;
			}
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public List<Content> getColumnNames() {
				return (List<Content>) (List) headers;
			}
		};
	}
}
