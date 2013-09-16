/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package clusterer;

/**
 *
 * @author gulsah
 */
public abstract class SearchEngine {
    private SearchEngineType type = null;
    
    public SearchEngine(SearchEngineType type){
        this.type = type;
    }

    abstract String search(String queryWord); 
    
    public SearchEngineType getType() {
        return type;
    }

    public void setType(SearchEngineType type) {
        this.type = type;
    }
    
}
