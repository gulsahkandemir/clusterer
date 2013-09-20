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
    public static SearchEngine createEngine(SearchEngineType type){
        SearchEngine engine = null;
        switch(type){
            case BING:
                engine =  new BingSearchEngine();
                break;
            case GOOGLE:
                engine = new GoogleSearchEngine();
                break;
            default:
                break;
        }
        return engine;
    }
}
