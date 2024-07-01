package org.example.copy;

/**
 * Custom exception class for handling errors that occur during deep copy operations.
 */
public class DeepCopyException extends RuntimeException {

    /**
     * Constructs a new DeepCopyException with the specified detail message.
     *
     * @param message the detail message
     */
    public DeepCopyException(String message) {
        super(message);
    }

    /**
     * Constructs a new DeepCopyException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause of the exception
     */
    public DeepCopyException(String message, Throwable cause) {
        super(message, cause);
    }
}
