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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author gulsah
 */
public class Bing extends SearchEngine {

    public Bing(){
        super(SearchEngineType.BING);
    }
    
    @Override
    String search(String queryWord) {
        String result = null;
        try {
            URL url;
            url = new URL ("https://api.datamarket.azure.com/Bing/Search/v1/Web?Query=%27"+queryWord+"%27&$format=json");
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
            result = stringBuilder.toString();
            
        
        } catch (MalformedURLException ex) {
            Logger.getLogger(Bing.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Bing.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
            
    }
    
}
