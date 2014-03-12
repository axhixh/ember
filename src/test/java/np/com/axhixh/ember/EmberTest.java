package np.com.axhixh.ember;

import np.com.axhixh.ember.Ember;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class EmberTest {

    @BeforeClass
    public static void startServer() {
        TestServer.main(new String[]{});
    }

    @AfterClass
    public static void stopServer() {
        Ember.stop();
    }

    @Test
    public void testSimpleGet() throws IOException {
        String result = get("http://localhost:6789/hello");
        Assert.assertEquals("Hello World!", result);
    }
    
    @Test
    public void test404() {
        try {
            get("http://localhost:6789/nonthere");
            Assert.fail("Did not throw 404 exception");
        } catch (IOException err) {
            Assert.assertEquals("HTTP error code : 404", err.getMessage());
        }
    }
    
    @Test
    public void testQueryParam() throws IOException {
        String result = get("http://localhost:6789/messages?to=Obelix&from=Dogmatix");
        Assert.assertEquals("Wuff! Wuff! Obelix! - Dogmatix", result);
        
        String result2 = get("http://localhost:6789/messages?to=Tintin&from=Snowy");
        Assert.assertEquals("Wuff! Wuff! Tintin! - Snowy", result2);
    }
    
    @Test
    public void testPathParam() throws IOException {
        String tintin = get("http://localhost:6789/dogs/Snowy/owner");
        Assert.assertEquals("Tintin", tintin);
        
        String obelix = get("http://localhost:6789/dogs/Dogmatix/owner");
        Assert.assertEquals("Obelix", obelix);
    }
    
    @Test
    public void testDelete() throws IOException {
        String garfield = delete("http://localhost:6789/cats/Garfield");
        Assert.assertEquals("Garfield is dead", garfield);
        
        String catbert = delete("http://localhost:6789/cats/Catbert");
        Assert.assertEquals("Catbert is dead", catbert);
    }
    
    @Test
    public void testPut() throws IOException {
        String result = put("http://localhost:6789/test/add", new Content("text/plain", "12, 34"));
        Assert.assertEquals("46", result);
    }
    
    @Test
    public void testPost() throws IOException {
        String result = post("http://localhost:6789/test/count", new Content("text/plain", "apple, ball, cat"));
        Assert.assertEquals("3", result);
    }
    
    @Test
    public void testStreamBody() throws IOException {
        String result = get("http://localhost:6789/stream");
        Assert.assertEquals("Hello Streaming World!", result);
    }

    private String get(String url) throws IOException {
        return http("GET", url, null);        
    }

    private String delete(String url) throws IOException {
        return http("DELETE", url, null);        
    }
    
    private String post(String url, Content content) throws IOException {
        return http("POST", url, content);
    }
    
    private String put(String url, Content content) throws IOException {
        return http("PUT", url, content);
    }
    
    private String http(String method, String url, Content content) throws IOException {
        if (("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method)) && content == null) {
            throw new IllegalArgumentException("POST and PUT need content");
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            try {
                conn.setRequestMethod(method);
                conn.setRequestProperty("Accept", "*/*");

                if (content != null) {
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-Type", content.getType());
                    OutputStream os = conn.getOutputStream();
                    os.write(content.getBody().getBytes());
                    os.flush();
                }
                
                if (!isOK(conn.getResponseCode())) {
                    throw new IOException("HTTP error code : " + conn.getResponseCode());
                }

                InputStream in = conn.getInputStream();
                byte[] buffer = new byte[4096];
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                int read;
                while ((read = in.read(buffer)) != -1) {
                    output.write(buffer, 0, read);
                }
                return output.toString("UTF-8");
            } finally {
                conn.disconnect();
            }
        } catch (MalformedURLException e) {

            throw new IOException("Invalid URL", e);

        }
    }
    
    private static boolean isOK(int status) {
        return status >= 200 && status < 300;
    }
    
    static class Content {
        private final String type;
        private final String body;

        public Content(String type, String body) {
            this.type = type;
            this.body = body;
        }
        
        String getType() {
            return type;
        }
        
        String getBody() {
            return body;
        }
    }
}
