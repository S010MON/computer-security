package server.client.networking;

import javafx.scene.layout.BorderPane;
import org.springframework.boot.SpringApplication;
import server.client.gui.Action;
import server.client.gui.ControlPane;
import server.client.gui.ActionsPane;
import server.client.gui.Direction;

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
            Session session = new Session();

            String steps = actions.getSteps();

            int authStatus = session.postAuthRequest(id, password, 100, steps);

            System.out.println(authStatus);

            // for every action
            while(actions.hasActions())
            {
                Action current = actions.popAction();
                session.postChangeRequest(current.direction, session.getId(), current.amount);
                Thread.sleep(10000);
            }

        } catch(Exception e) {
            System.out.println("Somebody done gone fucked up\n\n\n\n");
            e.printStackTrace();
        }

    }

}
