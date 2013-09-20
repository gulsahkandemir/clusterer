/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package clusterer;

import java.util.ArrayList;
import org.json.JSONObject;

/**
 *
 * @author gulsah
 */
public abstract class SearchEngine {
    private SearchEngineType type = null;
    
    public SearchEngine(SearchEngineType type){
        this.type = type;
    }

    public abstract ArrayList<Snippet> search(String queryWord); 
    
//    public ArrayList<Snippet> searchAndParse(String queryWord){
//        JSONObject resultJSON = search(queryWord);
//        return parseResult(resultJSON);
//    }
    
    public SearchEngineType getType() {
        return type;
    }

    public void setType(SearchEngineType type) {
        this.type = type;
    }
    
}
