package server.client;

import java.util.LinkedList;
import java.util.Queue;

public class Client implements ClientFrame
{
    private Queue<Action> actionQueue = new LinkedList<>();

    public int authorize(String username, String passcode)
    {
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
