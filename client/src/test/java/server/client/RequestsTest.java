package server.client;

import org.junit.jupiter.api.Test;

import server.client.gui.Direction;
import server.client.networking.Session;

import static org.junit.jupiter.api.Assertions.assertEquals;

//Please note the Fast API must be active for tests to successfully run

//TODO IP Check
public class RequestsTest
{
    // PYCHARM
//    String url = "http://127.0.0.1:8000/";
    String ip = "127.0.0.1";

    // Docker
//    String url =  "http://0.0.0.0:8000/";
//    String ip = "0.0.0.0";

    // Deployed
    String url = "https://cs-server-1.herokuapp.com/";

    int port = 8000;

    @Test
    void testGetRequest()
    {
        try
        {
            Session request = new Session(url);
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
            Session request = new Session(url);
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
            Session request = loginSetup();

            int responseCode = request.postAuthRequest(id, password, delay, steps);
            assertEquals(201, responseCode);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    void post_decrease()
    {
        int id = 2;
        String password = "pass";
        int delay = 100;
        String steps = "[\"DECREASE 1\"" + ", " + "\"DECREASE 1\"]";

        try
        {
            //Login
            Session request = loginSetup();
            int responseCode = request.postAuthRequest(id, password, delay, steps);
            assertEquals(201, responseCode);

            //Increase
            int increaseResponseCode = request.postChangeRequest(Direction.DECREASE, id,1);
            assertEquals(200, increaseResponseCode);
            assertEquals(-1, request.getCounter());

            //Decrease
            int decreaseResponseCode = request.postChangeRequest(Direction.DECREASE, id, 1);
            assertEquals(200, decreaseResponseCode);
            assertEquals(-2, request.getCounter());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    @Test
    void post_increase()
    {
        int id = 3;
        String password = "pass";
        int delay = 100;
        String steps = "[\"INCREASE 1\"" + ", " + "\"INCREASE 1\"]";

        try
        {
            //Login
            Session request = loginSetup();
            int responseCode = request.postAuthRequest(id, password, delay, steps);
            assertEquals(201, responseCode);

            //Increase
            int increaseResponseCode1 = request.postChangeRequest(Direction.INCREASE, id,1);
            assertEquals(200, increaseResponseCode1);
            assertEquals(1, request.getCounter());

            //Increase
            int increaseResponseCode2 = request.postChangeRequest(Direction.INCREASE, id, 1);
            assertEquals(200, increaseResponseCode2);
            assertEquals(2, request.getCounter());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }



    public Session loginSetup() throws Exception
    {
        Session request = new Session(url);

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
