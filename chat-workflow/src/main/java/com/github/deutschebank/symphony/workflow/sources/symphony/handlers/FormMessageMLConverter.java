package com.github.deutschebank.symphony.workflow.sources.symphony.handlers;

import java.util.List;

import org.springframework.validation.Errors;

import com.github.deutschebank.symphony.json.EntityJson;
import com.github.deutschebank.symphony.workflow.form.Button;

public interface FormMessageMLConverter {

	String convert(Class<?> c, Object o, List<Button> actions, boolean editMode, Errors e, EntityJson work);

}