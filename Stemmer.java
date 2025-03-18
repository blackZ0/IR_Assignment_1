package invertedIndex;

/**
 * Stemmer, implementing the Porter Stemming Algorithm.
 * Transforms a word into its root form.
 */
public class Stemmer {

    private char[] b; // Buffer to hold the word
    private int i;     // Current index in the buffer
    private int i_end; // End index of the stemmed word
    private int j, k; // Indices for processing

    private static final int INC = 50; // Increment size for buffer expansion

    /**
     * Constructor for Stemmer.
     * Initializes the buffer and indices.
     */
    public Stemmer() {
        b = new char[INC];
        i = 0;
        i_end = 0;
    }

    /**
     * Adds a string to the buffer for stemming.
     * @param s The string to add.
     */
    public void addString(String s) {
        add(s.toCharArray(), s.length());
    }

    /**
     * Adds a character to the buffer for stemming.
     * @param ch The character to add.
     */
    public void add(char ch) {
        if (i == b.length) {
            expandBuffer();
        }
        b[i++] = ch;
    }

    /**
     * Adds an array of characters to the buffer for stemming.
     * @param w The character array.
     * @param wLen The length of the array.
     */
    public void add(char[] w, int wLen) {
        if (i + wLen >= b.length) {
            expandBuffer(i + wLen + INC);
        }
        for (int c = 0; c < wLen; c++) {
            b[i++] = w[c];
        }
    }

    /**
     * Expands the buffer to accommodate more characters.
     * @param newSize The new size of the buffer.
     */
    private void expandBuffer(int newSize) {
        char[] new_b = new char[newSize];
        System.arraycopy(b, 0, new_b, 0, i);
        b = new_b;
    }

    /**
     * Expands the buffer by the default increment size.
     */
    private void expandBuffer() {
        expandBuffer(i + INC);
    }

    /**
     * Returns the stemmed word as a string.
     * @return The stemmed word.
     */
    @Override
    public String toString() {
        return new String(b, 0, i_end);
    }

    /**
     * Returns the length of the stemmed word.
     * @return The length of the stemmed word.
     */
    public int getResultLength() {
        return i_end;
    }

    /**
     * Returns the buffer containing the stemmed word.
     * @return The buffer.
     */
    public char[] getResultBuffer() {
        return b;
    }

    /**
     * Checks if the character at the given index is a consonant.
     * @param i The index to check.
     * @return True if the character is a consonant, false otherwise.
     */
    private boolean cons(int i) {
        switch (b[i]) {
            case 'a':
            case 'e':
            case 'i':
            case 'o':
            case 'u':
                return false;
            case 'y':
                return (i == 0) ? true : !cons(i - 1);
            default:
                return true;
        }
    }

    /**
     * Measures the number of consonant sequences between 0 and j.
     * @return The number of consonant sequences.
     */
    private int m() {
        int n = 0;
        int i = 0;
        while (true) {
            if (i > j) return n;
            if (!cons(i)) break;
            i++;
        }
        i++;
        while (true) {
            while (true) {
                if (i > j) return n;
                if (cons(i)) break;
                i++;
            }
            i++;
            n++;
            while (true) {
                if (i > j) return n;
                if (!cons(i)) break;
                i++;
            }
            i++;
        }
    }

    /**
     * Checks if the stem contains a vowel.
     * @return True if the stem contains a vowel, false otherwise.
     */
    private boolean vowelinstem() {
        for (int i = 0; i <= j; i++) {
            if (!cons(i)) return true;
        }
        return false;
    }

    /**
     * Checks if the characters at j and j-1 are the same consonant.
     * @param j The index to check.
     * @return True if the characters are the same consonant, false otherwise.
     */
    private boolean doublec(int j) {
        if (j < 1) return false;
        if (b[j] != b[j - 1]) return false;
        return cons(j);
    }

    /**
     * Checks if the characters at i-2, i-1, i form a consonant-vowel-consonant sequence.
     * @param i The index to check.
     * @return True if the sequence is CVC, false otherwise.
     */
    private boolean cvc(int i) {
        if (i < 2 || !cons(i) || cons(i - 1) || !cons(i - 2)) return false;
        char ch = b[i];
        return ch != 'w' && ch != 'x' && ch != 'y';
    }

    /**
     * Checks if the stem ends with the given string.
     * @param s The string to check.
     * @return True if the stem ends with s, false otherwise.
     */
    private boolean ends(String s) {
        int l = s.length();
        int o = k - l + 1;
        if (o < 0) return false;
        for (int i = 0; i < l; i++) {
            if (b[o + i] != s.charAt(i)) return false;
        }
        j = k - l;
        return true;
    }

    /**
     * Replaces the end of the stem with the given string.
     * @param s The string to replace the end with.
     */
    private void setto(String s) {
        int l = s.length();
        int o = j + 1;
        for (int i = 0; i < l; i++) {
            b[o + i] = s.charAt(i);
        }
        k = j + l;
    }

    /**
     * Replaces the end of the stem with the given string if m() > 0.
     * @param s The string to replace the end with.
     */
    private void r(String s) {
        if (m() > 0) setto(s);
    }

    /**
     * Steps through the Porter stemming algorithm.
     */
    public void stem() {
        k = i - 1;
        if (k > 1) {
            step1();
            step2();
            step3();
            step4();
            step5();
            step6();
        }
        i_end = k + 1;
        i = 0;
    }

    // Step 1 of the Porter stemming algorithm
    private void step1() {
        if (b[k] == 's') {
            if (ends("sses")) k -= 2;
            else if (ends("ies")) setto("i");
            else if (b[k - 1] != 's') k--;
        }
        if (ends("eed")) {
            if (m() > 0) k--;
        } else if ((ends("ed") || ends("ing")) && vowelinstem()) {
            k = j;
            if (ends("at")) setto("ate");
            else if (ends("bl")) setto("ble");
            else if (ends("iz")) setto("ize");
            else if (doublec(k)) {
                k--;
                char ch = b[k];
                if (ch == 'l' || ch == 's' || ch == 'z') k++;
            } else if (m() == 1 && cvc(k)) setto("e");
        }
    }

    // Step 2 of the Porter stemming algorithm
    private void step2() {
        if (ends("y") && vowelinstem()) b[k] = 'i';
    }

    // Step 3 of the Porter stemming algorithm
    private void step3() {
        if (k == 0) return;
        switch (b[k - 1]) {
            case 'a':
                if (ends("ational")) r("ate");
                else if (ends("tional")) r("tion");
                break;
            case 'c':
                if (ends("enci")) r("ence");
                else if (ends("anci")) r("ance");
                break;
            case 'e':
                if (ends("izer")) r("ize");
                break;
            case 'l':
                if (ends("bli")) r("ble");
                else if (ends("alli")) r("al");
                else if (ends("entli")) r("ent");
                else if (ends("eli")) r("e");
                else if (ends("ousli")) r("ous");
                break;
            case 'o':
                if (ends("ization")) r("ize");
                else if (ends("ation")) r("ate");
                else if (ends("ator")) r("ate");
                break;
            case 's':
                if (ends("alism")) r("al");
                else if (ends("iveness")) r("ive");
                else if (ends("fulness")) r("ful");
                else if (ends("ousness")) r("ous");
                break;
            case 't':
                if (ends("aliti")) r("al");
                else if (ends("iviti")) r("ive");
                else if (ends("biliti")) r("ble");
                break;
            case 'g':
                if (ends("logi")) r("log");
                break;
        }
    }

    // Step 4 of the Porter stemming algorithm
    private void step4() {
        switch (b[k]) {
            case 'e':
                if (ends("icate")) r("ic");
                else if (ends("ative")) r("");
                else if (ends("alize")) r("al");
                break;
            case 'i':
                if (ends("iciti")) r("ic");
                break;
            case 'l':
                if (ends("ical")) r("ic");
                else if (ends("ful")) r("");
                break;
            case 's':
                if (ends("ness")) r("");
                break;
        }
    }

    // Step 5 of the Porter stemming algorithm
    private void step5() {
        if (k == 0) return;
        switch (b[k - 1]) {
            case 'a':
                if (ends("al")) break;
                return;
            case 'c':
                if (ends("ance")) break;
                if (ends("ence")) break;
                return;
            case 'e':
                if (ends("er")) break;
                return;
            case 'i':
                if (ends("ic")) break;
                return;
            case 'l':
                if (ends("able")) break;
                if (ends("ible")) break;
                return;
            case 'n':
                if (ends("ant")) break;
                if (ends("ement")) break;
                if (ends("ment")) break;
                if (ends("ent")) break;
                return;
            case 'o':
                if (ends("ion") && j >= 0 && (b[j] == 's' || b[j] == 't')) break;
                if (ends("ou")) break;
                return;
            case 's':
                if (ends("ism")) break;
                return;
            case 't':
                if (ends("ate")) break;
                if (ends("iti")) break;
                return;
            case 'u':
                if (ends("ous")) break;
                return;
            case 'v':
                if (ends("ive")) break;
                return;
            case 'z':
                if (ends("ize")) break;
                return;
            default:
                return;
        }
        if (m() > 1) k = j;
    }

    // Step 6 of the Porter stemming algorithm
    private void step6() {
        j = k;
        if (b[k] == 'e') {
            int a = m();
            if (a > 1 || (a == 1 && !cvc(k - 1))) k--;
        }
        if (b[k] == 'l' && doublec(k) && m() > 1) k--;
    }
}