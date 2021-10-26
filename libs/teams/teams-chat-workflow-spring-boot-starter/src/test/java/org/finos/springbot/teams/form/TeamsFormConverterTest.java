package org.finos.springbot.teams.form;

import org.finos.springbot.tests.form.AbstractFormConverterTest;
import org.finos.springbot.workflow.form.FormConverter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class TeamsFormConverterTest extends AbstractFormConverterTest {

	@Override
	protected void before() {
		ObjectMapper om = new ObjectMapper();
		om.registerModule(new JavaTimeModule());
		//om.registerModule(new TeamsModule());
		this.fc = new FormConverter(om);
	}

	
}
