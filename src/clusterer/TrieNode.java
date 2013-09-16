package clusterer;

import java.util.ArrayList;

/**
 *
 * @author Gulsah Kandemir
 */
public class TrieNode {

    private ArrayList<Integer> docList;
    private ArrayList<TrieNode> children;
    private ArrayList<ArrayList<String>> edges;

    public TrieNode() {
        children = new ArrayList<TrieNode>();
        docList = new ArrayList<Integer>();
        edges = new ArrayList<ArrayList<String>>();

    }

    public ArrayList<Integer> getDocList() {
        return docList;
    }

    public void setDocList(ArrayList<Integer> docList) {
        this.docList = docList;
    }

    public ArrayList<TrieNode> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<TrieNode> children) {
        this.children = children;
    }

    public ArrayList<ArrayList<String>> getEdges() {
        return edges;
    }

    public void setEdges(ArrayList<ArrayList<String>> edges) {
        this.edges = edges;
    }

    public void addNewDoc(int i) {
        boolean flag = true;
        for (int d = 0; d < docList.size(); d++) {
            if (docList.get(d) == i) {
                flag = false;
            }
        }
        if (flag) {
            docList.add(i);
        }
    }

    public void print(int tabC) {
        for (int i = 0; i < tabC; i++) {
            System.out.print('\t');
        }
        System.out.print("Doc List: ");
        for (int i = 0; i < docList.size(); i++) {
            System.out.print(docList.get(i) + " ");
        }
        System.out.println();


        for (int i = 0; i < tabC; i++) {
            System.out.print('\t');
        }
        System.out.println("Edge List: ");
        for (int i = 0; i < edges.size(); i++) {
            for (int j = 0; j < tabC; j++) {
                System.out.print('\t');
            }
            System.out.println(edges.get(i));
        }



        for (int i = 0; i < tabC; i++) {
            System.out.print('\t');
        }
        System.out.println("Children: ");
        for (int i = 0; i < children.size(); i++) {
            for (int j = 0; j < tabC+1; j++) {
                System.out.print('\t');
            }
            System.out.println("------ Child " + i + "---------");
            children.get(i).print(tabC + 1);
        }

    }
}
