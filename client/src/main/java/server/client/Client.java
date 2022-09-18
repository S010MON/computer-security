package server.client;

import javafx.scene.layout.BorderPane;

public class Client extends BorderPane
{
    public ActionsPane actionsPane;
    public ControlPane controlPane;

    public Client()
    {
        actionsPane = new ActionsPane();
        this.setRight(actionsPane);

        controlPane = new ControlPane(actionsPane);
        this.setLeft(controlPane);
    }

    public int authorize(String username, String passcode)
    {
        return 0;
    }


    public void executeActions()
    {
    }

    public int logout()
    {
        return 0;
    }
}
