package org.example.classes;

public class MixedConstructorsClass {
    private final int age;
    private final String name;

    public MixedConstructorsClass(int age, String name) {
        this.age = age;
        this.name = name;
    }

    public MixedConstructorsClass(String name) {
        this(0, name);
    }

    public MixedConstructorsClass(int age) {
        this(age, null);
    }

    public int getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "MixedConstructorsClass{age=" + age + ", name='" + name + "'}";
    }
}
