/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package clusterer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import org.tartarus.snowball.ext.englishStemmer;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;



/**
 *
 * @author Gulsah Kandemir
 */
public class Main {

    //git test
    static boolean isSnippet = false;
    static ArrayList<String> tempSnipArray = new ArrayList<String>();
    public static ArrayList<Snippet> snipArray = new ArrayList<Snippet>();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, BadLocationException {
        try {
            //Get the desired SearchEngine instance from the args[0] parameter.
            SearchEngine searchEngine =  SearchEngineFactory.startEngine(SearchEngineType.valueOf(args[0]));
            
            //Get the search result of thw query
            String searchResult = searchEngine.search(args[1]);
            
            JSONObject jsonObject = new JSONObject(searchResult);
           
            parseResult(jsonObject);

            /*
             * Start stemming
             *
             */
            englishStemmer stemmer = new englishStemmer();
            for (int i = 0; i < snipArray.size(); i++) {
                ArrayList<String> tempStemmedWords = new ArrayList<String>();
                for (int j = 0; j < snipArray.get(i).getOrigWords().size(); j++) {
                    // Eliminate empty words
                    if (snipArray.get(i).getOrigWords().get(j).equals("")) {
                        snipArray.get(i).getOrigWords().remove(j--);
                    } else {
                        stemmer.setCurrent(snipArray.get(i).getOrigWords().get(j));
                        stemmer.stem();
                        tempStemmedWords.add(stemmer.getCurrent());
                    }
                }
                snipArray.get(i).setStemmedWords(tempStemmedWords);

            }
            
            // Clear stopwords
            for (int i = 0; i < snipArray.size(); i++) {
                for (int j = 0; j < snipArray.get(i).getStemmedWords().size(); j++) {
                    for (int k = 0; k < StopWord.stopWords.length; k++) {
                        if (snipArray.get(i).getStemmedWords().get(j).trim().equalsIgnoreCase(StopWord.stopWords[k])) {
                            snipArray.get(i).getStemmedWords().remove(j);
                            snipArray.get(i).getOrigWords().remove(j);
                            j--;
                            break;
                        }
                    }
                }
            }
            /** 
             * DEBUG : Print the list of stemmed words
             */
//            for (int i = 0; i < snipArray.size(); i++) {
//                System.out.println(i + "   " + snipArray.get(i).getStemmedWords());
//            }

            /**
             * Build up suffix tree
             */
            WordSuffixTree wordSuffixTree = new WordSuffixTree(snipArray);
            TrieNode suffixRoot = wordSuffixTree.constructSuffixTree();
            
            /*
             * Identify base clusters
             */
            Clusterer clusterer = new Clusterer(suffixRoot);
            clusterer.identifyBaseClusters();
            
            /*
             * Connect base clusters
             */
            clusterer.connectBaseClusters();
            
            /*
             * Sort the final clusters, biggest sets on front
             */
            clusterer.sortFinalClusters();
            
            
//        
        // Print final list
        System.out.println("SORTED CLUSTERS--------------------------");
        for(int i=0;i<clusterer.getFinalList().size();i++){
            System.out.println((clusterer.getFinalList().get(i).toString()));
        }
        System.out.println("----------------------------------------");

            
        /*
         * Print out the urls and descriptions of search result snippets
         * in a clustered groups
         */
        System.out.println("CLUSTERS AND CONTENTS-------------------------");
        for(int i=0;i<clusterer.getFinalList().size();i++ ){
           System.out.print(i + " the cluster keywords: ");
           for(Integer clusterId : clusterer.getFinalList().get(i)){
               System.out.print(clusterer.getBaseClusters().get(clusterId).getName() + ", ");
            }

           for(Integer clusterId : clusterer.getFinalList().get(i)){
               for(Integer resultId : clusterer.getBaseClusters().get(clusterId).getDocList()){
                System.out.println("\t Url: " + snipArray.get(resultId).getUrl());
                System.out.println("\t Description: " + snipArray.get(resultId).getDescription());

               }
            }

        }


        } catch (MalformedURLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void parseResult(JSONObject resultObject) throws FileNotFoundException, IOException {
        
        JSONArray resultArray = resultObject.getJSONObject("d").getJSONArray("results");
        
        for(int i=0; i<resultArray.length(); i++){
            String description = resultArray.getJSONObject(i).get("Description").toString();
            String url = resultArray.getJSONObject(i).get("Url").toString();
            String[] tempSplitted = description.toLowerCase().replaceAll("[^A-Za-z ]", "").split(" ");
            ArrayList<String> tempOrigWords = new ArrayList<String>(Arrays.asList(tempSplitted));
            Snippet newSnippet = new Snippet(tempOrigWords, url, description);
            snipArray.add(newSnippet);

        }  
    }
}
