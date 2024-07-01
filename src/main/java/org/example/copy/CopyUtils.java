package org.example.copy;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * A utility class for performing deep copy operations on objects.
 * <p>
 * This class provides methods to create a deep copy of an object
 * created from user-defined classes, including support for arrays,
 * records, and Enums. It handles different scenarios and ensures
 * that all mutable fields are copied recursively.
 * </p>
 */
public class CopyUtils {

    /**
     * Creates a deep copy of the provided object.
     * <p>
     * This method performs a deep copy of the original object recursively, including its mutable
     * fields. It handles primitive types, immutable types, arrays, and custom objects.
     * <b>Restrictions:</b> <i>this method is not suitable for the original objects implemented from an inner class
     * and from a class whose constructor parameters are of a type that is different from the class's
     * field types.</i>
     * </p>
     *
     * @param original The original object to be copied.
     * @param <T>      The type of the object.
     * @return A deep copy of the original object.
     * @throws DeepCopyException If deep copying fails.
     */
    public static <T> T deepCopy(T original) throws DeepCopyException {
        if (original == null) {
            return null;
        }

        Class<?> clazz = original.getClass();

        if (clazz.isEnum() || clazz.isInterface()) {
            // For enums, and interfaces, return the original object
            return original;
        }

        if (isImmutable(original)) {
            return original;
        }

        if (clazz.isRecord()) {
            return deepCopyRecord(original);
        }

        if (original.getClass().isArray()) {
            return deepCopyArray(original);
        }

        return deepCopyObject(original);
    }

    /**
     * Checks if a class is immutable.
     * <p>
     * This method determines if a class is considered immutable by testing whether
     * an object is an instance of a String, Integer, Boolean, Character, Byte, Short,
     * Long, Float, or Double class.
     * </p>
     * @param original The class to check for immutability.
     * @return {@code true} if the class is immutable, {@code false} otherwise.
     */
    private static <T> boolean isImmutable(T original) {
        return original instanceof String ||
                original instanceof Integer ||
                original instanceof Boolean ||
                original instanceof Character ||
                original instanceof Byte ||
                original instanceof Short ||
                original instanceof Long ||
                original instanceof Float ||
                original instanceof Double;
    }

    /**
     * Creates a deep copy of a record.
     * <p>
     * This method creates a deep copy of a record by invoking its canonical
     * constructor with deeply copied field values.
     * </p>
     *
     * @param original The original record to be copied.
     * @param <T>      The type of the record.
     * @return A deep copy of the original record.
     * @throws DeepCopyException If deep copying the record fails.
     */
    private static <T> T deepCopyRecord(T original) throws DeepCopyException {
        try {
            Class<?> clazz = original.getClass();
            RecordComponent[] components = clazz.getRecordComponents();
            Object[] args = new Object[components.length];

            for (int i = 0; i < components.length; i++) {
                Method accessor = components[i].getAccessor();
                args[i] = deepCopy(accessor.invoke(original));
            }

            Constructor<?> canonicalConstructor = clazz.getDeclaredConstructor(
                    Arrays.stream(components).map(RecordComponent::getType).toArray(Class<?>[]::new)
            );

            return (T) canonicalConstructor.newInstance(args);
        } catch (Exception e) {
            throw new DeepCopyException("Failed to deep copy record of type " + original.getClass().getName(), e);
        }
    }

    /**
     * Creates a deep copy of an array.
     * <p>
     * This method creates a deep copy of an array by recursively copying each
     * element in the array.
     * </p>
     *
     * @param original The original array to be copied.
     * @param <T>      The type of the array elements.
     * @return A deep copy of the original array.
     * @throws DeepCopyException If deep copying the array fails.
     */
    private static <T> T deepCopyArray(T original) throws DeepCopyException {
        try {
            int length = Array.getLength(original);
            T copy = (T) Array.newInstance(original.getClass().getComponentType(), length);
            for (int i = 0; i < length; i++) {
                Array.set(copy, i, deepCopy(Array.get(original, i)));
            }
            return copy;
        } catch (Exception e) {
            throw new DeepCopyException("Failed to deep copy array", e);
        }
    }

    /**
     * Deep copies an object by creating a new instance and copying fields.
     * <p>
     * This method creates a new instance of the same class as the original object
     * and then copies all fields from the original object to the new instance. It
     * handles fields of various types including arrays, collections, and custom objects.
     * <b>Restrictions:</b> <i>this method is not suitable for the original objects implemented from an inner class
     * and from a class whose constructor parameters are of a type that is different from the class's
     * field types.</i>
     * </p>
     *
     * @param original The original object to be copied.
     * @param <T>      The type of the object.
     * @return A new instance of the class with copied fields.
     * @throws DeepCopyException If creating a new instance or copying fields fails.
     */
    private static <T> T deepCopyObject(T original) throws DeepCopyException {
        Constructor<?> noArgConstructor = null;

        // Check if the class has a no-argument constructor
        for (Constructor<?> constructor : original.getClass().getDeclaredConstructors()) {
            if (constructor.getParameterCount() == 0) {
                noArgConstructor = constructor;
                break;
            }
        }

        if (noArgConstructor == null) {
            return deepCopyWithArgsConstructor(original);
        } else {
            return deepCopyNoArgsConstructor(original, noArgConstructor);
        }
    }

    /**
     * Deep copies an object in case when the original object does not contain a non-argument constructor.
     * <p>
     * This method creates a new instance of the same class as the original object
     * and then copies all fields from the original object to the new instance. It gets constructors of
     * the original object and tries to find a constructor with the appropriate arguments to create a copy instance.
     * This method uses the method <i>getFieldByType</i> to check whether a type of the constructor parameter matches with
     * a type of the class field. If the method <i>getFieldByType</i> can not find a type of the constructor parameter
     * in the field types of the original class, this constructor is ignored.
     * </p>
     *
     * @param original The original object to be copied.
     * @param <T>      The type of the object.
     * @return A new instance of the class with copied fields.
     * @throws DeepCopyException If finding suitable constructor or creating copy object fails.
     */
    private static <T> T deepCopyWithArgsConstructor(T original) throws DeepCopyException {
        Constructor<?> constructorToUse = null;
        Object[] constructorArgs = null;

        // Try to find a constructor and appropriate arguments
        for (Constructor<?> constructor : original.getClass().getDeclaredConstructors()) {
            try {
                constructor.setAccessible(true);
                Class<?>[] paramTypes = constructor.getParameterTypes();
                constructorArgs = new Object[paramTypes.length];
                boolean canUseConstructor = true;

                for (int i = 0; i < paramTypes.length; i++) {
                    Field field = getFieldByType(original, paramTypes[i]);
                    if (field == null) {
                        canUseConstructor = false;
                        break;
                    }
                    field.setAccessible(true);
                    constructorArgs[i] = field.get(original);
                }

                if (canUseConstructor) {
                    constructorToUse = constructor;
                    break;
                }
            } catch (Exception e) {
                // Ignore and try next constructor
            }
        }

        if (constructorToUse == null) {
            throw new DeepCopyException("Class " + original.getClass().getName() +
                    " does not have a suitable constructor");
        }

        try {
            T copy = (T) constructorToUse.newInstance(constructorArgs);
            copyFields(original, copy);
            return copy;
        } catch (Exception e) {
            throw new DeepCopyException("Failed to deep copy object of type " + original.getClass().getName() +
                    " with parameterized constructor", e);
        }
    }

    /**
     * Retrieves a field from a class based on its type.
     * <p>
     * This method searches for a field in a class of the given object that
     * matches the specified field type. It returns the first field found with the
     * matching type or null if no such field is found.
     * </p>
     *
     * @param object The Object to search for the field.
     * @param type  The type of the field to search for.
     * @return The field matching the specified type, or {@code null} if no such field is found.
     */
    private static Field getFieldByType(Object object, Class<?> type) {
        for (Field field : object.getClass().getDeclaredFields()) {
            if (field.getType().equals(type)) {
                return field;
            }
        }
        return null;
    }

    /**
     * Deep copies an object in case when the original object contains a non-argument constructor.
     * <p>
     * This method creates a new instance of the same class as the original object by using a non-argument constructor
     * and then copies all fields from the original object to the new instance.
     * </p>
     *
     * @param original The original object to be copied.
     * @param <T>      The type of the object.
     * @return A new instance of the class with copied fields.
     * @throws DeepCopyException If creating copy object fails.
     */
    private static <T> T deepCopyNoArgsConstructor(T original, Constructor<?> noArgConstructor) throws DeepCopyException {
        try {
            noArgConstructor.setAccessible(true);
            T copy = (T) noArgConstructor.newInstance();
            copyFields(original, copy);
            return copy;
        } catch (Exception e) {
            throw new DeepCopyException("Failed to deep copy object of type " + original.getClass().getName() + " with no-arg constructor", e);
        }
    }

    /**
     * Copies fields from the original object to the copy object.
     * <p>
     * This method copies all fields from the original object to the copy object. It
     * handles different field types and ensures that mutable fields are deeply copied.
     * </p>
     *
     * @param original The original object from which fields are copied.
     * @param copy The copy object to which fields are copied.
     * @throws IllegalAccessException If accessing fields fails.
     * @throws NoSuchMethodException If a particular method or constructor cannot be found.
     * @throws InvocationTargetException If an invoked method or constructor fails.
     */
    private static <T> void copyFields(T original, T copy) throws IllegalAccessException,
            NoSuchMethodException, InvocationTargetException {
        Field[] fields = original.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true); // Make private fields accessible
            Object value = field.get(original);
             if (value instanceof Cloneable) {
                 // If the field is Cloneable, use the clone method
                 field.set(copy, value.getClass().getMethod("clone").invoke(value));
             } else if (value instanceof Serializable) {
                 // If the field is Serializable, clone the field using serialization
                 field.set(copy, deepCopyUsingSerialization(value));
             } else {
                 field.set(copy, deepCopy(value));
             }
        }
    }

    /**
     * Creates a deep copy of an object using serialization.
     *
     * @param original The original object to be copied.
     * @param <T>      The type of the object.
     * @return A deep copy of the original object.
     * @throws DeepCopyException If deep copying using serialization fails.
     */
    private static <T> T deepCopyUsingSerialization(T original) throws DeepCopyException {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(original);

            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            return (T) objectInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new DeepCopyException("Failed to deep copy field using serialization", e);
        }
    }
}
