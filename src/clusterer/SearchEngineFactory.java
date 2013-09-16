/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package clusterer;

/**
 *
 * @author gulsah
 */
public class SearchEngineFactory {
    public static SearchEngine startEngine(SearchEngineType type){
        SearchEngine engine = null;
        switch(type){
            case BING:
                engine =  new Bing();
                break;
            default:
                break;
        }
        return engine;
    }
}
