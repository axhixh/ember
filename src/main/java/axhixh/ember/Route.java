

package axhixh.ember;

/**
 *
 * @author ashish
 */
public abstract class Route {
    
    private final String path;
    
    public Route(String path) {
        this.path = path;
    }
    
    public String getPath() {
        return path;
    }
    
    public abstract void handle(Request request, Response response);

}
