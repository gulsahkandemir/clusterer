package clusterer;

import clusterer.Cluster;
import libs.Combinator;
import java.util.ArrayList;

/**
 *
 * @author Gulsah Kandemir
 */
public class Clusterer {
    
    public TrieNode suffixRoot;
    public ArrayList<Cluster> baseClusters;
    boolean[][] edgeTable;
    public boolean[] visited;
    ArrayList<ArrayList<Integer>> finalList;

    public Clusterer(TrieNode suffixRoot) {
        this.suffixRoot = suffixRoot;
        baseClusters = new ArrayList<Cluster>();
    }

    public void identifyBaseClusters() {
        traverseTree(suffixRoot,"","");
        if(!baseClusters.isEmpty()) {
            baseClusters.remove(0);
        }
        for(int a=0;a<baseClusters.size();a++){
            for(int st=0;st<StopWord.stopWords.length;st++){
                if(baseClusters.get(a).name.trim().equalsIgnoreCase(StopWord.stopWords[st])){
                    //System.out.print(baseClusters.get(a).name);
                    baseClusters.remove(a--);
                }
            }
        }

        System.out.println("BASE CLUSTERS----------------------------");
        for(int k=0;k<baseClusters.size();k++){
            System.out.println(k+" "+baseClusters.get(k).name+"\t" +baseClusters.get(k).docList.toString());
        }
        System.out.println("-----------------------------------------");
    }

    public void traverseTree(TrieNode node, String parentName,String edgeName){

        if(node.docList.size() >=2){
            String name = parentName+ edgeName;

            Cluster newCluster = new Cluster(name,
                                    (ArrayList<Integer>) node.docList.clone());
            baseClusters.add(newCluster);
            for(int ch = 0;ch<node.children.size();ch++){
                String edge = "";
                //int ed=0;
                for(int ed=0;ed<node.edges.get(ch).size();ed++)
                    edge += node.edges.get(ch).get(ed)+" ";

                traverseTree(node.children.get(ch),name,edge);
            }
        }
    }

    public boolean isSimilar(Cluster a, Cluster b){
        double numIntersectionsAB = 0;
        for(int i=0;i<a.docList.size();i++){
            for(int j=0;j<b.docList.size();j++){
                if(a.docList.get(i) == b.docList.get(j))
                    numIntersectionsAB++;
            }
        }
        if(numIntersectionsAB > (a.docList.size()/2.0) && 
                numIntersectionsAB > b.docList.size()/2.0){
            return true;
        }
        return false;
    }
    
    public void connectBaseClusters() {
        Integer[] clusArr = new Integer[baseClusters.size()];
        edgeTable = new boolean[baseClusters.size()][baseClusters.size()];
        visited = new boolean[baseClusters.size()];
        finalList = new ArrayList<ArrayList<Integer>>();

        for(int i=0;i<baseClusters.size();i++){
            clusArr[i] = i;
        }
        Combinator<Integer> combinator = new Combinator<Integer>(clusArr, 2);

        for (Integer[] combination : combinator) {
            if(isSimilar(baseClusters.get(combination[0]),baseClusters.get(combination[1]))){
                edgeTable[combination[0]][combination[1]] = true;
                edgeTable[combination[1]][combination[0]] = true;
            }            
        }
        
        // Traverse edges to find connected components
        for(int i=0;i<baseClusters.size();i++){
            ArrayList<Integer> aFinalList = new ArrayList<Integer>();
           
            findConnectedComponents(i,aFinalList);
            if(!aFinalList.isEmpty()) {
                finalList.add(aFinalList);
            }
        }
        
        // Print final list
        System.out.println("FINAL CLUSTERS--------------------------");
        for(int i=0;i<finalList.size();i++){
            System.out.println((finalList.get(i).toString()));
        }
        System.out.println("----------------------------------------");

    }

    private void findConnectedComponents(int i, ArrayList<Integer> aFinalList) {
        if(!visited[i]){
            visited[i]=true;
            aFinalList.add(i);
            for(int j=0;j<baseClusters.size();j++){
               if(edgeTable[i][j]&& !visited[j]){
                   findConnectedComponents(j,aFinalList);
                }
            }
        }
   }
     
}
