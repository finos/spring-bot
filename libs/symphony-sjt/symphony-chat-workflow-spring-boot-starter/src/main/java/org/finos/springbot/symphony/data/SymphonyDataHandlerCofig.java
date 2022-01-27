package org.finos.springbot.symphony.data;

import java.util.Arrays;
import java.util.List;

import org.finos.springbot.entities.VersionSpaceHelp;
import org.finos.springbot.entityjson.VersionSpace;
import org.finos.springbot.workflow.data.DataHandlerConfig;
import org.finos.springbot.workflow.data.EntityJsonConverter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.symphony.user.DisplayName;
import com.symphony.user.EmailAddress;
import com.symphony.user.StreamID;

@Configuration
@Import(DataHandlerConfig.class)
public class SymphonyDataHandlerCofig implements InitializingBean {

	
	@Autowired
	EntityJsonConverter ejc;

	@Override
	public void afterPropertiesSet() throws Exception {
			
		VersionSpaceHelp.basicSymphonyVersionSpace().stream()
			.forEach(vs -> ejc.addVersionSpace(vs));
		
		List<VersionSpace> chatWorkflowVersionSpaces = syphonyExtendedVersionSpace();
		
		chatWorkflowVersionSpaces.stream().forEach(vs -> ejc.addVersionSpace(vs));
		
		ejc.getObjectMapper().registerModule(new LegacyFormatModule());
	}

	public static List<VersionSpace> syphonyExtendedVersionSpace() {
		return Arrays.asList(			
				new VersionSpace(DisplayName.class, "1.0"), 
				new VersionSpace(StreamID.class, "1.0"), 
				new VersionSpace(EmailAddress.class, "1.0"));
	}
}
