package server.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//Please note the Fast API must be active for tests to successfully run
public class RequestsTest
{
    @Test
    void testGetRequest()
    {
        Request request = new Request("http://127.0.0.1:8000/");
        int response = request.getRequest();
        assertEquals(response, 418);
    }

    @Test
    void testAuthQueryFormat()
    {
        int delay = 100;
        String steps = "[\"INCREASE 1\"" + ", " + "\"INCREASE 1\"]";

        Request request = new Request("http://127.0.0.1:8000/");
        request.setIp("\"" + "127.0.0.1" + "\"");
        request.setPort(-1);
        String generatedQuery = request.formatAuthRequestBody(delay, steps);

        String desiredQuery = "{\"server\": {\"ip\": \"127.0.0.1\", \"port\": -1}, " +
                "\"actions\": {\"delay\": 100, \"steps\": [\"INCREASE 1\", \"INCREASE 1\"]}}";
        assertEquals(generatedQuery, desiredQuery);
    }

    @Test
    void testAuthPostRequest()
    {
        int id = 1;
        String password = "pass";
        int delay = 100;
        String steps = "[\"INCREASE 1\"" + ", " + "\"INCREASE 1\"]";

        Request request = loginSetup();

        String generatedBody = request.formatAuthRequestBody(delay, steps);
        int responseCode = request.postAuthRequest(id, password, generatedBody);
        System.out.println(responseCode);
        assertEquals(responseCode, 201);
        assertTrue(!request.getJwt().equals(""));
    }

    @Test
    void testIncreasePostRequest()
    {
        //Login to the server
        int id = 1;
        String password = "pass";
        int delay = 100;
        String steps = "[\"INCREASE 1\"" + ", " + "\"INCREASE 1\"]";
        Request request = loginSetup();

        String generatedBody = request.formatAuthRequestBody(delay, steps);
        int responseCode = request.postAuthRequest(id, password, generatedBody);
        assertEquals(responseCode, 201);

        int increaseResponseCode = request.postIncreaseRequest(id, 1, request.getJwt());
        assertEquals(increaseResponseCode, 200);
    }

    public Request loginSetup()
    {


        Request request = new Request("http://127.0.0.1:8000/");

        // Ensure IP and Port are set to tests capable of running across all devices
        request.setIp("\"" + "127.0.0.1" + "\"");
        request.setPort(8000);

        return request;
    }
}
