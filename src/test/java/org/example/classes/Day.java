package org.example.classes;

public enum Day {
    SUNDAY("Weekend"), MONDAY("Weekday"), TUESDAY("Weekday"), WEDNESDAY("Weekday"),
    THURSDAY("Weekday"), FRIDAY("Weekday"), SATURDAY("Weekend");

    private String type;

    // Constructor
    Day(String type) {
        this.type = type;
    }

    // Getter method
    public String getType() {
        return type;
    }
}
