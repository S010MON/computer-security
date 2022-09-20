package server.client.networking;

import javafx.scene.layout.BorderPane;
import org.springframework.boot.SpringApplication;
import server.client.gui.ControlPane;
import server.client.gui.ActionsPane;

public class Client extends BorderPane
{
    public ActionsPane actionsPane;
    public ControlPane controlPane;

    public Client()
    {
        String[] args = {};
        SpringApplication.run(ClientApplication.class, args);

        actionsPane = new ActionsPane();
        this.setRight(actionsPane);

        controlPane = new ControlPane(actionsPane, this);
        this.setLeft(controlPane);
    }

    public void execute(int id, String password, ActionsPane actions)
    {
        try
        {
            Request authRequest = new Request("http://127.0.0.1/");
            String steps = actions.getSteps();

            int authStatus = authRequest.postAuthRequest(id, password, 100, steps);
            System.out.println(authStatus);

        } catch(Exception e) {
            System.out.println("Somebody done gone fucked up");
        }

    }

}
