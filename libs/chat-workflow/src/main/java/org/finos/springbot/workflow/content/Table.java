package org.finos.springbot.workflow.content;

import java.util.List;
import java.util.Objects;

/**
 * Represents a table pasted into a chat.
 * 
 * @author Rob Moffat
 *
 */
public interface Table extends Content {

	public List<Content> getColumnNames();
	
	public List<List<Content>> getData();
	
	public static class TableImpl implements Table {
		
		private final List<Content> columns;
		
		private final List<List<Content>> data;
		
		public TableImpl(List<Content> columns, List<List<Content>> data) {
			super();
			this.columns = columns;
			this.data = data;
		}

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
		
		@Override
		public List<List<Content>> getData() {
			return data;
		}
		
		@Override
		public List<Content> getColumnNames() {
			return columns;
		}

		@Override
		public int hashCode() {
			return Objects.hash(columns, data);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			} else if (obj instanceof Table) {
				Table other = (Table) obj;
				return Objects.equals(columns, other.getColumnNames()) && Objects.equals(data, other.getData());
			} else {
				return false;
			}
		}
		
		
	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Table of(List<? extends Content> headers, List<List<? extends Content>> data) {
		return new TableImpl((List<Content>) headers, (List<List<Content>>) (List) data);
	}
}
