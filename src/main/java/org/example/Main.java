package org.example;

import org.example.copy.CopyUtils;
import org.example.copy.DeepCopyException;
import org.example.source.Man;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Main class for demonstrating the deep copy functionality.
 * This class creates an instance of the class with sample data,
 * performs a deep copy, and prints both the original and copied objects.
 */
public class Main {

    /**
     * The entry point of the application.
     *
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        try {
            // Create list of favorite books
            ArrayList<String> favoriteBooks = new ArrayList<>();
            Collections.addAll(favoriteBooks, "Design Patterns: Elements of Reusable Object-Oriented Software",
                    "The Pragmatic Programmer: Your Journey to Mastery");

            // Create an instance of the class with some sample data
            Man original = new Man("John", 30, favoriteBooks);

            // Perform a deep copy of the original object
            Man copied = CopyUtils.deepCopy(original);

            // Print the original and copied objects
            System.out.println("Original Object: " + original);
            System.out.println("Copied Object: " + copied);

        } catch (DeepCopyException e) {
            System.out.println("Error occurred during the copy: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Performing a copy is not successful.");
        }
    }
}