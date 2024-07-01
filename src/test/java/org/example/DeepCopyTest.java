package org.example;

import org.example.classes.*;
import org.example.copy.CopyUtils;
import org.example.copy.DeepCopyException;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

public class DeepCopyTest {

    @Test
    public void testDeepCopy() {
        List<String> tags = Arrays.asList("tag1", "tag2");
        ExampleClass original = new ExampleClass("Original", 42, tags);

        // Clone the object using the CopyUtils.deepCopy method
        ExampleClass copied = CopyUtils.deepCopy(original);

        // Check if the copied object is not the same instance
        assertNotSame(original, copied);

        // Check if the fields are equal
        assertEquals(original.getName(), copied.getName());
        assertEquals(original.getValue(), copied.getValue());
        assertEquals(original.getTags(), copied.getTags());

        // Modify the copied object's mutable field and verify it does not affect the original
        copied.setTags(Arrays.asList("tag3"));

        assertNotEquals(original.getTags(), copied.getTags());
    }

    @Test
    public void testDeepCopyWithString() {
        String original = "Hello";
        String copied = CopyUtils.deepCopy(original);

        // Check if the copied object is the same instance
        assertSame(original, copied);
    }

    @Test
    public void testDeepCopyWithInteger() {
        Integer original = 123;
        Integer copied = CopyUtils.deepCopy(original);

        // Check if the copied object is the same instance
        assertSame(original, copied);
    }

    @Test
    public void testDeepCopyWithNull() {
        assertNull(CopyUtils.deepCopy(null));
    }

    @Test
    public void testDeepCopyWithNoSuitableConstructor() {
        InvalidClass original = new InvalidClass( "John", 42);

        Exception exception = assertThrows(DeepCopyException.class, () -> {
            CopyUtils.deepCopy(original);
        });

        assertTrue(exception.getMessage().contains("does not have a suitable constructor"));
    }

    @Test
    public void testDeepCopyWithArgumentConstructor() {

        Person original = new Person("John", 30);
        Person copy = CopyUtils.deepCopy(original);

        assertNotNull(copy);
        assertEquals(original.getName(), copy.getName());
        assertEquals(original.getAge(), copy.getAge());
        assertNotSame(original, copy);
    }

    @Test
    public void testDeepCopyWithMixedConstructors() {

        MixedConstructorsClass original = new MixedConstructorsClass(42, "Hello");
        MixedConstructorsClass copy = CopyUtils.deepCopy(original);

        assertNotNull(copy);
        assertEquals(original.getName(), copy.getName());
        assertEquals(original.getAge(), copy.getAge());
        assertNotSame(original, copy);
    }

    @Test
    public void testDeepCopyWithEnum() {
        Day original = Day.MONDAY;
        Day copy = CopyUtils.deepCopy(original);

        assertNotNull(copy);
        assertEquals(original, copy);
        assertSame(original, copy); // Enums should be the same instance
    }

    @Test
    public void testDeepCopyWithRecord() {
        PersonRecord original = new PersonRecord("John", 30);
        PersonRecord copy = CopyUtils.deepCopy(original);

        assertNotNull(copy);
        assertEquals(original.name(), copy.name());
        assertEquals(original.age(), copy.age());
        assertNotSame(original, copy); // Ensure it's a different instance
    }

    @Test
    public void testDeepCopyWithTestClass() {
        // Create the original object
        Date date = new Date();
        Double mathValue = Math.PI;
        HashMap<String, Integer> map = new HashMap<>();
        map.put("one", 1);
        map.put("two", 2);

        TestClass original = new TestClass(date, mathValue, map);

        // Perform deep copy
        TestClass copy = CopyUtils.deepCopy(original);

        // Verify the deep copy
        assertNotNull(copy);
        assertNotSame(original, copy); // Ensure it's a different instance

        // Verify the date field
        assertNotNull(copy.getDate());
        assertNotSame(original.getDate(), copy.getDate());
        assertEquals(original.getDate(), copy.getDate());

        // Verify the number field
        assertNotNull(copy.getNumber());
        assertEquals(original.getNumber(), copy.getNumber());

        // Verify the map field
        assertNotNull(copy.getMapField());
        assertNotSame(original.getMapField(), copy.getMapField());
        assertEquals(original.getMapField().size(), copy.getMapField().size());
        assertEquals(original.getMapField(), copy.getMapField());
    }
}
