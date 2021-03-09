package org.finos.symphony.practice.rsanag;

import org.finos.symphony.practice.rsanag.report.Report;
import org.finos.symphony.practice.rsanag.report.ReportBuilder;
import org.finos.symphony.toolkit.workflow.Workflow;
import org.finos.symphony.toolkit.workflow.java.workflow.ClassBasedWorkflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
@EnableConfigurationProperties(RsaNagProperties.class)
public class Config {
	
	public static final String RSA_KEY_AUDIT_ROOM = "RSA Key Audit Room";
	
	
	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}
	
	@Autowired
	RsaNagProperties rsaNagProperties;
	
	@Bean
	public Workflow appWorkflow() {
		ClassBasedWorkflow wf = new ClassBasedWorkflow(Config.class.getCanonicalName());
		wf.addClass(Report.class);
		wf.addClass(ReportBuilder.class);
		return wf;
	}
	
	@Bean
	public ReportBuilder reportBuilder() {
		return new ReportBuilder(rsaNagProperties.isSendEmails(), rsaNagProperties.isExpireBots());
	}
	
	@Bean
	public SpringTemplateEngine thymeleafTemplateEngine(ITemplateResolver templateResolver) {
	    SpringTemplateEngine templateEngine = new SpringTemplateEngine();
	    templateEngine.setTemplateResolver(templateResolver);
	    templateEngine.setTemplateEngineMessageSource(emailMessageSource());
	    return templateEngine;
	}
}
