package org.finos.springbot.teams.controller;

import org.finos.springbot.workflow.controller.AbstractHandlerMappingTest;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {
		AbstractMockSymphonyTest.MockConfiguration.class, 
		SymphonyWorkflowConfig.class,
})
public class HandlerMappingTest extends AbstractHandlerMappingTest {

}
