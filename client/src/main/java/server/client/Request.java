package server.client;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;

public class Request
{
    private URL url;
    private String ip;
    private HttpURLConnection conn;
    private final int CONNECTION_FAILURE = 404;

    public Request(String url)
    {
        try
        {
            this.url = new URL(url);
            this.conn = (HttpURLConnection) this.url.openConnection();
            this.ip = this.url.getHost();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public int getRequest()
    {
        try
        {
            conn.setRequestMethod("GET");
            conn.connect();
            System.out.println(conn.getResponseMessage());
            return conn.getResponseCode();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return CONNECTION_FAILURE;
    }

    public int postRequest(String jsonString)
    {
        try
        {
            conn.setRequestMethod("POST");

            //Set the request header content type
            conn.setRequestProperty("Content-Type", "application/json");
            //Set the response header content type
            conn.setRequestProperty("Accept", "application/json");
            // Enable write access to output stream
            conn.setDoOutput(true);

            //Write JSON string to output stream as bytes
            OutputStream os = conn.getOutputStream();
            byte[] input = jsonString.getBytes("utf-8");
            os.write(input, 0, input.length);

            //Read response
            String response = conn.getResponseMessage();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    public String formatAuthRequestJSON(String id, String password, int delay, String steps)
    {
        String userInfo = "{id: " + id + ", password: " + password + ", ";
        String serverInfo = "server: {ip: " + ip.toString() + ", port: " + url.getPort() + "}, ";
        String actionsInfo = "actions: {delay: " + Integer.toString(delay) +
                            ", steps: " + steps + "}}";
        String request = userInfo + serverInfo + actionsInfo;
        System.out.println(request);
        return request;
    }
}



