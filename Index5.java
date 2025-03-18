/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package invertedIndex;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.lang.Math.log10;
import static java.lang.Math.sqrt;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.io.PrintWriter;

/**
 *
 * @author ehab
 */
public class Index5 {

    //--------------------------------------------
    int N = 0;
    public Map<Integer, SourceRecord> sources;  // store the doc_id and the file name.

    public HashMap<String, DictEntry> index; // THe inverted index
    //--------------------------------------------

    /**
     * Constructor for Index5 class.
     * Initializes the sources and index HashMaps.
     */
    public Index5() {
        sources = new HashMap<Integer, SourceRecord>();
        index = new HashMap<String, DictEntry>();
    }

    /**
     * Sets the total number of documents (N) in the collection.
     * @param n The number of documents.
     */
    public void setN(int n) {
        N = n;
    }

    //---------------------------------------------
    /**
     * Prints the posting list for a given Posting object.
     * @param p The Posting object to print.
     */
    public void printPostingList(Posting p) {
        System.out.print("[");
        while (p != null) {
            System.out.print(p.docId);
            p = p.next;
            if (p != null) {
                System.out.print(",");
            }
        }
        System.out.println("]");
    }

    //---------------------------------------------
    /**
     * Prints the entire dictionary (inverted index) with term frequencies and posting lists.
     */
    public void printDictionary() {
        Iterator it = index.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            DictEntry dd = (DictEntry) pair.getValue();
            System.out.print("** [" + pair.getKey() + "," + dd.doc_freq + "]       =--> ");
            printPostingList(dd.pList);
        }
        System.out.println("------------------------------------------------------");
        System.out.println("*** Number of terms = " + index.size());
    }

    //-----------------------------------------------
    /**
     * Builds the inverted index from a list of files.
     * @param files An array of file names to be indexed.
     */
    public void buildIndex(String[] files) {
        int fid = 0;
        for (String fileName : files) {
            try (BufferedReader file = new BufferedReader(new FileReader(fileName))) {
                if (!sources.containsKey(fileName)) {
                    sources.put(fid, new SourceRecord(fid, fileName, fileName, "notext"));
                }
                String ln;
                int flen = 0;
                while ((ln = file.readLine()) != null) {
                    flen += indexOneLine(ln, fid); // Process each line and update the index
                }
                sources.get(fid).length = flen; // Update the length of the document
            } catch (IOException e) {
                System.out.println("File " + fileName + " not found. Skip it");
            }
            fid++;
        }
    }

    //----------------------------------------------------------------------------
    /**
     * Indexes a single line of text from a document.
     * @param ln The line of text to index.
     * @param fid The document ID.
     * @return The number of words indexed in this line.
     */
    public int indexOneLine(String ln, int fid) {
        int flen = 0;

        String[] words = ln.split("\\W+");
        flen += words.length;
        for (String word : words) {
            word = word.toLowerCase();
            if (stopWord(word)) {
                continue;
            }
            word = stemWord(word);
            // check to see if the word is not in the dictionary
            // if not add it
            if (!index.containsKey(word)) {
                index.put(word, new DictEntry());
            }
            // add document id to the posting list
            if (!index.get(word).postingListContains(fid)) {
                index.get(word).doc_freq += 1; //set doc freq to the number of doc that contain the term
                if (index.get(word).pList == null) {
                    index.get(word).pList = new Posting(fid);
                    index.get(word).last = index.get(word).pList;
                } else {
                    index.get(word).last.next = new Posting(fid);
                    index.get(word).last = index.get(word).last.next;
                }
            } else {
                index.get(word).last.dtf += 1;
            }
            //set the term_fteq in the collection
            index.get(word).term_freq += 1;
            if (word.equalsIgnoreCase("lattice")) {
                System.out.println("  <<" + index.get(word).getPosting(1) + ">> " + ln);
            }
        }
        return flen;
    }

    //----------------------------------------------------------------------------
    /**
     * Checks if a word is a stop word.
     * @param word The word to check.
     * @return True if the word is a stop word, false otherwise.
     */
    boolean stopWord(String word) {
        if (word.equals("the") || word.equals("to") || word.equals("be") || word.equals("for") || word.equals("from") || word.equals("in")
                || word.equals("a") || word.equals("into") || word.equals("by") || word.equals("or") || word.equals("and") || word.equals("that")) {
            return true;
        }
        if (word.length() < 2) {
            return true;
        }
        return false;
    }

    //----------------------------------------------------------------------------
    /**
     * Stems a word (currently a placeholder, returns the word as is).
     * @param word The word to stem.
     * @return The stemmed word.
     */
    String stemWord(String word) { //skip for now
        return word;
    }

    //----------------------------------------------------------------------------
    /**
     * Intersects two posting lists to find common document IDs.
     * @param pL1 The first posting list.
     * @param pL2 The second posting list.
     * @return A new posting list containing the intersection of the two input lists.
     */
    Posting intersect(Posting pL1, Posting pL2) {
        Posting answer = null;
        Posting last = null;

        while (pL1 != null && pL2 != null) {
            if (pL1.docId == pL2.docId) {
                // Add the document ID to the result list
                if (answer == null) {
                    answer = new Posting(pL1.docId);
                    last = answer;
                } else {
                    last.next = new Posting(pL1.docId);
                    last = last.next;
                }
                pL1 = pL1.next;
                pL2 = pL2.next;
            } else if (pL1.docId < pL2.docId) {
                pL1 = pL1.next;
            } else {
                pL2 = pL2.next;
            }
        }

        return answer;
    }

    /**
     * Finds documents that contain a given phrase.
     * @param phrase The phrase to search for.
     * @return A string listing the documents containing the phrase.
     */
    public String find_24_01(String phrase) {
        String result = "";
        String[] words = phrase.split("\\W+");
        int len = words.length;

        // Check if the first word exists in the index
        if (!index.containsKey(words[0].toLowerCase())) {
            return "No documents found for the phrase: " + phrase;
        }

        Posting posting = index.get(words[0].toLowerCase()).pList;
        int i = 1;
        while (i < len) {
            // Check if the current word exists in the index
            if (!index.containsKey(words[i].toLowerCase())) {
                return "No documents found for the phrase: " + phrase;
            }
            posting = intersect(posting, index.get(words[i].toLowerCase()).pList);
            i++;
        }

        // Collect results from the posting list
        while (posting != null) {
            result += "\t" + posting.docId + " - " + sources.get(posting.docId).title + " - " + sources.get(posting.docId).length + "\n";
            posting = posting.next;
        }

        // If no results were found, return a message
        if (result.isEmpty()) {
            return "No documents found for the phrase: " + phrase;
        }

        return result;
    }

    //---------------------------------
    /**
     * Sorts an array of words using bubble sort.
     * @param words The array of words to sort.
     * @return The sorted array of words.
     */
    String[] sort(String[] words) {  //bubble sort
        boolean sorted = false;
        String sTmp;
        //-------------------------------------------------------
        while (!sorted) {
            sorted = true;
            for (int i = 0; i < words.length - 1; i++) {
                int compare = words[i].compareTo(words[i + 1]);
                if (compare > 0) {
                    sTmp = words[i];
                    words[i] = words[i + 1];
                    words[i + 1] = sTmp;
                    sorted = false;
                }
            }
        }
        return words;
    }

    //---------------------------------

    /**
     * Stores the index and source records to a file.
     * @param storageName The name of the file to store the data.
     */
    public void store(String storageName) {
        try {
            String pathToStorage = "D:\\is322_HW_1\\collections\\"+storageName;
            Writer wr = new FileWriter(pathToStorage);
            for (Map.Entry<Integer, SourceRecord> entry : sources.entrySet()) {
                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue().URL + ", Value = " + entry.getValue().title + ", Value = " + entry.getValue().text);
                wr.write(entry.getKey().toString() + ",");
                wr.write(entry.getValue().URL.toString() + ",");
                wr.write(entry.getValue().title.replace(',', '~') + ",");
                wr.write(entry.getValue().length + ","); //String formattedDouble = String.format("%.2f", fee );
                wr.write(String.format("%4.4f", entry.getValue().norm) + ",");
                wr.write(entry.getValue().text.toString().replace(',', '~') + "\n");
            }
            wr.write("section2" + "\n");

            Iterator it = index.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                DictEntry dd = (DictEntry) pair.getValue();
                //  System.out.print("** [" + pair.getKey() + "," + dd.doc_freq + "] <" + dd.term_freq + "> =--> ");
                wr.write(pair.getKey().toString() + "," + dd.doc_freq + "," + dd.term_freq + ";");
                Posting p = dd.pList;
                while (p != null) {
                    //    System.out.print( p.docId + "," + p.dtf + ":");
                    wr.write(p.docId + "," + p.dtf + ":");
                    p = p.next;
                }
                wr.write("\n");
            }
            wr.write("end" + "\n");
            wr.close();
            System.out.println("=============EBD STORE=============");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //=========================================
    /**
     * Checks if a storage file exists.
     * @param storageName The name of the storage file.
     * @return True if the file exists, false otherwise.
     */
    public boolean storageFileExists(String storageName){
        java.io.File f = new java.io.File("/home/ehab/tmp11/rl/"+storageName);
        if (f.exists() && !f.isDirectory())
            return true;
        return false;
    }

    //----------------------------------------------------
    /**
     * Creates a new storage file.
     * @param storageName The name of the storage file to create.
     */
    public void createStore(String storageName) {
        try {
            String pathToStorage = "/home/ehab/tmp11/"+storageName;
            Writer wr = new FileWriter(pathToStorage);
            wr.write("end" + "\n");
            wr.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //----------------------------------------------------
    /**
     * Loads the index and source records from a storage file.
     * @param storageName The name of the storage file.
     * @return The loaded index as a HashMap.
     */
    public HashMap<String, DictEntry> load(String storageName) {
        try {
            String pathToStorage = "/home/ehab/tmp11/rl/"+storageName;
            sources = new HashMap<Integer, SourceRecord>();
            index = new HashMap<String, DictEntry>();
            BufferedReader file = new BufferedReader(new FileReader(pathToStorage));
            String ln = "";
            int flen = 0;
            while ((ln = file.readLine()) != null) {
                if (ln.equalsIgnoreCase("section2")) {
                    break;
                }
                String[] ss = ln.split(",");
                int fid = Integer.parseInt(ss[0]);
                try {
                    System.out.println("**>>" + fid + " " + ss[1] + " " + ss[2].replace('~', ',') + " " + ss[3] + " [" + ss[4] + "]   " + ss[5].replace('~', ','));

                    SourceRecord sr = new SourceRecord(fid, ss[1], ss[2].replace('~', ','), Integer.parseInt(ss[3]), Double.parseDouble(ss[4]), ss[5].replace('~', ','));
                    //   System.out.println("**>>"+fid+" "+ ss[1]+" "+ ss[2]+" "+ ss[3]+" ["+ Double.parseDouble(ss[4])+ "]  \n"+ ss[5]);
                    sources.put(fid, sr);
                } catch (Exception e) {
                    System.out.println(fid + "  ERROR  " + e.getMessage());
                    e.printStackTrace();
                }
            }
            while ((ln = file.readLine()) != null) {
                //     System.out.println(ln);
                if (ln.equalsIgnoreCase("end")) {
                    break;
                }
                String[] ss1 = ln.split(";");
                String[] ss1a = ss1[0].split(",");
                String[] ss1b = ss1[1].split(":");
                index.put(ss1a[0], new DictEntry(Integer.parseInt(ss1a[1]), Integer.parseInt(ss1a[2])));
                String[] ss1bx;   //posting
                for (int i = 0; i < ss1b.length; i++) {
                    ss1bx = ss1b[i].split(",");
                    if (index.get(ss1a[0]).pList == null) {
                        index.get(ss1a[0]).pList = new Posting(Integer.parseInt(ss1bx[0]), Integer.parseInt(ss1bx[1]));
                        index.get(ss1a[0]).last = index.get(ss1a[0]).pList;
                    } else {
                        index.get(ss1a[0]).last.next = new Posting(Integer.parseInt(ss1bx[0]), Integer.parseInt(ss1bx[1]));
                        index.get(ss1a[0]).last = index.get(ss1a[0]).last.next;
                    }
                }
            }
            System.out.println("============= END LOAD =============");
            //    printDictionary();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return index;
    }
}