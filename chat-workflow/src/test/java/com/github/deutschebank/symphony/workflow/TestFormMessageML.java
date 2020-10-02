package com.github.deutschebank.symphony.workflow;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.github.deutschebank.symphony.json.EntityJson;
import com.github.deutschebank.symphony.workflow.content.Author;
import com.github.deutschebank.symphony.workflow.content.ID;
import com.github.deutschebank.symphony.workflow.content.RoomDef;
import com.github.deutschebank.symphony.workflow.content.UserDef;
import com.github.deutschebank.symphony.workflow.fixture.TestOb4;
import com.github.deutschebank.symphony.workflow.fixture.TestOb4.Choice;
import com.github.deutschebank.symphony.workflow.fixture.TestObject;
import com.github.deutschebank.symphony.workflow.fixture.TestObjects;
import com.github.deutschebank.symphony.workflow.fixture.TestTemplatedObject;
import com.github.deutschebank.symphony.workflow.form.Button;
import com.github.deutschebank.symphony.workflow.form.Button.Type;
import com.github.deutschebank.symphony.workflow.form.ButtonList;
import com.github.deutschebank.symphony.workflow.sources.symphony.handlers.EntityJsonConverter;
import com.github.deutschebank.symphony.workflow.sources.symphony.handlers.FormMessageMLConverter;
import com.github.deutschebank.symphony.workflow.validation.ErrorHelp;

public class TestFormMessageML extends AbstractMockSymphonyTest {

	@Autowired
	FormMessageMLConverter messageMlConverter;
	
	@Autowired
	Validator validator;

	@Autowired
	EntityJsonConverter ejc;
	
	@Before
	public void setup() {
	}
	

	@Test
	public void testFreemarkerView() throws Exception {
		TestTemplatedObject to4 = new TestTemplatedObject();
		to4.setSomeText("howdy");
		to4.setR(new RoomDef("tesxt room", "blah", true, "abc123"));
		Button submit = new Button("submit", Type.ACTION, "GO");
		EntityJson empty = new EntityJson();
		String actual = messageMlConverter.convert(TestTemplatedObject.class, to4, ButtonList.of(submit), false, ErrorHelp.createErrorHolder(), empty);
		String json = ejc.writeValue(empty);
		System.out.println("<messageML>" + actual + "</messageML>\n"+json);
		Assert.assertEquals("abcdef", actual); 
	}
	
	
	@Test
	public void testNewWeirdFieldsEdit() throws Exception {
		TestOb4 to4 = new TestOb4();
		to4.setTheId(new ID(UUID.fromString("adf360dd-06fe-43a4-9a62-2c17fe2deefa")));
		to4.setC(Choice.C);
		Button submit = new Button("submit", Type.ACTION, "GO");
		EntityJson empty = new EntityJson();
		String actual = messageMlConverter.convert(TestOb4.class, to4, ButtonList.of(submit), true, ErrorHelp.createErrorHolder(), empty);
		String json = ejc.writeValue(empty);
		System.out.println("<messageML>" + actual + "</messageML>\n"+json);
		Assert.assertEquals("<form id=\"com.github.deutschebank.symphony.workflow.fixture.TestOb4\" ><hash tag=\"adf360dd-06fe-43a4-9a62-2c17fe2deefa\" /> <select name=\"c.\" required=\"false\" data-placeholder=\"Choose c\" ><option value=\"A\" >A</option><option value=\"B\" >B</option><option value=\"C\" >C</option></select><checkbox name=\"b.\" checked=\"false\" value=\"true\" >b</checkbox><person-selector name=\"someUser.\" placeholder=\"someUser\" required=\"false\"/><p><button name=\"submit\" type=\"action\" >GO</button></p></form>", actual); 
	}
	
	
	@Test
	public void testNewWeirdFieldsView() throws Exception {
		Author.CURRENT_AUTHOR.set(new UserDef("28374682376", "bbb", "v@example.com"));
		TestOb4 to4 = new TestOb4();
		to4.setB(true);
		to4.setC(Choice.B);
		to4.setSomeUser(new UserDef("2678", "bob", "bob@example.com"));
		to4.setTheId(new ID(UUID.fromString("adf360dd-06fe-43a4-9a62-2c17fe2deefa")));
		Button submit = new Button("submit", Type.ACTION, "GO");
		EntityJson empty = new EntityJson();
		
		String actual = messageMlConverter.convert(TestOb4.class, to4, ButtonList.of(submit), false, ErrorHelp.createErrorHolder(), empty);
		String json = ejc.writeValue(empty);
		System.out.println("<messageML>" + actual + "</messageML>\n"+json);
		Assert.assertEquals("<table><tr><td><b>theId:</b></td><td><hash tag=\"adf360dd-06fe-43a4-9a62-2c17fe2deefa\" /> </td></tr><tr><td><b>c:</b></td><td>B</td></tr><tr><td><b>b:</b></td><td>Y</td></tr><tr><td><b>a:</b></td><td><mention uid=\"sdfjk\" /></td></tr><tr><td><b>someUser:</b></td><td><mention uid=\"2678\" /></td></tr></table><form id=\"com.github.deutschebank.symphony.workflow.fixture.TestOb4\" ><p><button name=\"submit\" type=\"action\" >GO</button></p></form>", actual);
		
	}	
	@Test
	public void testAxeFormEditMessageML() throws Exception {

		TestObject a = new TestObject("83274239874", true, true, "rob@example.com", 234786, 2138);
		Button submit = new Button("submit", Type.ACTION, "GO");

		// can we convert to messageML? (something populated)
		EntityJson empty = new EntityJson();
		String out = messageMlConverter.convert(TestObject.class, a, ButtonList.of(submit), true,
				ErrorHelp.createErrorHolder(), empty);
		String json = ejc.writeValue(empty);
		System.out.println("<messageML>" + out + "</messageML>\n"+json);
		Assert.assertEquals(
				"<form id=\"com.github.deutschebank.symphony.workflow.fixture.TestObject\" ><text-field name=\"isin.\" placeholder=\"isin\" >83274239874</text-field><checkbox name=\"bidAxed.\" checked=\"true\" value=\"true\" >bidAxed</checkbox><checkbox name=\"askAxed.\" checked=\"true\" value=\"true\" >askAxed</checkbox><text-field name=\"creator.\" placeholder=\"creator\" >rob@example.com</text-field><text-field name=\"bidQty.\" placeholder=\"bidQty\" >234786</text-field><text-field name=\"askQty.\" placeholder=\"askQty\" >2138</text-field><p><button name=\"submit\" type=\"action\" >GO</button></p></form>",
				out);

		// new form
		empty = new EntityJson();
		out = messageMlConverter.convert(TestObject.class, null, ButtonList.of(submit), true,
				ErrorHelp.createErrorHolder(), empty);
		json = ejc.writeValue(empty);
		System.out.println("<messageML>" + out + "</messageML>\n"+json);
		Assert.assertEquals(
				"<form id=\"com.github.deutschebank.symphony.workflow.fixture.TestObject\" ><text-field name=\"isin.\" placeholder=\"isin\" ></text-field><checkbox name=\"bidAxed.\" value=\"true\" >bidAxed</checkbox><checkbox name=\"askAxed.\" value=\"true\" >askAxed</checkbox><text-field name=\"creator.\" placeholder=\"creator\" ></text-field><text-field name=\"bidQty.\" placeholder=\"bidQty\" ></text-field><text-field name=\"askQty.\" placeholder=\"askQty\" ></text-field><p><button name=\"submit\" type=\"action\" >GO</button></p></form>",
				out);

	}

	@Test
	public void testAxeFormViewMessageML() throws Exception {

		TestObject a = new TestObject("83274239874", true, true, "rob@example.com", 234786, 2138);
		Button submit = new Button("submit", Type.ACTION, "GO");

		// can we convert to messageML? (something populated)
		EntityJson empty = new EntityJson();
		String out = messageMlConverter.convert(TestObject.class, a, ButtonList.of(submit), false,
				ErrorHelp.createErrorHolder(), empty);
		String json = ejc.writeValue(empty);
		System.out.println("<messageML>" + out + "</messageML>\n"+json);
		Assert.assertEquals("<table><tr><td><b>isin:</b></td><td>83274239874</td></tr><tr><td><b>bidAxed:</b></td><td>Y</td></tr><tr><td><b>askAxed:</b></td><td>Y</td></tr><tr><td><b>creator:</b></td><td>rob@example.com</td></tr><tr><td><b>bidQty:</b></td><td>234786</td></tr><tr><td><b>askQty:</b></td><td>2138</td></tr></table><form id=\"com.github.deutschebank.symphony.workflow.fixture.TestObject\" ><p><button name=\"submit\" type=\"action\" >GO</button></p></form>", out);
							
	}

	@Test
	public void testAxesTableEditMessageML() throws Exception {

		TestObject a1 = new TestObject("83274239874", true, true, "rob@example.com", 234786, 2138);
		TestObject a2 = new TestObject("AUD274239874", true, false, "gregb@example.com", 2386, new BigDecimal("234823498.573"));

		TestObjects a = new TestObjects(Arrays.asList(a1, a2));

		Button submit = new Button("submit", Type.ACTION, "GO");

		// can we convert to messageML? (something populated)
		EntityJson empty = new EntityJson();
		String out = messageMlConverter.convert(TestObjects.class, a, ButtonList.of(submit), true,
				ErrorHelp.createErrorHolder(), empty);
		String json = ejc.writeValue(empty);
		System.out.println("<messageML>" + out + "</messageML>\n"+json);
		Assert.assertEquals("<form id=\"com.github.deutschebank.symphony.workflow.fixture.TestObjects\" ><table><thead><tr><td ><b>isin</b></td><td style=\"text-align:center;\" ><b>bidAxed</b></td><td style=\"text-align:center;\" ><b>askAxed</b></td><td ><b>creator</b></td><td style=\"text-align: right;\"><b>bidQty</b></td><td style=\"text-align: right;\"><b>askQty</b></td><td style=\"text-align:center;\" ><button name=\"items.table-delete-rows\">Delete</button></td><td style=\"text-align:center;\" ><button name=\"items.table-add-row\">New</button></td></tr></thead><tbody><tr><td >83274239874</td><td style=\"text-align:center;\" >Y</td><td style=\"text-align:center;\" >Y</td><td >rob@example.com</td><td style=\"text-align: right;\">234786</td><td style=\"text-align: right;\">2138</td><td style=\"text-align:center;\" ><checkbox name=\"items.[0].selected\" /></td><td style=\"text-align:center;\" ><button name=\"items.[0].table-edit-row\">Edit</button></td></tr><tr><td >AUD274239874</td><td style=\"text-align:center;\" >Y</td><td style=\"text-align:center;\" >N</td><td >gregb@example.com</td><td style=\"text-align: right;\">2386</td><td style=\"text-align: right;\">234823498.573</td><td style=\"text-align:center;\" ><checkbox name=\"items.[1].selected\" /></td><td style=\"text-align:center;\" ><button name=\"items.[1].table-edit-row\">Edit</button></td></tr></tbody></table><p><button name=\"submit\" type=\"action\" >GO</button></p></form>", out);
	}

	@Test
	public void testAxesTableViewMessageML() throws Exception {

		TestObject a1 = new TestObject("83274239874", true, true, "rob@example.com", 234786, 2138);
		TestObject a2 = new TestObject("AUD274239874", true, false, "gregb@example.com", 2386, new BigDecimal("234823498.573"));

		TestObjects a = new TestObjects(Arrays.asList(a1, a2));

		Button submit = new Button("submit", Type.ACTION, "GO");

		// can we convert to messageML? (something populated)
		EntityJson empty = new EntityJson();
		String out = messageMlConverter.convert(TestObjects.class, a, ButtonList.of(submit), false,
				ErrorHelp.createErrorHolder(), empty);
		String json = ejc.writeValue(empty);
		System.out.println("<messageML>" + out + "</messageML>\n"+json);
		Assert.assertTrue(out.startsWith("<table><tr><td><b>items:</b></td><td><table><thead><tr><td ><b>isin</b></td><td style=\"text-align:center;\" ><b>bidAxed</b></td><td style=\"text-align:center;\" ><b>askAxed</b></td><td ><b>creator</b></td><td style=\"text-align: right;\"><b>bidQty</b></td><td style=\"text-align: right;\"><b>askQty</b></td></tr></thead><tbody><tr><td >83274239874</td><td style=\"text-align:center;\" >Y</td><td style=\"text-align:center;\" >Y</td><td >rob@example.com</td><td style=\"text-align: right;\">234786</td><td style=\"text-align: right;\">2138</td></tr><tr><td >AUD274239874</td><td style=\"text-align:center;\" >Y</td><td style=\"text-align:center;\" >N</td><td >gregb@example.com</td><td style=\"text-align: right;\">2386</td><td style=\"text-align: right;\">234823498.573</td></tr></tbody></table></td></tr></table><form id=\"com.github.deutschebank.symphony.workflow.fixture.TestObjects\" ><p><button name=\"submit\" type=\"action\" >GO</button></p></form>"));

	}
	
	@Test
	public void testValidation() throws Exception {
		TestObject a = new TestObject("83274239874", true, true, "rob", 234786, 2138);
		Button submit = new Button("submit", Type.ACTION, "GO");

		// can we convert to messageML? (something populated)
		Errors eh = ErrorHelp.createErrorHolder();
		validator.validate(a, eh);
		
		EntityJson empty = new EntityJson();
		String out = messageMlConverter.convert(TestObject.class, a, ButtonList.of(submit), true, eh, empty);
		String json = ejc.writeValue(empty);
		System.out.println("<messageML>" + out + "</messageML>\n"+json);
		Assert.assertTrue(out.startsWith("<form id=\"com.github.deutschebank.symphony.workflow.fixture.TestObject\" ><span class=\"tempo-text-color--red\">size must be between 12 and 2147483647</span><text-field name=\"isin.\" placeholder=\"isin\" >83274239874</text-field"));
		Assert.assertTrue(out.contains("<span class=\"tempo-text-color--red\">must be a well-formed email address</span><text-field name=\"creator.\" placeholder=\"creator\" >rob</text-field>"));
	}
}
