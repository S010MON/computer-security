package server.client.networking;

import lombok.Getter;
import lombok.Setter;
import server.client.RequestBody;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Request
{
    private URL baseUrl;
    private HttpURLConnection conn;
    @Setter private String ip;
    @Setter private int port;
    @Setter @Getter private String jwt = "";
    @Setter @Getter private int counter;


    public Request(String baseURL) throws Exception
    {
        baseUrl = new URL(baseURL);
        ip = "\"" + baseUrl.getHost() + "\"";
        port = baseUrl.getPort();

        conn = (HttpURLConnection) this.baseUrl.openConnection();
    }

    public int getRequest() throws Exception
    {
        conn.setRequestMethod("GET");
        conn.connect();

        System.out.println(conn.getResponseMessage());

        //Safety
        conn.disconnect();

        return conn.getResponseCode();
    }

    public int postAuthRequest(int id, String password, int delay, String steps) throws Exception
    {
        URL url = new URL(baseUrl.toString() + "auth");
        System.out.println(url);
        conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");

        //Set the request header content type
        conn.setRequestProperty("Content-Type", "application/json");
        //Set the response header content type
        conn.setRequestProperty("Accept", "application/json");
        // Enable write access to output stream
        conn.setDoOutput(true);

        //Write JSON string to output stream as bytes
        String jsonBodyString = formatAuthRequestBody(id, password, delay, steps);
        OutputStream os = conn.getOutputStream();
        byte[] input = jsonBodyString.getBytes("utf-8");
        os.write(input, 0, input.length);

        //Read jwt if successful
        if(conn.getResponseCode() == 201)
            setJwt(jwtParser(getResponseBody()));

        //Safety
        conn.disconnect();

        return conn.getResponseCode();
    }

    public int postChangeRequest(String change, int id,  int amount, String jwt) throws Exception
    {
        String path = changeRequestPath(change);

        URL url = new URL(baseUrl.toString() + path);
        System.out.println(url);
        conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");

        //Set the request header content type
        conn.setRequestProperty("Content-Type", "application/json");
        //Set the response header content type
        conn.setRequestProperty("Accept", "application/json");
        // Enable write access to output stream
        conn.setDoOutput(true);

        //Write JSON string to output stream as bytes
        String jsonBodyString = RequestBody.formatChangeRequest(id, jwt,  amount);
        OutputStream os = conn.getOutputStream();
        byte[] input = jsonBodyString.getBytes("utf-8");
        os.write(input, 0, input.length);

        if (conn.getResponseCode() == 200)
            setCounter(counterParser(getResponseBody()));

        //Safety
        conn.disconnect();

        return conn.getResponseCode();
    }

    public String formatAuthRequestBody(int id, String password, int delay, String steps)
    {
        return RequestBody.formatAuthRequest(id,
                password,
                delay,
                steps,
                port,
                ip);
    }

    private String getResponseBody() throws Exception
    {
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        String response = "";
        while((line = br.readLine()) != null)
        {
            response = response + line;
        }
        return response;
    }

    private String jwtParser(String responseMessage)
    {
        int jsonIndexStartSeparator = responseMessage.indexOf(":\"") + 1;
        int jsonIndexEndSeparator = responseMessage.indexOf("\"}") + 1;
        String jwtFormatted = responseMessage.substring(jsonIndexStartSeparator, jsonIndexEndSeparator).replace("\"", "");
        return jwtFormatted;
    }

    private int counterParser(String responseMessage)
    {
        int jsonIndexStartSeparator = responseMessage.indexOf(":") + 1;
        int jsonIndexEndSeparator = responseMessage.indexOf("}");
        String counterValue = responseMessage.substring(jsonIndexStartSeparator, jsonIndexEndSeparator);
        return Integer.parseInt(counterValue);
    }

    private String changeRequestPath(String change)
    {
        switch(change)
        {
            case "INCREASE":
                return "increase";
            case "DECREASE":
                return "decrease";
            default:
                return "";
        }
    }
}



