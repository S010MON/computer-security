package server.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//Please note the Fast API must be active for tests to successfully run
public class RequestsTest
{
//    String url = "http://127.0.0.1:8000/";
//    String ip = "127.0.0.1";

    String url =  "http://0.0.0.0:8000/";
    String ip = "0.0.0.0";

    int port = 8000;

    @Test
    void testGetRequest()
    {
        Request request = new Request(url);
        int response = request.getRequest();
        assertEquals(418, response);
    }

    @Test
    void testAuthQueryFormat()
    {
        int id = 1;
        String password = "pass";
        int delay = 100;
        String steps = "[\"INCREASE 1\"" + ", " + "\"INCREASE 1\"]";

        Request request = new Request(url);
        request.setIp("\"" + ip + "\"");
        request.setPort(port);
        String generatedQuery = request.formatAuthRequestBody(id, password, delay, steps);

        String desiredQuery = "{\"id\": 1, \"password\": \"pass\", \"server\": {\"ip\": \"" + ip + "\", \"port\": "+ port +"}, " +
                "\"actions\": {\"delay\": 100, \"steps\": [\"INCREASE 1\", \"INCREASE 1\"]}}";
        assertEquals(desiredQuery, generatedQuery);
    }

    @Test
    void testAuthPostRequest()
    {
        int id = 1;
        String password = "pass";
        int delay = 100;
        String steps = "[\"INCREASE 1\"" + ", " + "\"INCREASE 1\"]";

        Request request = loginSetup();

        String generatedBody = request.formatAuthRequestBody(id, password, delay, steps);
        int responseCode = request.postAuthRequest(id, password, generatedBody);
        System.out.println(responseCode);
        assertEquals(201, responseCode);
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

        String generatedBody = request.formatAuthRequestBody(id, password, delay, steps);
        int responseCode = request.postAuthRequest(id, password, generatedBody);
        assertEquals(201, responseCode);

        int increaseResponseCode = request.postIncreaseRequest(id, 1, request.getJwt());
        assertEquals(200, increaseResponseCode);
    }

    public Request loginSetup()
    {
        Request request = new Request(url);

        // Ensure IP and Port are set to tests capable of running across all devices
        request.setIp("\"" + ip + "\"");
        request.setPort(port);

        return request;
    }

    @Test
    void string_equals()
    {
        assertEquals("5c3885c6f012373c2a262d8c46818652a68a410e4d077b3ee3db36eb467de009d9975073e29cb65f28f5be423600f9cd1917d6852b0a79a825482047a5c094eb",
                "5c3885c6f012373c2a262d8c46818652a68a410e4d077b3ee3db36eb467de009d9975073e29cb65f28f5be423600f9cd1917d6852b0a79a825482047a5c094eb");
    }
}
