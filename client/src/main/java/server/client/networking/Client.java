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
        Request authRequest = new Request("http://127.0.0.1/");
        String steps = actions.getSteps();

        String requestBody = authRequest.formatAuthRequestBody(id, password, 100, steps);
        int authStatus = authRequest.postAuthRequest(id, password, requestBody);
        System.out.println(authStatus);


    }

}
