package server.client.networking;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import org.springframework.boot.SpringApplication;
import server.client.gui.Action;
import server.client.gui.ControlPane;
import server.client.gui.ActionsPane;

public class Client extends BorderPane
{
    public ActionsPane actionsPane;
    public ControlPane controlPane;
    private Session session;
    private Timeline timeline;

    public Client()
    {
        String[] args = {};
        SpringApplication.run(Server.class, args);

        actionsPane = new ActionsPane();
        this.setRight(actionsPane);

        controlPane = new ControlPane(actionsPane, this);
        this.setLeft(controlPane);

        session = null;
    }

    public void execute(int id, String password, ActionsPane actions)
    {
        try
        {
            session = new Session();
            String steps = actions.getSteps();
            int authStatus = session.postAuthRequest(id, password, controlPane.getDelay(), steps);

            timeline = new Timeline(new KeyFrame( Duration.millis(controlPane.getDelay() * 1000), ae -> fireOffAction()));
            timeline.setCycleCount(Animation.INDEFINITE);
            timeline.play();

            System.out.println(authStatus);

        } catch(Exception e) {
            System.out.println("Somebody done gone fucked up\n\n\n\n");
            e.printStackTrace();
        }

    }

    private void fireOffAction()
    {
        try
        {
            if(session != null && actionsPane.hasActions())
            {
                Action current = actionsPane.popAction();
                session.postChangeRequest(current.direction, session.getId(), current.amount);
                controlPane.updateCounter(session.getCounter());
            }
            else
            {
                timeline.stop();
            }
        }
        catch(Exception e)
        {
            timeline.stop();
        }
    }
}
