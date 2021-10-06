package org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker;

import java.lang.reflect.Field;
import java.util.Date;

import org.finos.symphony.toolkit.workflow.sources.symphony.handlers.freemarker.annotations.Display;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class WithFieldTest {

    private WithField withField;

    @BeforeEach
    public void init() {
        this.withField = new WithField() {
            @Override
            public String apply(Field f, boolean editMode, Variable variable, WithType contentHandler) {
                return null;
            }

            @Override
            public boolean expand() {
                return false;
            }
        };
    }

    @Test
    public void testForValidFieldName() {
        Assertions.assertEquals(withField.fieldNameDefaultFormatter("firstName"), "First Name");
        Assertions.assertEquals(withField.fieldNameDefaultFormatter("dateOfBirth"), "Date Of Birth");
        Assertions.assertEquals(withField.fieldNameDefaultFormatter(""), "");
        Assertions.assertEquals(withField.fieldNameDefaultFormatter(null), "");
        Assertions.assertEquals(withField.fieldNameDefaultFormatter("birth10Date5"), "Birth10 Date5");
        Assertions.assertEquals(withField.fieldNameDefaultFormatter("12birth10Date5"), "12birth10 Date5");
        Assertions.assertEquals(withField.fieldNameDefaultFormatter("      "), "");
    }

    @Test
    public void testGetFieldNameOrientation() throws NoSuchFieldException {
        DummyDisplayModel dummyDisplayModel = new DummyDisplayModel();
        Field firstName = dummyDisplayModel.getClass().getDeclaredField("firstName");
        withField.getFieldNameOrientation(firstName);
        Assertions.assertEquals(withField.getFieldNameOrientation(firstName), "My First Name");

        Field middleName = dummyDisplayModel.getClass().getDeclaredField("middleName");
        Assertions.assertEquals(withField.getFieldNameOrientation(middleName), "");

        Field lastName = dummyDisplayModel.getClass().getDeclaredField("lastName");
        Assertions.assertEquals(withField.getFieldNameOrientation(lastName), "Last Name");

        Field dateOfBirth = dummyDisplayModel.getClass().getDeclaredField("dateOfBirth");
        Assertions.assertEquals(withField.getFieldNameOrientation(dateOfBirth), "Date Of Birth");

        Field city = dummyDisplayModel.getClass().getDeclaredField("city");
        Assertions.assertEquals(withField.getFieldNameOrientation(city), "City");

        Field pinCode = dummyDisplayModel.getClass().getDeclaredField("pinCode");
        Assertions.assertEquals(withField.getFieldNameOrientation(pinCode), "Pin Code");

    }

    class DummyDisplayModel {

        @Display(name = "My First Name")
        private String firstName;
        @Display(visible = false)
        private String middleName;
        @Display(visible = true)
        private String lastName;

        private Date dateOfBirth;

        @Display(name = "")
        private String city;

        @Display(name = "")
        private Integer pinCode;
    }
}
