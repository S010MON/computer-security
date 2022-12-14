package server.client.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

public class Action extends Label
{
    public Direction direction;
    public int amount;

    public Action(Direction direction, int amount)
    {
        this.direction = direction;
        this.amount = amount;

        setText(toLabel());
        setMinSize(120, 40);
        setAlignment(Pos.CENTER);

        BackgroundFill bf = new BackgroundFill(color(),
                new CornerRadii(10) ,
                new Insets(0d));

        setBackground(new Background(bf));
    }

    private String toLabel()
    {
        return direction.toString() + " " + amount;
    }

    private Color color()
    {
        switch(direction)
        {
            case DECREASE -> {return Color.RED;}
            case INCREASE -> {return Color.GREEN;}
        }
        return null;
    }

    @Override public String toString()
    {
        return "\"" + direction.toString() + " " + amount + "\"";
    }
}
