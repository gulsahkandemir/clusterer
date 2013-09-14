package clusterer;

import java.util.ArrayList;

/**
 *
 * @author Gulsah Kandemir
 */
public class Snippet {

    private ArrayList<String> origWords;
    private ArrayList<String> stemmedWords;
    private String url;
    private String description;

    public Snippet(ArrayList<String> origWords, String url, String description) {
        this.origWords = origWords;
        this.url = url;
        this.description = description;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }

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
