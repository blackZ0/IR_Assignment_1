package invertedIndex;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class Test {

    /**
     * The main method for testing the inverted index functionality.
     * It builds an index from files in a specified directory, allows querying the index,
     * and provides an interactive search interface.
     *
     * @param args Command-line arguments (not used in this program).
     * @throws IOException If an I/O error occurs during file operations.
     */
    public static void main(String args[]) throws IOException {
        // Create an instance of the Index5 class to build and manage the inverted index.
        Index5 index = new Index5();

        // Define the directory containing the files to be indexed.
        String filesDirectory = "D:\\is322_HW_1\\collections\\";

        // Check if the specified directory exists and is valid.
        File directory = new File(filesDirectory);
        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("Directory does not exist or is not a directory: " + filesDirectory);
            return; // Exit the program if the directory is invalid.
        }

        // Retrieve the list of files in the directory.
        String[] fileList = directory.list();
        if (fileList == null || fileList.length == 0) {
            System.out.println("No files found in the directory: " + filesDirectory);
            return; // Exit the program if no files are found.
        }

        // Sort the list of files alphabetically using the sort method from Index5.
        fileList = index.sort(fileList);

        // Set the total number of documents (N) in the index.
        index.setN(fileList.length);

        // Prepend the directory path to each file name for full file paths.
        for (int i = 0; i < fileList.length; i++) {
            fileList[i] = filesDirectory + fileList[i];
        }

        // Build the inverted index from the list of files.
        index.buildIndex(fileList);

        // Store the index to a file named "index".
        index.store("index");

        // Print the dictionary (inverted index) to the console.
        index.printDictionary();

        // Test the search functionality with a predefined query.
        String testQuery = "data should plain greatest comif";
        System.out.println("Boolean Model result for \"" + testQuery + "\":");
        System.out.println(index.find_24_01(testQuery));

        // Set up an interactive search interface.
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String phrase;
        do {
            // Prompt the user to enter a search phrase.
            System.out.println("Enter a search phrase (or press Enter to exit): ");
            phrase = in.readLine().trim();

            // If the user enters a phrase, search the index and display the results.
            if (!phrase.isEmpty()) {
                String result = index.find_24_01(phrase);
                System.out.println(result);
            }
        } while (!phrase.isEmpty()); // Continue until the user presses Enter without typing a phrase.

        // Exit the program with a goodbye message.
        System.out.println("Exiting the program. Goodbye!");
    }
}