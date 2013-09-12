/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cluster;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
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
import utility.Clusterer;
import utility.Snippet;
import utility.StopWord;
import utility.TrieNode;
import utility.WordSuffixTree;

/**
 *
 * @author Gulsah Kandemir
 */
public class Main {

    static boolean isSnippet = false;
    static ArrayList<String> tempSnipArray = new ArrayList<String>();
    public static ArrayList<Snippet> snipArray = new ArrayList<Snippet>();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, BadLocationException {
        try {
            String link = "https://www.google.com/search?hl=en&ie=UTF-8&q="+args[0]+"&num=200";
            URL url = new URL(link);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (X11; U; Linux x86_64; en-GB; rv:1.8.1.6) Gecko/20070723 Iceweasel/2.0.0.6 (Debian-2.0.0.6-0etch1)");
            
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String inputLine;
            StringBuilder strBuild = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                strBuild.append(inputLine);
            }
            in.close();
            String resultString = strBuild.toString();
            parseResult(resultString);

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

            for (int i = 0; i < snipArray.size(); i++) {
                System.out.println(i + "   " + snipArray.get(i).getStemmedWords());
            }

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

    private static void parseResult(String resultString) throws FileNotFoundException, IOException {
        // Get the factory
        HTMLEditorKit.ParserCallback callback =
                new HTMLEditorKit.ParserCallback() {

                    String tempSnip = "";

                    @Override
                    public void handleText(char[] data, int pos) {
                        if (isSnippet == true) {
                            tempSnip = tempSnip + new String(data);
                        }
                    }

                    @Override
                    public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos) {
                        if (t.toString().equals("div") && a.getAttribute(HTML.Attribute.CLASS) != null && (a.getAttribute(HTML.Attribute.CLASS).toString().equals("s") || a.getAttribute(HTML.Attribute.CLASS).toString().equals("s hc"))) {
                            isSnippet = true;
                        }
                        if (t == HTML.Tag.CITE) {
                            isSnippet = false;
                            tempSnipArray.add(tempSnip);
                            tempSnip = "";
                        }
                    }
                };
        Reader reader = new StringReader(resultString);
        new ParserDelegator().parse(reader, callback, true);

        for (int i = 0; i < tempSnipArray.size(); i++) {
            String[] tempSplitted = tempSnipArray.get(i).toLowerCase().replaceAll("[^A-Za-z ]", "").split(" ");
            ArrayList<String> tempOrigWords = new ArrayList<String>(Arrays.asList(tempSplitted));

            Snippet newSnippet = new Snippet();
            newSnippet.setOrigWords((ArrayList<String>) tempOrigWords.clone());
            snipArray.add(newSnippet);
        }
    }
}
