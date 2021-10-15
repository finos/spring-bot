package org.finos.symphony.toolkit.workflow.fixture;

import java.util.ArrayList;
import java.util.List;

import org.finos.springbot.workflow.annotations.Work;

@Work()
public class TestPrimitives {

    private List<String> names = new ArrayList<>();
    private List<Integer> integerList = new ArrayList<>();
    private List<Number> numberList = new ArrayList<>();

    public TestPrimitives() {
    }

    public List<String> getNames() {
        return names;
    }

    public void setNames(List<String> names) {
        this.names = names;
    }

    public List<Integer> getIntegerList() {
        return integerList;
    }

    public void setIntegerList(List<Integer> integerList) {
        this.integerList = integerList;
    }

    public List<Number> getNumberList() {
        return numberList;
    }

    public void setNumberList(List<Number> numberList) {
        this.numberList = numberList;
    }
}

