package org.finos.symphony.toolkit.workflow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.finos.symphony.toolkit.json.EntityJson;
import org.finos.symphony.toolkit.workflow.content.HashTagDef;
import org.finos.symphony.toolkit.workflow.fixture.TestCollections;
import org.finos.symphony.toolkit.workflow.fixture.TestCollections.Choice;
import org.finos.symphony.toolkit.workflow.fixture.TestCollections.MiniBean;
import org.finos.symphony.toolkit.workflow.form.Button;
import org.finos.symphony.toolkit.workflow.form.Button.Type;
import org.finos.symphony.toolkit.workflow.form.ButtonList;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.EntityJsonConverter;
import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.FormMessageMLConverter;
import org.finos.symphony.toolkit.workflow.validation.ErrorHelp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StreamUtils;
import org.springframework.validation.Validator;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

public class TestFormCollectionsMessageML extends AbstractMockSymphonyTest {

	@Autowired
	FormMessageMLConverter messageMlConverter;
	
	@Autowired
	Validator validator;

	@Autowired
	EntityJsonConverter ejc;

	
	
	private String loadML(String string) throws IOException {
		return StreamUtils.copyToString(TestFormCollectionsMessageML.class.getResourceAsStream(string), Charset.forName("UTF-8")).replace("\r\n", "\n");
	}

	private String loadJson(String string) throws IOException {
		return StreamUtils.copyToString(TestFormCollectionsMessageML.class.getResourceAsStream(string), Charset.forName("UTF-8"));
	}

	private TestCollections createTestCollections() {
		MiniBean mb1 = new MiniBean("A String", 4973, Arrays.asList("Amsidh", "Rob"));
		MiniBean mb2 = new MiniBean("Another String", 45, Arrays.asList("Terry", "James"));
		MiniBean mb3 = new MiniBean("Thing 3", 8787, null);
		
		TestCollections out = new TestCollections(
				Arrays.asList("a", "b", "c"), 
				Arrays.asList(Choice.A, Choice.B), 
				Arrays.asList(mb1, mb2, mb3),
				Arrays.asList(new HashTagDef("abc"), new HashTagDef("def")));
		return out;
	}
	

	@Test
	public void testCollectionsEditMessageML() throws Exception {

		TestCollections c = createTestCollections();

		Button submit = new Button("submit", Type.ACTION, "GO");

		// can we convert to messageML? (something populated)
		EntityJson empty = new EntityJson();
		String out = messageMlConverter.convert(TestCollections.class, c, ButtonList.of(submit), true,
				ErrorHelp.createErrorHolder(), empty);
		String json = ejc.writeValue(empty);
		System.out.println("<messageML>" + out + "</messageML>\n"+json);
		Assertions.assertEquals(getTestCollectionsEditMessage(), out);
		compareJson(loadJson("testCollectionsEditMessageML.json"), json); 
	}

	@Test
	public void testCollectionsViewMessageML() throws Exception {

		TestCollections c = createTestCollections();

		Button submit = new Button("submit", Type.ACTION, "GO");

		// can we convert to messageML? (something populated)
		EntityJson empty = new EntityJson();
		String out = messageMlConverter.convert(TestCollections.class, c, ButtonList.of(submit), false,
				ErrorHelp.createErrorHolder(), empty);
		String json = ejc.writeValue(empty);
		System.out.println("<messageML>" + out + "</messageML>\n"+json);
		Assertions.assertEquals(getTestCollectionsViewMessage(), out);
		compareJson(loadJson("testCollectionsViewMessageML.json"), json); 
	}
	
	private void compareJson(String loadJson, String json) throws JsonMappingException, JsonProcessingException {
		ObjectMapper om = new ObjectMapper();
		Assertions.assertEquals(om.readTree(loadJson), om.readTree(json));
	}


 private String getTestCollectionsEditMessage(){
		return "\n<#-- starting template -->\n" +
				"<form \n" +
				"  id=\"org.finos.symphony.toolkit.workflow.fixture.TestCollections\">\n" +
				" <table>\n" +
				" <tr><td><b>String List:</b></td><td>\n" +
				"  <span class=\"tempo-text-color--red\">${entity.errors.contents['stringList']!''}</span>\n" +
				"  \n" +
				"  <table><thead><tr><td><b>Value</b></td>\n" +
				"   <td style=\"text-align:center;\" ><button name=\"stringList.table-delete-rows\">Delete</button></td>\n" +
				"   <td style=\"text-align:center;\" ><button name=\"stringList.table-add-row\">New</button></td>\n" +
				"  </tr></thead><tbody>\n" +
				"  <#list entity.formdata.stringList as iB>\n" +
				"  <tr><td>${iB!''}</td>\n" +
				"   <td style=\"text-align:center; width:10%\" ><checkbox name=\"stringList.${iB?index}.selected\" /></td>\n" +
				"   <td style=\"text-align:center;\" ><button name=\"stringList[${iB?index}].table-edit-row\">Edit</button></td>\n" +
				"  </tr>\n" +
				"  </#list>\n" +
				"  </tbody></table></td></tr>\n" +
				" <tr><td><b>Enum List:</b></td><td>\n" +
				"  <span class=\"tempo-text-color--red\">${entity.errors.contents['enumList']!''}</span>\n" +
				"  \n" +
				"  <table><thead><tr><td><b>Value</b></td>\n" +
				"   <td style=\"text-align:center;\" ><button name=\"enumList.table-delete-rows\">Delete</button></td>\n" +
				"   <td style=\"text-align:center;\" ><button name=\"enumList.table-add-row\">New</button></td>\n" +
				"  </tr></thead><tbody>\n" +
				"  <#list entity.formdata.enumList as iB>\n" +
				"  <tr><td>${iB!''}</td>\n" +
				"   <td style=\"text-align:center; width:10%\" ><checkbox name=\"enumList.${iB?index}.selected\" /></td>\n" +
				"   <td style=\"text-align:center;\" ><button name=\"enumList[${iB?index}].table-edit-row\">Edit</button></td>\n" +
				"  </tr>\n" +
				"  </#list>\n" +
				"  </tbody></table></td></tr>\n" +
				" <tr><td><b>Min Bean List:</b></td><td>\n" +
				"  <span class=\"tempo-text-color--red\">${entity.errors.contents['minBeanList']!''}</span>\n" +
				"  \n" +
				"  <table><thead><tr>\n" +
				"  \n" +
				"    <td style=\"text-align:center; width:10%\" ><b>Some String</b></td>\n" +
				"  \n" +
				"    <td style=\"text-align: right;\"><b>Some Integer</b></td>\n" +
				"  \n" +
				"    <td style=\"text-align:center; width:10%\" ><b>Some More Strings</b></td>\n" +
				"   <td style=\"text-align:center;\" ><button name=\"minBeanList.table-delete-rows\">Delete</button></td>\n" +
				"   <td style=\"text-align:center;\" ><button name=\"minBeanList.table-add-row\">New</button></td>\n" +
				"  </tr></thead><tbody>\n" +
				"  <#list entity.formdata.minBeanList as iB>\n" +
				"  <tr>\n" +
				"  \n" +
				"   <td >${iB.someString!''}</td>\n" +
				"  \n" +
				"   <td style=\"text-align: right;\">${iB.someInteger!''}</td>\n" +
				"  \n" +
				"   <td >...</td>\n" +
				"   <td style=\"text-align:center; width:10%\" ><checkbox name=\"minBeanList.${iB?index}.selected\" /></td>\n" +
				"   <td style=\"text-align:center;\" ><button name=\"minBeanList[${iB?index}].table-edit-row\">Edit</button></td>\n" +
				"  </tr>\n" +
				"  </#list>\n" +
				"  </tbody></table></td></tr>\n" +
				" <tr><td><b>Some Hash Tags:</b></td><td>\n" +
				"  <span class=\"tempo-text-color--red\">${entity.errors.contents['someHashTags']!''}</span>\n" +
				"  \n" +
				"  <table><thead><tr><td><b>Value</b></td>\n" +
				"   <td style=\"text-align:center;\" ><button name=\"someHashTags.table-delete-rows\">Delete</button></td>\n" +
				"   <td style=\"text-align:center;\" ><button name=\"someHashTags.table-add-row\">New</button></td>\n" +
				"  </tr></thead><tbody>\n" +
				"  <#list entity.formdata.someHashTags as iB>\n" +
				"  <tr><td>\n" +
				"  <#if iB??><cash \n" +
				"   tag=\"${iB.name!''}\" /></#if></td>\n" +
				"   <td style=\"text-align:center; width:10%\" ><checkbox name=\"someHashTags.${iB?index}.selected\" /></td>\n" +
				"   <td style=\"text-align:center;\" ><button name=\"someHashTags[${iB?index}].table-edit-row\">Edit</button></td>\n" +
				"  </tr>\n" +
				"  </#list>\n" +
				"  </tbody></table></td></tr>\n" +
				" </table>\n" +
				"  <p><#list entity.buttons.contents as button>\n" +
				"    <button \n" +
				"         name=\"${button.name}\"\n" +
				"         type=\"${button.buttonType?lower_case}\">\n" +
				"      ${button.text}\n" +
				"    </button>\n" +
				"  </#list></p>\n" +
				"</form>\n" +
				"<#-- ending template -->\n";
 }

 private String getTestCollectionsViewMessage(){
		return "\n<#-- starting template -->\n" +
				" <table>\n" +
				" <tr><td><b>String List:</b></td><td>\n" +
				"  <span class=\"tempo-text-color--red\">${entity.errors.contents['stringList']!''}</span>\n" +
				"  \n" +
				"  <table><thead><tr><td><b>Value</b></td>\n" +
				"  </tr></thead><tbody>\n" +
				"  <#list entity.workflow_001.stringList as iB>\n" +
				"  <tr><td>${iB!''}</td>\n" +
				"  </tr>\n" +
				"  </#list>\n" +
				"  </tbody></table></td></tr>\n" +
				" <tr><td><b>Enum List:</b></td><td>\n" +
				"  <span class=\"tempo-text-color--red\">${entity.errors.contents['enumList']!''}</span>\n" +
				"  \n" +
				"  <table><thead><tr><td><b>Value</b></td>\n" +
				"  </tr></thead><tbody>\n" +
				"  <#list entity.workflow_001.enumList as iB>\n" +
				"  <tr><td>${iB!''}</td>\n" +
				"  </tr>\n" +
				"  </#list>\n" +
				"  </tbody></table></td></tr>\n" +
				" <tr><td><b>Min Bean List:</b></td><td>\n" +
				"  <span class=\"tempo-text-color--red\">${entity.errors.contents['minBeanList']!''}</span>\n" +
				"  \n" +
				"  <table><thead><tr>\n" +
				"  \n" +
				"    <td style=\"text-align:center; width:10%\" ><b>Some String</b></td>\n" +
				"  \n" +
				"    <td style=\"text-align: right;\"><b>Some Integer</b></td>\n" +
				"  \n" +
				"    <td style=\"text-align:center; width:10%\" ><b>Some More Strings</b></td>\n" +
				"  </tr></thead><tbody>\n" +
				"  <#list entity.workflow_001.minBeanList as iB>\n" +
				"  <tr>\n" +
				"  \n" +
				"   <td >${iB.someString!''}</td>\n" +
				"  \n" +
				"   <td style=\"text-align: right;\">${iB.someInteger!''}</td>\n" +
				"  \n" +
				"   <td >...</td>\n" +
				"  </tr>\n" +
				"  </#list>\n" +
				"  </tbody></table></td></tr>\n" +
				" <tr><td><b>Some Hash Tags:</b></td><td>\n" +
				"  <span class=\"tempo-text-color--red\">${entity.errors.contents['someHashTags']!''}</span>\n" +
				"  \n" +
				"  <table><thead><tr><td><b>Value</b></td>\n" +
				"  </tr></thead><tbody>\n" +
				"  <#list entity.workflow_001.someHashTags as iB>\n" +
				"  <tr><td>\n" +
				"  <#if iB??><cash \n" +
				"   tag=\"${iB.name!''}\" /></#if></td>\n" +
				"  </tr>\n" +
				"  </#list>\n" +
				"  </tbody></table></td></tr>\n" +
				" </table>\n" +
				"<form \n" +
				"  id=\"just-buttons-form\">\n" +
				"  <p><#list entity.buttons.contents as button>\n" +
				"    <button \n" +
				"         name=\"${button.name}\"\n" +
				"         type=\"${button.buttonType?lower_case}\">\n" +
				"      ${button.text}\n" +
				"    </button>\n" +
				"  </#list></p>\n" +
				"</form>\n" +
				"<#-- ending template -->\n";
 }
}
