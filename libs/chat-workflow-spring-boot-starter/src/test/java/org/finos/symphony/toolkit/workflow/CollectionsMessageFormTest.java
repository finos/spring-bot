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
        Assertions.assertTrue(loadML("testCollectionEditMessageML.ml").contentEquals(out));
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
        Assertions.assertTrue(loadML("testCollectionViewMessageML.ml").contentEquals(out));
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
