package org.finos.springbot.teams.state;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.finos.springbot.teams.TeamsException;
import org.finos.springbot.teams.state.TeamsStateStorage.*;


public abstract class AbstractStateStorageTest {

	protected TeamsStateStorage tss;
	
	@Test
	public void testStoreAndRetrieveNoTags() {
		Map<String, Object> somedata = Collections.singletonMap("a", "b");
		Assertions.assertEquals(Optional.empty(), tss.retrieve("nonfile"));
		assertThrows(TeamsException.class, () -> tss.store("thefile", Collections.emptyMap(), somedata));
	}
	
	@Test
	public void testStoreWithTags() throws IOException {
		Map<String, Object> somedata = Collections.singletonMap("a", "b");
		Map<String, String> tags = Collections.singletonMap("rob", "tag");
		List<TeamsStateStorage.Filter> tagList = Collections.singletonList(new Filter("rob", "tag", "="));
		List<TeamsStateStorage.Filter> otherTagList = Collections.singletonList(new Filter("lag", "rob", "="));
		
		tss.store("thefile", tags, somedata);
		Assertions.assertEquals(somedata, tss.retrieve("thefile").get());
		Assertions.assertEquals(Collections.singletonList(somedata), hoover(tss.retrieve(tagList, false)));
		Assertions.assertEquals(Collections.emptyList(), hoover(tss.retrieve(otherTagList, false)));
	}
	
	@Test
	public void testStoreWithTagDates() throws IOException {
		Map<String, Object> somedata = Collections.singletonMap("a", "b");
		Map<String, String> tags = new HashMap<String, String>();
		tags.put("date", "20220513");
		tags.put("name", "rob");

		List<TeamsStateStorage.Filter> tagList1 = Arrays.asList(
				new Filter("date", "20220501", ">="));

		List<TeamsStateStorage.Filter> tagList2 = Arrays.asList(
				new Filter("date", "20220501", ">="),
				new Filter("name", "rob", "=")) ;

		List<TeamsStateStorage.Filter> tagList3 = Arrays.asList(
				new Filter("date", "20220501", "<"));
		
		List<TeamsStateStorage.Filter> tagList4 = Arrays.asList(
				new Filter("date", "20220601", "<"));

		
		tss.store("thefile", tags, somedata);
		tss.store("theotherfile", tags, somedata);
		
		Assertions.assertEquals(2, hoover(tss.retrieve(tagList1, false)).size());
		Assertions.assertEquals(2, hoover(tss.retrieve(tagList2, false)).size());
		Assertions.assertEquals(0, hoover(tss.retrieve(tagList3, false)).size());
		Assertions.assertEquals(2, hoover(tss.retrieve(tagList4, false)).size());
	}
	
	public List<Map<String, Object>> hoover(Iterable<Map<String, Object>> iterable) {
		List<Map<String, Object>> result =  StreamSupport.stream(iterable.spliterator(), false)
			    .collect(Collectors.toList());
		return result;
	}
}
