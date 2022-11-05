package server.client.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class ActionsPane extends VBox
{
    public ActionsPane()
    {
        BackgroundFill bf = new BackgroundFill(Color.GRAY, CornerRadii.EMPTY , Insets.EMPTY);
        setBackground(new Background(bf));
        setAlignment(Pos.TOP_CENTER);
        setSpacing(5);
        setMaxWidth(140d);
        setMinWidth(140d);

        setVisible(true);
    }

    public void addIncrease(int amount)
    {
        getChildren().add(new Action(Direction.INCREASE, amount));
    }

    public void addDecrease(int amount)
    {
        getChildren().add(new Action(Direction.DECREASE, amount));
    }

    public String getSteps()
    {
        List<Action> actions = getActions();

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for(int i = 0; i <= actions.size() -1; i++)
        {
            sb.append(actions.get(i).toString());
            if(i != (actions.size()-1))
                sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    public List<Action> getActions()
    {
        ArrayList<Action> actions = new ArrayList<>();
        for(Object o: getChildren())
        {
            if(o instanceof Action)
                actions.add((Action) o);
        }
        return actions;
    }

    public boolean hasActions()
    {
        return getChildren().size() != 0;
    }

    public Action popAction()
    {
        Action a = (Action) getChildren().remove(0);
        return a;
    }
}
