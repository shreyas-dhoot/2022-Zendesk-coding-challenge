import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class HTTPConnections {

    private final String username;
    private final String password;

    public HTTPConnections(String username, String password) {
        this.username = username;
        this.password = password;
    }


    public static void main(String[] args) {
        try {
            //fetchCountOfTickets();
            //fetchTickets();
            //fetchPerTicket(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public JSONObject httpGet(final String url) throws Exception {
        CredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials(username, password)
        );
        CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Accept", "application/json");
        httpGet.setHeader("Content-type", "application/json");

        HttpResponse httpResponse = httpClient.execute(httpGet);
        HttpEntity responseEntity = httpResponse.getEntity();
        if(httpResponse.getStatusLine().getStatusCode() != 200) {
            String message = "API failed. Status Code : " +
                    httpResponse.getStatusLine().getStatusCode() +
                    " Reason : " + httpResponse.getStatusLine().getReasonPhrase();
            throw new APIFailedException(message);
        }
        String jsonString = EntityUtils.toString(responseEntity);
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(jsonString);


    }
}
