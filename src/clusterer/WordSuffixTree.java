package clusterer;

import java.util.ArrayList;

/**
 *
 * @author Gulsah Kandemir
 */
public class WordSuffixTree {
    
    ArrayList<Snippet> snippets;
    TrieNode root;
    
    public WordSuffixTree(ArrayList<Snippet> snippets){
        this.snippets = snippets;
        this.root = new TrieNode();
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
        for(int e=0;e<node.edges.size();e++){
            int ee = 0;
            int ww = w; //copy value of w in case
            while(ee!=node.edges.get(e).size() && ww!= snippets.get(i).getStemmedWords().size()
                    && node.edges.get(e).get(ee).equals(snippets.get(i).getStemmedWords().get(ww))) {
                ee++;
                ww++;
            }
            // case 1: sf = bcd vs. ed = bcd
            if(ww == snippets.get(i).getStemmedWords().size() && ee==node.edges.get(e).size()){
                node.children.get(e).addNewDoc(i);
                return;
            }
            // case 2: sf = bcdef vs. ed = bcd
            else if(ee == node.edges.get(e).size()){
                node.addNewDoc(i);
                findPlaceForSuffix(i, ww, node.children.get(e));
                return;
            }
            // case 3: sf = bcd vs. ed = bcde
            else if(ww == snippets.get(i).getStemmedWords().size()){
                //First backup the old node
                TrieNode tempNode = new TrieNode();
                tempNode.children = (ArrayList<TrieNode>) node.children.get(e).children.clone();
                tempNode.edges = (ArrayList<ArrayList<String>>) node.children.get(e).edges.clone();
                tempNode.docList = (ArrayList<Integer>) node.children.get(e).docList.clone();

                // Create the new node, add the old backuped node as a child
                TrieNode newNode = new TrieNode();
                newNode.children.add(tempNode);
                newNode.docList.addAll(tempNode.docList);
                newNode.addNewDoc(i);
                newNode.edges.add(new ArrayList<String>(node.edges.get(e).subList(0,ee)));
                node.children.set(e, newNode);
                
                // Update the above node's edge and remove `e`
                ArrayList<String> updatedEdge = new ArrayList( node.edges.get(e).subList(0, ee-1));
                node.edges.set(e, updatedEdge);
                node.addNewDoc(i);
                return;
            }
            // case 4: bcd vs bck
            else if(ee>0){
                //First backup the old node
                TrieNode tempNode = new TrieNode();
                tempNode.children = (ArrayList<TrieNode>) node.children.get(e).children.clone();
                tempNode.edges = (ArrayList<ArrayList<String>>) node.children.get(e).edges.clone();
                tempNode.docList = (ArrayList<Integer>) node.children.get(e).docList.clone();
                
                // Create the new node, add the old backuped node as a child
                TrieNode newNode = new TrieNode();
                newNode.children.add(tempNode);
                newNode.docList.addAll(tempNode.docList);
                newNode.addNewDoc(i);
                newNode.edges.add(new ArrayList<String>(node.edges.get(e).subList(ee,node.edges.get(e).size())));
                node.children.set(e, newNode);
                
                // Create 2nd new node
                TrieNode newNode2 = new TrieNode();
                newNode2.addNewDoc(i);

                newNode.children.add(newNode2);
                newNode.edges.add(new ArrayList<String>( snippets.get(i).getStemmedWords().
                                          subList(ww,snippets.get(i).getStemmedWords().size() )));

                // Update the above node's edge and remove `e`
                ArrayList<String> updatedEdge = new ArrayList( node.edges.get(e).subList(0, ee));
                node.edges.set(e, updatedEdge);
                node.addNewDoc(i);
                return;
            }
        }
        
        // Case 5: No edges found, add a new node
        TrieNode newNode = new TrieNode();
        newNode.addNewDoc(i);
        node.children.add(newNode);
        node.edges.add(new ArrayList<String>(snippets.get(i).getStemmedWords().
                                subList(w, snippets.get(i).getStemmedWords().size())));
        node.addNewDoc(i);        
    }
}
