package np.com.axhixh.ember;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;

public final class Ember {

    public static void get(Route route) {
        addRoute(HttpMethod.GET, route);
    }
    
    public static void delete(Route route) {
        addRoute(HttpMethod.DELETE, route);
    }
    
    public static void post(Route route) {
        addRoute(HttpMethod.POST, route);
    }
    
    public static void put(Route route) {
        addRoute(HttpMethod.PUT, route);
    }

    public static void setPort(int port) {
        Ember.port = port;
    }

    public static int getPort() {
        return port;
    }

    public static void stop() {
        server.stop(0);
    }

    private static synchronized void init() {
        if (initialized) {
            return;
        }

        initialized = true;
        try {
            routeMatcher = new RouteMatcher();
            server = HttpServer.create(new InetSocketAddress(port), BACKLOG);
            server.createContext("/", new HttpHandler() {

                @Override
                public void handle(HttpExchange he) throws IOException {
                    HttpMethod method = getMethod(he.getRequestMethod());
                    if (method == null) {
                        send(he, HttpURLConnection.HTTP_BAD_METHOD, "Unsupported HTTP Method");
                        return;
                    }
                    String path = he.getRequestURI().getPath();
                    Route route = routeMatcher.findTarget(method, path);
                    if (route == null) {
                        send(he, HttpURLConnection.HTTP_NOT_FOUND, "Route not found for " + path);
                        return;
                    }
                    route.handle(new Request(he, route.getPath()), new Response(he));
                }
            });
            server.start();
        } catch (IOException err) {
            throw new RuntimeException("Error initializing Ember Server.", err);
        }

    }

    private static HttpMethod getMethod(String methodName) {
        for (HttpMethod method : HttpMethod.values()) {
            if (method.name().equalsIgnoreCase(methodName)) {
                return method;
            }
        }
        return null;
    }

    private static void send(HttpExchange he, int code, String message) throws IOException {
        byte[] msg = message.getBytes();
        he.sendResponseHeaders(code, msg.length);
        try (OutputStream out = he.getResponseBody()) {
            out.write(msg);
        }
    }

    private static synchronized void addRoute(HttpMethod method, Route route) {
        init();
        routeMatcher.addRoute(method, route);
    }

    private static boolean initialized;
    private static int port = 6789;
    private static HttpServer server;
    private static RouteMatcher routeMatcher;

    private static final int BACKLOG = 5;
}
