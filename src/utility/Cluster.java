package utility;

import java.util.ArrayList;

/**
 *
 * @author Gulsah Kandemir
 */
public class Cluster {
    public String name;
    public ArrayList<Integer> docList;

    public Cluster(String name, ArrayList<Integer> docList) {
        this.name = name;
        this.docList = docList;
    }
    
}
