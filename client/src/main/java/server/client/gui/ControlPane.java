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

    public ControlPane(ActionsPane actionsPane, Client client)
    {
        this.actionsPane = actionsPane;
        setPadding(new Insets(10));
        setHgap(10);
        setVgap(10);

        Label userIdLbl = new Label("User ID (int):");
        add(userIdLbl, 0,0);
        add(userIdInput, 1,0);

        Label passwordLbl = new Label("Password:");
        add(passwordLbl, 0,1);
        add(passwordInput, 1,1);

        Button submitBtn = new Button("Submit");
        submitBtn.setOnAction(event -> client.execute(parseID(), passwordInput.getText(), actionsPane));
        add(submitBtn, 1,7);

        Label actionsLbl = new Label("Actions:");
        add(actionsLbl, 0,4);

        Label amountLbl = new Label("Amount:");
        add(amountLbl, 0,5);

        Button incBtn = new Button("INCREASE");
        incBtn.setMinSize(40, 20);
        incBtn.setOnAction(event -> actionsPane.addIncrease(parseAmountInput()));
        add(incBtn, 0,6);

        amountInput.setText("10");
        amountInput.setMaxWidth(40);
        add(amountInput, 1,5);

        Button decBtn = new Button("DECREASE");
        decBtn.setMinSize(40, 20);
        decBtn.setOnAction(event -> actionsPane.addDecrease(parseAmountInput()));
        add(decBtn, 1,6);

        Label counterLbl = new Label("Counter: ");
        add(counterLbl, 0, 8);
        add(counterAmount, 1, 8);

        setVisible(true);
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
}
