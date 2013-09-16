package clusterer;

import java.util.ArrayList;

/**
 *
 * @author Gulsah Kandemir
 */
public class Cluster {
    private String name;
    private ArrayList<Integer> docList;

    public Cluster(String name, ArrayList<Integer> docList) {
        this.name = name;
        this.docList = docList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Integer> getDocList() {
        return docList;
    }

    public void setDocList(ArrayList<Integer> docList) {
        this.docList = docList;
    }
    
}
