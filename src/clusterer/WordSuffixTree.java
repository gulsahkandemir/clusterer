package clusterer;

import java.util.ArrayList;

/**
 *
 * @author Gulsah Kandemir
 */
public class WordSuffixTree {
    
    private ArrayList<Snippet> snippets;
    private TrieNode root;
    
    public WordSuffixTree(ArrayList<Snippet> snippets){
        this.snippets = snippets;
        this.root = new TrieNode();
    }

    public ArrayList<Snippet> getSnippets() {
        return snippets;
    }

    public void setSnippets(ArrayList<Snippet> snippets) {
        this.snippets = snippets;
    }

    public TrieNode getRoot() {
        return root;
    }

    public void setRoot(TrieNode root) {
        this.root = root;
    }


    public TrieNode constructSuffixTree(){
        for(int i=0;i<snippets.size();i++){
            for(int w=0;w<snippets.get(i).getStemmedWords().size();w++){
                findPlaceForSuffix(i,w,root);
            }
        }
        return root;
    }


    private void findPlaceForSuffix(int i, int w, TrieNode node) {
        //for all edges do comparison
        for(int e=0;e<node.getEdges().size();e++){
            int ee = 0;
            int ww = w; //copy value of w in case
            while(ee!=node.getEdges().get(e).size() && ww!= snippets.get(i).getStemmedWords().size()
                    && node.getEdges().get(e).get(ee).equals(snippets.get(i).getStemmedWords().get(ww))) {
                ee++;
                ww++;
            }
            // case 1: sf = bcd vs. ed = bcd
            if(ww == snippets.get(i).getStemmedWords().size() && ee==node.getEdges().get(e).size()){
                node.getChildren().get(e).addNewDoc(i);
                return;
            }
            // case 2: sf = bcdef vs. ed = bcd
            else if(ee == node.getEdges().get(e).size()){
                node.addNewDoc(i);
                findPlaceForSuffix(i, ww, node.getChildren().get(e));
                return;
            }
            // case 3: sf = bcd vs. ed = bcde
            else if(ww == snippets.get(i).getStemmedWords().size()){
                //First backup the old node
                TrieNode tempNode = new TrieNode();
                tempNode.setChildren((ArrayList<TrieNode>) node.getChildren().get(e).getChildren().clone());
                tempNode.setEdges((ArrayList<ArrayList<String>>) node.getChildren().get(e).getEdges().clone());
                tempNode.setDocList((ArrayList<Integer>) node.getChildren().get(e).getDocList().clone());

                // Create the new node, add the old backuped node as a child
                TrieNode newNode = new TrieNode();
                newNode.getChildren().add(tempNode);
                newNode.getDocList().addAll(tempNode.getDocList());
                newNode.addNewDoc(i);
                newNode.getEdges().add(new ArrayList<String>(node.getEdges().get(e).subList(0,ee)));
                node.getChildren().set(e, newNode);
                
                // Update the above node's edge and remove `e`
                ArrayList<String> updatedEdge = new ArrayList( node.getEdges().get(e).subList(0, ee-1));
                node.getEdges().set(e, updatedEdge);
                node.addNewDoc(i);
                return;
            }
            // case 4: bcd vs bck
            else if(ee>0){
                //First backup the old node
                TrieNode tempNode = new TrieNode();
                tempNode.setChildren((ArrayList<TrieNode>) node.getChildren().get(e).getChildren().clone());
                tempNode.setEdges((ArrayList<ArrayList<String>>) node.getChildren().get(e).getEdges().clone());
                tempNode.setDocList((ArrayList<Integer>) node.getChildren().get(e).getDocList().clone());
                
                // Create the new node, add the old backuped node as a child
                TrieNode newNode = new TrieNode();
                newNode.getChildren().add(tempNode);
                newNode.getDocList().addAll(tempNode.getDocList());
                newNode.addNewDoc(i);
                newNode.getEdges().add(new ArrayList<String>(node.getEdges().get(e).subList(ee,node.getEdges().get(e).size())));
                node.getChildren().set(e, newNode);
                
                // Create 2nd new node
                TrieNode newNode2 = new TrieNode();
                newNode2.addNewDoc(i);

                newNode.getChildren().add(newNode2);
                newNode.getEdges().add(new ArrayList<String>( snippets.get(i).getStemmedWords().
                                          subList(ww,snippets.get(i).getStemmedWords().size() )));

                // Update the above node's edge and remove `e`
                ArrayList<String> updatedEdge = new ArrayList( node.getEdges().get(e).subList(0, ee));
                node.getEdges().set(e, updatedEdge);
                node.addNewDoc(i);
                return;
            }
        }
        
        // Case 5: No edges found, add a new node
        TrieNode newNode = new TrieNode();
        newNode.addNewDoc(i);
        node.getChildren().add(newNode);
        node.getEdges().add(new ArrayList<String>(snippets.get(i).getStemmedWords().
                                subList(w, snippets.get(i).getStemmedWords().size())));
        node.addNewDoc(i);        
    }
}
