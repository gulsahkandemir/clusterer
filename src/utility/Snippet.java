package utility;

import java.util.ArrayList;

/**
 *
 * @author Gulsah Kandemir
 */
public class Snippet {
    private ArrayList<String> origWords;
    private ArrayList<String> stemmedWords;

    public ArrayList<String> getOrigWords() {
        return origWords;
    }

    public ArrayList<String> getStemmedWords() {
        return stemmedWords;
    }

    public void setOrigWords(ArrayList<String> origWords) {
        this.origWords = origWords;
    }

    public void setStemmedWords(ArrayList<String> stemmedWords) {
        this.stemmedWords = stemmedWords;
    }
}
