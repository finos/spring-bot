package com.github.deutschebank.symphony.workflow.sources.symphony;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.deutschebank.symphony.workflow.sources.symphony.elements.AbstractElementsConsumer;
import com.github.deutschebank.symphony.workflow.sources.symphony.elements.MethodCallElementsConsumer;
import com.github.deutschebank.symphony.workflow.sources.symphony.elements.edit.EditActionElementsConsumer;
import com.github.deutschebank.symphony.workflow.sources.symphony.elements.edit.TableAddRow;
import com.github.deutschebank.symphony.workflow.sources.symphony.elements.edit.TableDeleteRows;
import com.github.deutschebank.symphony.workflow.sources.symphony.elements.edit.TableEditRow;
import com.github.deutschebank.symphony.workflow.sources.symphony.messages.HelpMessageConsumer;
import com.github.deutschebank.symphony.workflow.sources.symphony.messages.MethodCallMessageConsumer;

@Configuration
public class SymphonyWorkflowComponentsConfig {
	
	
	@Bean
	public HelpMessageConsumer helpConsumer() {
		return new HelpMessageConsumer();
	}
	
	@Bean
	public MethodCallMessageConsumer mcConsumer() {
		return new MethodCallMessageConsumer();
	}
	
	@Bean
	public AbstractElementsConsumer elementsMethodCallConsumer() {
		return new MethodCallElementsConsumer();
	}

	@Bean
	public EditActionElementsConsumer editActionElementsConsumer() {
		return new EditActionElementsConsumer();
	}
	
	@Bean
	public TableAddRow tableAddRow() {
		return new TableAddRow();
	}
	
	@Bean
	public TableDeleteRows tableDeleteRows() {
		return new TableDeleteRows();
	}
	
	@Bean
	public TableEditRow tableEditRow() {
		return new TableEditRow();
	}
	
}
