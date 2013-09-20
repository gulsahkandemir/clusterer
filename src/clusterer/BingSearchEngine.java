/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package clusterer;

import static clusterer.Main.snipArray;
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
import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author gulsah
 */
public class BingSearchEngine extends SearchEngine {

    public BingSearchEngine(){
        super(SearchEngineType.BING);
    }
    
    @Override
    public ArrayList<Snippet> search(String queryWord) {
        String result = null;
        try {
            URL url;
            url = new URL ("https://api.datamarket.azure.com/Bing/Search/v1/Web?Query=%27"+queryWord+"%27&$format=json");
            String auth =  Config.BING_ACCOUNT_KEY + ":" + Config.BING_ACCOUNT_KEY;
            String encodedAuth = Base64.encodeBase64String(auth.getBytes());

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "Basic " + encodedAuth);
            InputStream content = (InputStream)connection.getInputStream();
            BufferedReader in   = 
                new BufferedReader (new InputStreamReader (content));
            String line;
            
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = in.readLine()) != null) {
                stringBuilder.append(line);
            }
            in.close();
            result = stringBuilder.toString();
            
        
        } catch (MalformedURLException ex) {
            Logger.getLogger(BingSearchEngine.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BingSearchEngine.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        JSONObject jsonObject = new JSONObject(result);
           
        return parseResult(jsonObject);
            
    }


    protected ArrayList<Snippet> parseResult(JSONObject resultJSON) {
        
        JSONArray resultArray = resultJSON.getJSONObject("d").getJSONArray("results");
        ArrayList<Snippet> snipArray = new ArrayList<Snippet>();
        for(int i=0; i<resultArray.length(); i++){
            String description = resultArray.getJSONObject(i).get("Description").toString();
            String url = resultArray.getJSONObject(i).get("Url").toString();
            String[] tempSplitted = description.toLowerCase().replaceAll("[^A-Za-z ]", "").split(" ");
            ArrayList<String> tempOrigWords = new ArrayList<String>(Arrays.asList(tempSplitted));
            Snippet newSnippet = new Snippet(tempOrigWords, url, description);
            snipArray.add(newSnippet);

        }  
        return snipArray;
    
    }
    
    
    
}
