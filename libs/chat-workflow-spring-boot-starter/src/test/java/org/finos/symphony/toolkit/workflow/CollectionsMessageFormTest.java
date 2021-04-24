package org.finos.symphony.toolkit.workflow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.finos.symphony.toolkit.json.EntityJson;
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
import java.util.List;

public class CollectionsMessageFormTest extends AbstractMockSymphonyTest {

    @Autowired
    FormMessageMLConverter messageMlConverter;

    @Autowired
    Validator validator;

    @Autowired
    EntityJsonConverter ejc;


    private String loadML(String string) throws IOException {
        return StreamUtils.copyToString(CollectionsMessageFormTest.class.getResourceAsStream(string), Charset.forName("UTF-8")).replace("\r\n", "\n");
    }

    private String loadJson(String string) throws IOException {
        return StreamUtils.copyToString(CollectionsMessageFormTest.class.getResourceAsStream(string), Charset.forName("UTF-8"));
    }

    @Test
    public void testCollectionEditMessage() throws Exception {

        Person person = getPerson();

        Button submit = new Button("submit", Type.ACTION, "GO");

        // can we convert to messageML? (something populated)
        EntityJson empty = new EntityJson();
        String out = messageMlConverter.convert(Person.class, person, ButtonList.of(submit), true,
                ErrorHelp.createErrorHolder(), empty);
        String json = ejc.writeValue(empty);
        System.out.println("<messageML>" + out + "</messageML>\n" + json);
        Assertions.assertEquals(getTestCollectionEditMessageML(), out);
        compareJson(loadJson("testCollectionEditMessage.json"), json);
    }

    @Test
    public void testCollectionViewMessage() throws Exception {

        Person person = getPerson();

        Button submit = new Button("submit", Type.ACTION, "GO");

        // can we convert to messageML? (something populated)
        EntityJson empty = new EntityJson();
        String out = messageMlConverter.convert(Person.class, person, ButtonList.of(submit), false,
                ErrorHelp.createErrorHolder(), empty);
        String json = ejc.writeValue(empty);
        System.out.println("<messageML>" + out + "</messageML>\n" + json);
        Assertions.assertEquals(getTestCollectionViewMessageML(), out);
        compareJson(loadJson("testCollectionViewMessage.json"), json);
    }
    private void compareJson(String loadJson, String json) throws JsonMappingException, JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        Assertions.assertEquals(om.readTree(loadJson), om.readTree(json));
    }

    private Person getPerson(){
        Person person = new Person(Arrays.asList("abc","pqr"), Arrays.asList(new Address("Pune"), new Address("Mumbai"), new Address("Bangalore")));
        return person;
    }

    private String getTestCollectionEditMessageML(){
        return "\n" +
                "<#-- starting template -->\n" +
                "<form \n" +
                "  id=\"org.finos.symphony.toolkit.workflow.Person\">\n" +
                " <table>\n" +
                " <tr><td><b>Names:</b></td><td>\n" +
                "  <span class=\"tempo-text-color--red\">${entity.errors.contents['names']!''}</span>\n" +
                "  \n" +
                "  <table><thead><tr><td><b>Value</b></td>\n" +
                "   <td style=\"text-align:center;\" ><button name=\"names.table-delete-rows\">Delete</button></td>\n" +
                "   <td style=\"text-align:center;\" ><button name=\"names.table-add-row\">New</button></td>\n" +
                "  </tr></thead><tbody>\n" +
                "  <#list entity.formdata.names as iB>\n" +
                "  <tr><td>${iB!''}</td>\n" +
                "   <td style=\"text-align:center; width:10%\" ><checkbox name=\"names.${iB?index}.selected\" /></td>\n" +
                "   <td style=\"text-align:center;\" ><button name=\"names[${iB?index}].table-edit-row\">Edit</button></td>\n" +
                "  </tr>\n" +
                "  </#list>\n" +
                "  </tbody></table></td></tr>\n" +
                " <tr><td><b>Addresses:</b></td><td>\n" +
                "  <span class=\"tempo-text-color--red\">${entity.errors.contents['addresses']!''}</span>\n" +
                "  \n" +
                "  <table><thead><tr>\n" +
                "  \n" +
                "    <td style=\"text-align:center; width:10%\" ><b>City</b></td>\n" +
                "   <td style=\"text-align:center;\" ><button name=\"addresses.table-delete-rows\">Delete</button></td>\n" +
                "   <td style=\"text-align:center;\" ><button name=\"addresses.table-add-row\">New</button></td>\n" +
                "  </tr></thead><tbody>\n" +
                "  <#list entity.formdata.addresses as iB>\n" +
                "  <tr>\n" +
                "  \n" +
                "   <td >${iB.city!''}</td>\n" +
                "   <td style=\"text-align:center; width:10%\" ><checkbox name=\"addresses.${iB?index}.selected\" /></td>\n" +
                "   <td style=\"text-align:center;\" ><button name=\"addresses[${iB?index}].table-edit-row\">Edit</button></td>\n" +
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


    private String getTestCollectionViewMessageML() {
        return "\n" +
                "<#-- starting template -->\n" +
                " <table>\n" +
                " <tr><td><b>Names:</b></td><td>\n" +
                "  <span class=\"tempo-text-color--red\">${entity.errors.contents['names']!''}</span>\n" +
                "  \n" +
                "  <table><thead><tr><td><b>Value</b></td>\n" +
                "  </tr></thead><tbody>\n" +
                "  <#list entity.workflow_001.names as iB>\n" +
                "  <tr><td>${iB!''}</td>\n" +
                "  </tr>\n" +
                "  </#list>\n" +
                "  </tbody></table></td></tr>\n" +
                " <tr><td><b>Addresses:</b></td><td>\n" +
                "  <span class=\"tempo-text-color--red\">${entity.errors.contents['addresses']!''}</span>\n" +
                "  \n" +
                "  <table><thead><tr>\n" +
                "  \n" +
                "    <td style=\"text-align:center; width:10%\" ><b>City</b></td>\n" +
                "  </tr></thead><tbody>\n" +
                "  <#list entity.workflow_001.addresses as iB>\n" +
                "  <tr>\n" +
                "  \n" +
                "   <td >${iB.city!''}</td>\n" +
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

class Person {
    private List<String> names;
    private List<Address> addresses;

    public Person() {
    }

    public Person(List<String> names, List<Address> addresses) {
        this.names = names;
        this.addresses = addresses;
    }

    public Person(List<String> names) {
        this.names = names;
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }
}

class Address {
    private String city;

    public Address() {
    }

    public Address(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
