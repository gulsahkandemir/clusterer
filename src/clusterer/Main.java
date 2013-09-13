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
import java.io.Reader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
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
            String queryString = "salsa";
            URL url = new URL ("https://api.datamarket.azure.com/Bing/Search/v1/Web?Query=%27"+queryString+"%27&$format=json");
            String auth =  Config.BING_ACCOUNT_KEY + ":" + Config.BING_ACCOUNT_KEY;
            String encoding = Base64.encodeBase64String(auth.getBytes());

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "Basic " + encoding);
            InputStream content = (InputStream)connection.getInputStream();
            BufferedReader in   = 
                new BufferedReader (new InputStreamReader (content));
            String line;
            
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = in.readLine()) != null) {
                stringBuilder.append(line);
            }
            in.close();
            String resultString = stringBuilder.toString();
            
            JSONObject jsonObject = new JSONObject(resultString);
           
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


        } catch (MalformedURLException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private static void parseResult(JSONObject resultObject) throws FileNotFoundException, IOException {
        
        JSONArray resultArray = resultObject.getJSONObject("d").getJSONArray("results");
        
        for(int i=0; i<resultArray.length(); i++){
            tempSnipArray.add(resultArray.getJSONObject(i).get("Description").toString());
        }  

        for (int i = 0; i < tempSnipArray.size(); i++) {
            String[] tempSplitted = tempSnipArray.get(i).toLowerCase().replaceAll("[^A-Za-z ]", "").split(" ");
            ArrayList<String> tempOrigWords = new ArrayList<String>(Arrays.asList(tempSplitted));

            Snippet newSnippet = new Snippet();
            newSnippet.setOrigWords((ArrayList<String>) tempOrigWords.clone());
            snipArray.add(newSnippet);
        }
    }
}
