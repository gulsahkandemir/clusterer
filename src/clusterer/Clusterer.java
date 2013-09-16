package clusterer;

import clusterer.Cluster;
import libs.Combinator;
import java.util.ArrayList;

/**
 *
 * @author Gulsah Kandemir
 */
public class Clusterer {
    
    private TrieNode suffixRoot;
    private ArrayList<Cluster> baseClusters;
    private boolean[][] edgeTable;
    private boolean[] visited;
    private ArrayList<ArrayList<Integer>> finalList;

    public Clusterer(TrieNode suffixRoot) {
        this.suffixRoot = suffixRoot;
        baseClusters = new ArrayList<Cluster>();
    }

    public TrieNode getSuffixRoot() {
        return suffixRoot;
    }

    public void setSuffixRoot(TrieNode suffixRoot) {
        this.suffixRoot = suffixRoot;
    }

    public ArrayList<Cluster> getBaseClusters() {
        return baseClusters;
    }

    public void setBaseClusters(ArrayList<Cluster> baseClusters) {
        this.baseClusters = baseClusters;
    }

    public boolean[][] getEdgeTable() {
        return edgeTable;
    }

    public void setEdgeTable(boolean[][] edgeTable) {
        this.edgeTable = edgeTable;
    }

    public boolean[] getVisited() {
        return visited;
    }

    public void setVisited(boolean[] visited) {
        this.visited = visited;
    }

    public ArrayList<ArrayList<Integer>> getFinalList() {
        return finalList;
    }

    public void setFinalList(ArrayList<ArrayList<Integer>> finalList) {
        this.finalList = finalList;
    }

    public void identifyBaseClusters() {
        traverseTree(suffixRoot,"","");
        if(!baseClusters.isEmpty()) {
            baseClusters.remove(0);
        }
        for(int a=0;a<baseClusters.size();a++){
            for(int st=0;st<StopWord.stopWords.length;st++){
                if(baseClusters.get(a).getName().trim().equalsIgnoreCase(StopWord.stopWords[st])){
                    //System.out.print(baseClusters.get(a).name);
                    baseClusters.remove(a--);
                }
            }
        }
        /*
         * 
         */
        System.out.println("BASE CLUSTERS----------------------------");
        for(int k=0;k<baseClusters.size();k++){
            System.out.println(k+" "+baseClusters.get(k).getName()+"\t" +baseClusters.get(k).getDocList().toString());
        }
        System.out.println("-----------------------------------------");
    }

    public void traverseTree(TrieNode node, String parentName,String edgeName){

        if(node.getDocList().size() >=2){
            String name = parentName+ edgeName;
            
            //if name is empty, just skip the clustering with that name
            if(!name.equals("")){
                Cluster newCluster = new Cluster(name,
                                        (ArrayList<Integer>) node.getDocList().clone());
                baseClusters.add(newCluster);
            }
            for(int ch = 0;ch<node.getChildren().size();ch++){
                String edge = "";
                //int ed=0;
                for(int ed=0;ed<node.getEdges().get(ch).size();ed++)
                    edge += node.getEdges().get(ch).get(ed)+" ";

                traverseTree(node.getChildren().get(ch),name,edge);
            }
        }
    }

    public boolean isSimilar(Cluster a, Cluster b){
        double numIntersectionsAB = 0;
        for(int i=0;i<a.getDocList().size();i++){
            for(int j=0;j<b.getDocList().size();j++){
                if(a.getDocList().get(i) == b.getDocList().get(j))
                    numIntersectionsAB++;
            }
        }
        if(numIntersectionsAB > (a.getDocList().size()/2.0) && 
                numIntersectionsAB > b.getDocList().size()/2.0){
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
        
//       
//        
//        // Print final list
//        System.out.println("SORTED CLUSTERS--------------------------");
//        for(int i=0;i<finalList.size();i++){
//            System.out.println((finalList.get(i).toString()));
//        }
//        System.out.println("----------------------------------------");

    }
    
    public void sortFinalClusters(){
        for(int i=0;i<finalList.size();i++){
            
            int numOfDocsInIthFinalCluster = 0;
            for(Integer clusterId : finalList.get(i)){
                numOfDocsInIthFinalCluster += baseClusters.get(clusterId).getDocList().size();
            }
            
            int currentMaxFinalClusterIndex = i;
            int currentMaxNumOfDocs = numOfDocsInIthFinalCluster;
            
            for(int j=i+1;j<finalList.size();j++){
                int numOfDocsInJthFinalCluster = 0;
                for(Integer clusterId : finalList.get(j)){
                    numOfDocsInJthFinalCluster += baseClusters.get(clusterId).getDocList().size();
                }
                
                if(numOfDocsInJthFinalCluster > currentMaxNumOfDocs){
                    currentMaxFinalClusterIndex = j;
                    currentMaxNumOfDocs = numOfDocsInJthFinalCluster;
                }
            }
            
            if(i != currentMaxFinalClusterIndex){
                ArrayList<Integer> tmpFinalCluster = finalList.get(i);
                finalList.set(i, finalList.get(currentMaxFinalClusterIndex));
                finalList.set(currentMaxFinalClusterIndex, tmpFinalCluster);
            }
            
        }
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
