package com.github.deutschebank.symphony.workflow.sources.symphony.handlers;

import org.springframework.validation.Errors;

import com.github.deutschebank.symphony.json.EntityJson;
import com.github.deutschebank.symphony.workflow.form.ButtonList;

public interface FormMessageMLConverter {

	String convert(Class<?> c, Object o, ButtonList actions, boolean editMode, Errors e, EntityJson work);

}