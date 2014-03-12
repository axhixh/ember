package np.com.axhixh.ember;

import np.com.axhixh.ember.Request;
import np.com.axhixh.ember.Response;
import np.com.axhixh.ember.Route;
import static np.com.axhixh.ember.Ember.delete;
import static np.com.axhixh.ember.Ember.get;
import static np.com.axhixh.ember.Ember.post;
import static np.com.axhixh.ember.Ember.put;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author ashish
 */
public class TestServer {

    public static void main(String[] args) {
        get(new Route("/hello") {
            @Override
            public void handle(Request request, Response response) {
                response.send("Hello World!");
            }
        });
        
        get(new Route("/messages") {
            @Override
            public void handle(Request request, Response response) {
                String to = request.getQueryParam("to");
                String from = request.getQueryParam("from");
                response.send(String.format("Wuff! Wuff! %s! - %s", to, from));
            }
        });
        
        get(new Route("/dogs/:name/owner") {

            @Override
            public void handle(Request request, Response response) {
                String name = request.getPathParam(":name");
                switch (name) {
                    case "Snowy":
                        response.send("Tintin");
                        break;
                    case "Dogmatix":
                        response.send("Obelix");
                        break;
                    default:
                        response.send("Don't know");
                        break;
                }
            }
            
        });
        
        get(new Route("/stream") {

            @Override
            public void handle(Request request, Response response) {
                response.stream(new Response.Stream() {

                    @Override
                    public void write(OutputStream out) throws IOException {
                        out.write("Hello Streaming World!".getBytes());
                    }
                });
            }
            
        });
        
        delete(new Route("/cats/:name") {

            @Override
            public void handle(Request request, Response response) {
                String name = request.getPathParam(":name");
                response.send(String.format("%s is dead", name));
            }
            
        });
        
        post(new Route("/test/count") {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String content = request.getContent();
                    String[] parts = content.split(",");
                    response.send(String.valueOf(parts.length));
                } catch (IOException err) {
                    response.error(err);
                }
            }
            
        });
        
        put(new Route("/test/add") {

            @Override
            public void handle(Request request, Response response) {
                try {
                    String content = request.getContent();
                    String[] parts = content.split(",");
                    int sum = 0;
                    for (String part : parts) {
                        sum += Integer.parseInt(part.trim());                        
                    }
                    response.send(String.valueOf(sum));
                } catch (IOException err) {
                    response.error(err);
                }
            }
            
        });
    }
}
