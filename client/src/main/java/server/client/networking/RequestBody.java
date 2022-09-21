package server.client.networking;

public class RequestBody
{
    public static String formatAuthRequest(int id, String password, int delay, String steps, int port, String ip)
    {
        String clientInfo = "{\"id\": " + id + ", \"password\": " + "\"" + password + "\", ";
        String serverInfo = "\"server\": {\"ip\": " + ip + ", \"port\": " + port + "}, ";
        String actionsInfo = "\"actions\": {\"delay\": " + delay +
                            ", \"steps\": " + steps + "}}";
        return clientInfo + serverInfo + actionsInfo;
    }

    public static String formatChangeRequest(int id, String jwt, int amount)
    {
        String idInfo = "{\"id\": " + id + ", ";
        String jwtInfo = "\"jwt\": \"" + jwt + "\", ";
        String amountInfo = "\"amount\": " + amount + "}";
        return idInfo + jwtInfo + amountInfo;
    }
}
