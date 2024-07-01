package org.example.classes;

import java.util.ArrayList;
import java.util.List;

public class ExampleClass {
    private String name;
    private int value;
    private List<String> tags;

    public ExampleClass() {
    }

    public ExampleClass(String name, int value, List<String> tags) {
        this.name = name;
        this.value = value;
        // Deep copy of mutable field
        this.tags = new ArrayList<>(tags);
    }

    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public List<String> getTags() {
        return new ArrayList<>(tags); // Return a deep copy
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setTags(List<String> tags) {
        this.tags = new ArrayList<>(tags); // Deep copy
    }

    @Override
    public String toString() {
        return "ExampleClass{name='" + name + "', value=" + value + ", tags=" + tags + '}';
    }
}
