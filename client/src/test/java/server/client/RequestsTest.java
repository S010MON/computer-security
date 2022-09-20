package server.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

//Please note the Fast API must be active for tests to successfully run
public class RequestsTest
{
    // PYCHARM
//    String url = "http://127.0.0.1:8000/";
//    String ip = "127.0.0.1";

    // DOCKER
    String url =  "http://0.0.0.0:80/";
    String ip = "0.0.0.0";

    int port = 8000;

    @Test
    void testGetRequest()
    {
        try
        {
            Request request = new Request(url);
            int response = request.getRequest();
            assertEquals(418, response);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    void testAuthQueryFormat()
    {
        int id = 1;
        String password = "pass";
        int delay = 100;
        String steps = "[\"INCREASE 1\"" + ", " + "\"INCREASE 1\"]";

        try
        {
            Request request = new Request(url);
            request.setIp("\"" + ip + "\"");
            request.setPort(port);
            String generatedQuery = request.formatAuthRequestBody(id, password, delay, steps);

            String desiredQuery = "{\"id\": 1, \"password\": \"pass\", \"server\": {\"ip\": \"" + ip + "\", \"port\": "+ port +"}, " +
                    "\"actions\": {\"delay\": 100, \"steps\": [\"INCREASE 1\", \"INCREASE 1\"]}}";
            assertEquals(desiredQuery, generatedQuery);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    void testAuthPostRequest()
    {
        int id = 1;
        String password = "pass";
        int delay = 100;
        String steps = "[\"INCREASE 1\"" + ", " + "\"INCREASE 1\"]";

        try
        {
            Request request = loginSetup();

            int responseCode = request.postAuthRequest(id, password, delay, steps);
            assertEquals(201, responseCode);
            assertTrue(!request.getJwt().equals(""));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    void testChangePostRequest()
    {
        int id = 2;
        String password = "pass";
        int delay = 100;
        String steps = "[\"INCREASE 1\"" + ", " + "\"DECREASE 1\"]";

        try
        {
            //Login
            Request request = loginSetup();
            int responseCode = request.postAuthRequest(id, password, delay, steps);
            assertEquals(201, responseCode);

            //Increase
            int increaseResponseCode = request.postChangeRequest("INCREASE",1, request.getJwt());
            assertEquals(200, increaseResponseCode);
            assertEquals(1, request.getCounter());

            //Decrease
            int decreaseResponseCode = request.postChangeRequest("DECREASE", 1, request.getJwt());
            assertEquals(200, decreaseResponseCode);
            assertEquals(0, request.getCounter());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public Request loginSetup() throws Exception
    {
        Request request = new Request(url);

        // Ensure IP and Port are set to tests capable of running across all devices
        request.setIp("\"" + ip + "\"");
        request.setPort(port);

        return request;
    }

    @Test
    void jwt_equals()
    {
        assertEquals("5c3885c6f012373c2a262d8c46818652a68a410e4d077b3ee3db36eb467de009d9975073e29cb65f28f5be423600f9cd1917d6852b0a79a825482047a5c094eb",
                "5c3885c6f012373c2a262d8c46818652a68a410e4d077b3ee3db36eb467de009d9975073e29cb65f28f5be423600f9cd1917d6852b0a79a825482047a5c094eb");
    }
}
