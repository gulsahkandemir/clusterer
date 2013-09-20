/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package clusterer;

import java.io.BufferedReader;
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
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author gulsah
 */
public class GoogleSearchEngine extends SearchEngine{

    
    public GoogleSearchEngine(){
        super(SearchEngineType.GOOGLE);
    }
    
    @Override
    public ArrayList<Snippet> search(String queryWord) {
        
        ArrayList<Snippet> snipArray = new ArrayList<Snippet>();
        try {
            
            int i=1;
            String urlString = "https://www.googleapis.com/customsearch/v1?key="+Config.GOOGLE_API_KEY+"&cx="+Config.GOOGLE_CX+"&q="+queryWord+"&start=";
            while(i<=51){
         
              URL url = new URL(urlString+i);
              
       //       System.out.println("DEBUG: Called Url: " + url.toString());
                      
              HttpURLConnection connection = (HttpURLConnection) url.openConnection();
              connection.setRequestMethod("GET");
              connection.setDoOutput(true);
              InputStream content = (InputStream)connection.getInputStream();
              BufferedReader in   = 
                  new BufferedReader (new InputStreamReader (content));
              String line;

              StringBuilder stringBuilder = new StringBuilder();
              while ((line = in.readLine()) != null) {
                  stringBuilder.append(line);
              }
              in.close();

              String result = stringBuilder.toString();
              JSONObject resultJSON = new JSONObject(result);
              JSONArray items = resultJSON.getJSONArray("items");
              for(int index=0;index<items.length();index++){
                String resultSnippet = ((JSONObject)items.get(index)).getString("snippet");
                String resultUrl = ((JSONObject)items.get(index)).getString("link");
                String[] tempSplitted = resultSnippet.toLowerCase().replaceAll("[^A-Za-z ]", "").split(" ");
                ArrayList<String> tempOrigWords = new ArrayList<String>(Arrays.asList(tempSplitted));
                Snippet newSnippet = new Snippet(tempOrigWords, resultUrl, resultSnippet);
                snipArray.add(newSnippet);
              }
              i+=10;
            }

        } catch (MalformedURLException ex) {
            Logger.getLogger(GoogleSearchEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GoogleSearchEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
    
        return snipArray;

    }

    
}
