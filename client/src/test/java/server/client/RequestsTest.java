package server.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

//Please note the Fast API must be active for tests to successfully run
public class RequestsTest
{
    @Test
    void testGetRequest()
    {
        Request request = new Request("http://127.0.0.1/");
        int response = request.getRequest();
        assertEquals(response, 418);
    }

    @Test
    void testAuthQueryFormat()
    {
        String id = "John";
        String password = "Smith123";
        int delay = 100;
        String steps = "[INCREASE 1" + ", " + "INCREASE 1]";

        Request request = new Request("http://127.0.0.1/auth");
        String generatedQuery = request.formatAuthRequestJSON(id, password, delay, steps);

        String desiredQuery = "{id: John, password: Smith123, server: {ip: 127.0.0.1, port: -1}, " +
                "actions: {delay: 100, steps: [INCREASE 1, INCREASE 1]}}";
        assertEquals(generatedQuery, desiredQuery);
    }

    @Test
    void testAuthPostRequest()
    {
        String id = "John";
        String password = "Smith123";
        int delay = 100;
        String steps = "[Increase1" + ", " + "Increase1]";

        Request request = new Request("http://127.0.0.1/auth");
        String generatedQuery = request.formatAuthRequestJSON(id, password, delay, steps);
        int responseCode = request.postRequest(generatedQuery);
        System.out.println(responseCode);
        assertEquals(responseCode, 201);
    }
}
