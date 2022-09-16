package server.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;

public class Client implements ClientFrame
{
    private Queue<Action> actionQueue = new LinkedList<>();

    public int authorize(String username, String passcode)
    {
        int authorizationResponse;
        try
        {
            URL url = new URL("http://127.0.0.1/auth");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");

            //Set the request header content type
            conn.setRequestProperty("Content-Type", "application/json");
            //Set the response header content type
            conn.setRequestProperty("Accept", "application/json");
            // Enable write access to output stream
            conn.setDoOutput(true);

            //Create the request body
            String jsonInputString = "{username:" + username + ", password: "  + passcode + "}";
            try(OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);

                try(BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine = null;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    System.out.println(response.toString());
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    public void addAction(Action action)
    {
        actionQueue.add(action);
    }

    public void executeActions()
    {
        actionQueue.remove().actionRequest();
    }

    public int logout()
    {
        return 0;
    }
}
