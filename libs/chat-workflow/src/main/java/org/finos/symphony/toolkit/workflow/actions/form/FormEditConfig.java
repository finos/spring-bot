package org.finos.symphony.toolkit.workflow.actions.form;

import org.finos.symphony.toolkit.workflow.response.handlers.ResponseHandlers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ErrorHandler;

@Configuration
public class FormEditConfig {

	@Autowired
	ErrorHandler eh;
	
	@Autowired
	ResponseHandlers rh;
	
//	@Bean
//	@ConditionalOnMissingBean
//	public TableAddRow tableAddRow() {
//		return new TableAddRow(eh, rh);
//	}
//	
//	@Bean
//	@ConditionalOnMissingBean
//	public TableDeleteRows tableDeleteRows() {
//		return new TableDeleteRows(eh, rh);
//	}
//	
//	@Bean
//	@ConditionalOnMissingBean
//	public TableEditRow tableEditRow() {
//		return new TableEditRow(eh, rh);
//	}
	
	@Bean
	@ConditionalOnMissingBean
	public EditActionElementsConsumer editActionElementsConsumer() {
		return new EditActionElementsConsumer(eh, rh);
	}
}
