Overview
-----------
The program consists of two main classes:
Index5: Handles the creation, storage, and querying of the inverted index.
Test: The main class that sets up the environment, builds the index, and provides an interactive search interface.
The inverted index is built from a collection of text files stored in a specified directory. The program supports:
Indexing documents.
Storing the index to a file.
Querying the index using a Boolean model.
Interactive search functionality.
__________________________________________________________________________________-
How It Works
-------------
Index Building:
The program reads text files from a specified directory.
For each file, it processes the text line by line, tokenizes the words, and updates the inverted index.
The index maps each term to a DictEntry, which contains:
Term frequency (term_freq).
Document frequency (doc_freq).
A posting list (pList) of documents containing the term.
Querying:
The program supports phrase queries.
It uses the inverted index to find documents that contain all terms in the query.
Results are displayed as a list of document IDs, titles, and lengths.
Interactive Search:
Users can enter search phrases interactively.
The program retrieves and displays matching documents.
______________________________________________________________________________________-
Code Structure
-----------------
Classes
Index5:
Attributes:
sources: A map of document IDs to SourceRecord objects (stores file metadata).
index: The inverted index, implemented as a HashMap<String, DictEntry>.
Methods:
buildIndex: Builds the index from a list of files.
indexOneLine: Processes a single line of text and updates the index.
stopWord: Filters out common stop words.
stemWord: Placeholder for stemming functionality (currently returns the word as-is).
intersect: Finds common documents between two posting lists.
find_24_01: Searches the index for a given phrase.
store: Saves the index to a file.
load: Loads the index from a file.
Test:
Main Method:
Sets up the directory and file paths.
Builds and stores the index.
Provides an interactive search interface.
_________________________________________________________________________________________--
