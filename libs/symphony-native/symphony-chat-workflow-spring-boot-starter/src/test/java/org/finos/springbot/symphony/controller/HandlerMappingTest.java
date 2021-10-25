package org.finos.springbot.symphony.controller;

import org.finos.springbot.tests.controller.AbstractHandlerMappingTest;
import org.finos.symphony.toolkit.workflow.AbstractMockSymphonyTest;
import org.finos.symphony.toolkit.workflow.sources.symphony.SymphonyWorkflowConfig;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest(classes = {
		AbstractMockSymphonyTest.MockConfiguration.class, 
		SymphonyWorkflowConfig.class,
})
public class HandlerMappingTest extends AbstractHandlerMappingTest {

}
