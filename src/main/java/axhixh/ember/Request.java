
package axhixh.ember;

import com.sun.net.httpserver.HttpExchange;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 *
 * @author ashish 
 */
public class Request {
    private final HttpExchange he;
    private final String[] pathTemplate;
    
    Request(HttpExchange he, String path) {
        this.he = he;
        this.pathTemplate = path.split("/");
    }

    public String getPathParam(String param) {
        if (!param.startsWith(":")) {
            return null;
        }
        for (int i = 0; i < pathTemplate.length; i++) {
            if (pathTemplate[i].equals(param)) {
                URI uri = he.getRequestURI();
                String path = uri.getPath();
                String[] subpaths = path.split("/");
                if (subpaths.length > i) {
                    return subpaths[i];
                } else {
                    break;
                }
            }
        }
        
        return null;
    }
    
    public String getQueryParam(String param) {
        URI uri = he.getRequestURI();
        String query = uri.getQuery();
        if (query == null) {
            return null;
        }
        
        return get(param, query);
    }
    
    public String getContent() throws IOException {
        InputStream in = he.getRequestBody();

        byte[] buffer = new byte[4096];
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        int read;
        while ((read = in.read(buffer)) != -1) {
            output.write(buffer, 0, read);
        }
        return output.toString("UTF-8");
    }
    
    private String get(String param, String query) {
            String p = param + '=';
            int index = query.indexOf(p);
            if (index < 0) {
                return null;
            }

            int start = index + p.length();
            int end = query.indexOf('&', start);
            String v = end < 0 ? query.substring(start)
                    : query.substring(start, end);

            return v;
        }
}
