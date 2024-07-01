package org.example.classes;

import java.util.Date;
import java.util.HashMap;

public class TestClass {
    private Date date;
    private Double number;
    private HashMap<String, Integer> mapField;

    public TestClass(Date date, Double number, HashMap<String, Integer> mapField) {
        this.date = date;
        this.number = number;
        this.mapField = mapField;
    }

    public Date getDate() {
        return date;
    }

    public Double getNumber() {
        return number;
    }

    public HashMap<String, Integer> getMapField() {
        return mapField;
    }
}
