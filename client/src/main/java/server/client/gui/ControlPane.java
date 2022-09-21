package server.client.gui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import server.client.networking.Client;

public class ControlPane extends GridPane
{
    ActionsPane actionsPane;
    TextField amountInput = new TextField();
    TextField userIdInput = new TextField("1");
    TextField passwordInput = new TextField("pass");
    Label counterAmount = new Label("");
    TextField delayInput = new TextField("1");

    public ControlPane(ActionsPane actionsPane, Client client)
    {
        this.actionsPane = actionsPane;
        setPadding(new Insets(10));
        setHgap(10);
        setVgap(10);
        
        Label userIdLbl = new Label("User\sID\s(int):");
        add(userIdLbl, 0,0);
        add(userIdInput, 1,0);

        Label passwordLbl = new Label("Password:");
        add(passwordLbl, 0,1);
        add(passwordInput, 1,1);

        Label actionsLbl = new Label("Actions:");
        add(actionsLbl, 0,3);

        Label amountLbl = new Label("Amount:");
        add(amountLbl, 0,4);
        amountInput.setText("1");
        amountInput.setMaxWidth(40);
        add(amountInput, 1,4);

        Label delayLbl = new Label("Delay:");
        add(delayLbl, 0,5);
        delayInput.setMaxWidth(40);
        add(delayInput, 1, 5);

        Button incBtn = new Button("INCREASE");
        incBtn.setMinSize(40, 20);
        incBtn.setOnAction(event -> actionsPane.addIncrease(parseAmountInput()));
        add(incBtn, 0,7);

        Button decBtn = new Button("DECREASE");
        decBtn.setMinSize(40, 20);
        decBtn.setOnAction(event -> actionsPane.addDecrease(parseAmountInput()));
        add(decBtn, 1,7);

        Button popBtn = new Button("Undo");
        popBtn.setOnAction(event -> actionsPane.popAction());
        add(popBtn, 0,8);

        Button submitBtn = new Button("Submit");
        submitBtn.setOnAction(event -> client.execute(parseID(), passwordInput.getText(), actionsPane));
        add(submitBtn, 1,8);

        Label counterLbl = new Label("Counter: ");
        add(counterLbl, 0, 9);
        add(counterAmount, 1, 9);

        setVisible(true);
    }

    public int getDelay()
    {
        return parseDelayInput();
    }

    public void updateCounter(int counter)
    {
        counterAmount.setText(Integer.toString(counter));
    }

    private int parseAmountInput()
    {
        try
        {
            return Integer.parseInt(amountInput.getText());
        }
        catch(NumberFormatException e)
        {
            return 0;
        }
    }

    private int parseID()
    {
        try
        {
            return Integer.parseInt(userIdInput.getText());
        }
        catch(NumberFormatException e)
        {
            return 0;
        }
    }

    private int parseDelayInput()
    {
        try
        {
            return Integer.parseInt(delayInput.getText());
        }
        catch(NumberFormatException e)
        {
            return 1;
        }
    }
}
