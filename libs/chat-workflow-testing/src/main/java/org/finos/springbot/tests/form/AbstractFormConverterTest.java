package org.finos.springbot.tests.form;

import java.util.Map;

import org.finos.springbot.tests.form.Primitives.Meal;
import org.finos.springbot.workflow.form.FormConverter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractFormConverterTest {
	
	protected FormConverter fc;
	
	protected abstract void before();

	@SuppressWarnings("unchecked")
	@Test
	public void testPrimitives() throws Exception {
		before();
		Object o = new ObjectMapper().readValue("{\"action\":\"add+0\",\"a.\":\"fd3442\",\"b.\":true,\"c.\":32432, \"m.\":\"LUNCH\"}", Map.class);
		Primitives to = (Primitives) fc.convert((Map<String, Object>) o, Primitives.class.getCanonicalName());
		Assertions.assertEquals("fd3442", to.getA());
		Assertions.assertEquals(true, to.isB());
		Assertions.assertEquals(32432, to.getC());
		Assertions.assertEquals(Meal.LUNCH, to.getM());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testCollection() throws Exception {
		before();
		Object o = new ObjectMapper().readValue("{\"action\":\"add+0\",\"items.[0].a.\":\"fd3442\",\"items.[0].b.\":\"true\",\"items.[0].c.\":\"43534\",\"items.[0].m.\":\"LUNCH\"}", Map.class);
		Collection to = (Collection) fc.convert((Map<String, Object>) o, Collection.class.getCanonicalName());
		Assertions.assertEquals("fd3442", to.getItems().get(0).getA());
		Assertions.assertEquals(true, to.getItems().get(0).isB());
		Assertions.assertEquals(43534, to.getItems().get(0).getC());
		Assertions.assertEquals(Meal.LUNCH, to.getItems().get(0).getM());
	}

	

}
