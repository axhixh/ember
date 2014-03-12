

package np.com.axhixh.ember;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;

/**
 *
 * @author ashish
 */
public class Response {
    private final HttpExchange he;
    private int status = HttpURLConnection.HTTP_OK;
    
    Response(HttpExchange he) {
        this.he = he;
    }
    
    public void setStatus(int code) {
        this.status = code;
    }
    
    public void send(String content)  {
        byte[] b = content.getBytes();
        try {
            he.sendResponseHeaders(status, b.length);
            try (OutputStream out = he.getResponseBody()) {
                out.write(b);
            }
        } catch (IOException err) {
            err.printStackTrace(); //do better;
        }
    }
    
    public void stream(Stream stream) {
        try {
            he.sendResponseHeaders(status, 0);
            try (OutputStream out = he.getResponseBody()) {
                stream.write(out);                
            }
        } catch (IOException err) {
            err.printStackTrace(); //do better;
        }
    }
    
    public void error(final Exception err) {
        setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
        stream(new Stream() {

            @Override
            public void write(OutputStream out) throws IOException {
                err.printStackTrace(new PrintStream(out));
            }
            
        });
    }
    
    public static interface Stream {
        public void write(OutputStream out) throws IOException;
    }
}
