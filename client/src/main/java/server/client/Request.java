package server.client;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Request
{
    private URL url;
    private HttpURLConnection conn;
    private final int FAILURE_CODE = 404;

    public Request(String baseURL)
    {
        try
        {
            this.url = new URL(baseURL);

            conn = (HttpURLConnection) this.url.openConnection();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }


    public int getRequest() {
        try
        {
            conn.setRequestMethod("GET");
            conn.connect();

            System.out.println(conn.getResponseMessage());

            //Safety
            conn.disconnect();

            return conn.getResponseCode();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return FAILURE_CODE;
    }

    public int postRequest(int id, String password, String jsonBodyString)
    {
        try
        {
            this.url = new URL(this.url.toString() + "auth?id=" + id + "&password=" + password);
            HttpURLConnection conn = (HttpURLConnection) this.url.openConnection();
            conn.setRequestMethod("POST");

            //Set the request header content type
            conn.setRequestProperty("Content-Type", "application/json");
            //Set the response header content type
            conn.setRequestProperty("Accept", "application/json");
            // Enable write access to output stream
            conn.setDoOutput(true);

            //Write JSON string to output stream as bytes
            OutputStream os = conn.getOutputStream();
            byte[] input = jsonBodyString.getBytes("utf-8");
            os.write(input, 0, input.length);

            //Read response
            String response = conn.getResponseMessage();
            System.out.println(response);

            //Safety
            conn.disconnect();

            return conn.getResponseCode();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return FAILURE_CODE;
    }

    public String formatAuthRequestBody(int delay, String steps)
    {
        String serverInfo = "{\"server\": {\"ip\": " + "\"" + this.url.getHost() + "\"" + ", \"port\": " + url.getPort() + "}, ";
        String actionsInfo = "\"actions\": {\"delay\": " + delay +
                            ", \"steps\": " + steps + "}}";
        String request =serverInfo + actionsInfo;
        System.out.println(request);
        return request;
    }
}



