package server.client;

public interface ClientFrame
{
    public int authorize(String username, String passcode);
    public void addAction(Action action);
    public int logout();
}
