package server.client;

import javafx.geometry.Insets;
import javafx.geometry.Pos;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class ActionsPane extends VBox
{
    private LinkedList<Action> actions = new LinkedList<>();

    public ActionsPane()
    {
        BackgroundFill bf = new BackgroundFill(Color.GRAY, CornerRadii.EMPTY , Insets.EMPTY);
        setBackground(new Background(bf));
        setAlignment(Pos.TOP_CENTER);
        setSpacing(5);
        setMaxWidth(140d);
        setMinWidth(140d);

        addIncrease(10);
        addDecrease(10);
        getActions().forEach(action -> System.out.println(action.toString()));

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
}
