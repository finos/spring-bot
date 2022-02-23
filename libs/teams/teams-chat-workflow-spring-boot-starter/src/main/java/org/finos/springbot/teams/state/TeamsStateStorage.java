package org.finos.springbot.teams.state;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TeamsStateStorage {

	public static final String PRESENT = "tag";
	public static final String ADDRESSABLE_KEY = "chat";

	public class Filter {
		
		final String key;
		final String value;
		final String operator;
		
		public Filter(String key) {
			this(key, PRESENT, "=");
		}
		
		public Filter(String key, String value, String operator) {
			super();
			this.key = key;
			this.value = value;
			this.operator = operator;
		}
	}
	
	/**
	 * Returns a unique ID that can be used to retrieve/store data.
	 */
	public String createStorageId();
	
	public void store(String file, Map<String, String> tags, Map<String, Object> data); 
	
	public Iterable<Map<String, Object>> retrieve(List<Filter> tags, int maxPageSize);

	public Optional<Map<String, Object>> retrieve(String file);

}
