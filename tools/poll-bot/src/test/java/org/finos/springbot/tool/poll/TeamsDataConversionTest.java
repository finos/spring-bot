package org.finos.springbot.tool.poll;

import org.finos.springbot.entityjson.EntityJson;
import org.finos.springbot.tool.poll.poll.Question;
import org.finos.springbot.workflow.content.User;
import org.finos.springbot.workflow.data.DataHandlerConfig;
import org.finos.springbot.workflow.data.EntityJsonConverter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {
		DataHandlerConfig.class
})
public class TeamsDataConversionTest {
	
	public static final String SOME_JSON = "{\n"
			+ "  \"formid\" : \"org.finos.springbot.tool.poll.poll.Question\",\n"
			+ "  \"buttons\" : {\n"
			+ "    \"type\" : \"org.finos.springbot.workflow.form.buttonList\",\n"
			+ "    \"version\" : \"1.0\",\n"
			+ "    \"contents\" : [ {\n"
			+ "      \"type\" : \"org.finos.springbot.workflow.form.button\",\n"
			+ "      \"version\" : \"1.0\",\n"
			+ "      \"name\" : \"org.finos.springbot.tool.poll.poll.PollController-poll0\",\n"
			+ "      \"buttonType\" : \"ACTION\",\n"
			+ "      \"text\" : \"kjh\"\n"
			+ "    }, {\n"
			+ "      \"type\" : \"org.finos.springbot.workflow.form.button\",\n"
			+ "      \"version\" : \"1.0\",\n"
			+ "      \"name\" : \"org.finos.springbot.tool.poll.poll.PollController-poll1\",\n"
			+ "      \"buttonType\" : \"ACTION\",\n"
			+ "      \"text\" : \"jh\"\n"
			+ "    }, {\n"
			+ "      \"type\" : \"org.finos.springbot.workflow.form.button\",\n"
			+ "      \"version\" : \"1.0\",\n"
			+ "      \"name\" : \"org.finos.springbot.tool.poll.poll.PollController-poll2\",\n"
			+ "      \"buttonType\" : \"ACTION\",\n"
			+ "      \"text\" : \"kkjh\"\n"
			+ "    } ]\n"
			+ "  },\n"
			+ "  \"form\" : {\n"
			+ "    \"type\" : \"org.finos.springbot.tool.poll.poll.question\",\n"
			+ "    \"version\" : \"1.0\",\n"
			+ "    \"question\" : \"hkjhjkj\",\n"
			+ "    \"options\" : [ \"kjh\", \"jh\", \"kkjh\" ],\n"
			+ "    \"id\" : \"ffe0d5988ab7\",\n"
			+ "    \"poller\" : {\n"
			+ "      \"type\" : \"org.finos.springbot.teams.content.teamsUser\",\n"
			+ "      \"version\" : \"1.0\",\n"
			+ "      \"aadObjectId\" : \"4a2777dd-0619-4f1e-b9d4-1112a701a40d\",\n"
			+ "      \"key\" : \"29:1nKqklVb4WIofhY4D5N3WoyKsl35ekq-qV-5lliPFJ-0E3RVd3gXp-ylXSKy9ixB3t2S-39Jc7lDztlD585iqOQ\",\n"
			+ "      \"name\" : \"Rob Moffat\"\n"
			+ "    },\n"
			+ "    \"endTime\" : null\n"
			+ "  },\n"
			+ "  \"header\" : {\n"
			+ "    \"type\" : \"org.finos.springbot.workflow.tags.headerDetails\",\n"
			+ "    \"version\" : \"1.0\",\n"
			+ "    \"name\" : null,\n"
			+ "    \"description\" : null,\n"
			+ "    \"tags\" : [ \"ffe0d5988ab7-q\", \"org-finos-springbot-tool-poll-poll-question\" ]\n"
			+ "  },\n"
			+ "  \"errors\" : {\n"
			+ "    \"type\" : \"org.finos.springbot.workflow.form.errorMap\",\n"
			+ "    \"version\" : \"1.0\",\n"
			+ "    \"contents\" : { }\n"
			+ "  },\n"
			+ "  \"storageId\" : \"9223370375352721108-8e7238cc-5f40-4a6d-a0ee-008e6ae96eff\"\n"
			+ "}";
	
	@Autowired
	EntityJsonConverter ejc;
	
	@Test
	public void testDeserialize() {
		EntityJson data = ejc.readValue(SOME_JSON);
		Question q = (Question) data.get("form");
		User tu = q.getPoller();
		System.out.println(tu);
	}
	
	
}
