package server.client;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.json.JsonParseException;
import org.springframework.boot.json.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

//TODO Return correct error status code instead of FAILURE_CODE
public class Request
{
    private URL baseUrl;
    private HttpURLConnection conn;
    @Setter private String ip;
    @Setter private int port;
    @Setter @Getter private String jwt = "";
    private final int FAILURE_CODE = 404;


    public Request(String baseURL)
    {
        try
        {
            baseUrl = new URL(baseURL);
            ip = "\"" + baseUrl.getHost() + "\"";
            port = baseUrl.getPort();

            conn = (HttpURLConnection) this.baseUrl.openConnection();
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

    public int postIncreaseRequest(int id, int amount, String jwt)
    {
        try {
            URL url = new URL(baseUrl.toString() + "increase?id=" + id + "&amount=" + amount + "&jwt=" + jwt);
            System.out.println(url);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            //Set the request header content type
            conn.setRequestProperty("Content-Type", "application/json");
            //Set the response header content type
            conn.setRequestProperty("Accept", "application/json");
            // Enable write access to output stream
            conn.setDoOutput(true);

            if (conn.getResponseCode() == 200)
                //TODO Update counter
                System.out.println(conn.getResponseMessage());

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

    public int postAuthRequest(int id, String password, String jsonBodyString)
    {
        try
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
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return FAILURE_CODE;
    }

    public String formatAuthRequestBody(int id, String password, int delay, String steps)
    {
        String clientInfo = "{\"id\": " + id + ", \"password\": " + "\"" + password + "\", ";
        String serverInfo = "\"server\": {\"ip\": " + ip + ", \"port\": " + port + "}, ";
        String actionsInfo = "\"actions\": {\"delay\": " + delay +
                            ", \"steps\": " + steps + "}}";
        String request =clientInfo + serverInfo + actionsInfo;
        System.out.println(request);
        return request;
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
        System.out.println(response);
        return response;
    }

    private String jwtParser(String responseMessage)
    {
        int jsonIndexStartSeparator = responseMessage.indexOf(":\"") + 1;
        int jsonIndexEndSeparator = responseMessage.indexOf("\"}") + 1;
        String jwtFormatted = responseMessage.substring(jsonIndexStartSeparator, jsonIndexEndSeparator).replace("\"", "");
        return jwtFormatted;
    }
}



