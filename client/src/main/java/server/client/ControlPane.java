package server.client;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class ControlPane extends GridPane
{
    ActionsPane actionsPane;
    TextField amountInput = new TextField();


    public ControlPane(ActionsPane actionsPane)
    {
        this.actionsPane = actionsPane;

        setHgap(10);
        setVgap(10);

        Label userIdLbl = new Label("User ID:");
        add(userIdLbl, 0,0);

        TextField userIdInput = new TextField();
        add(userIdInput, 1,0);

        Label passwordLbl = new Label("Password:");
        add(passwordLbl, 0,1);
        TextField passwordInput = new TextField();
        add(passwordInput, 1,1);


        Label actionsLbl = new Label("Actions:");
        add(actionsLbl, 0,3);

        Button incBtn = new Button("+");
        incBtn.setMinSize(40, 20);
        incBtn.setOnAction(event -> actionsPane.addIncrease(parseAmountInput()));
        add(incBtn, 0,5);

        amountInput.setText("10");
        amountInput.setMaxWidth(40);
        add(amountInput, 0,4);

        Button decBtn = new Button("-");
        decBtn.setMinSize(40, 20);
        decBtn.setOnAction(event -> actionsPane.addDecrease(parseAmountInput()));
        add(decBtn, 1,5);

        Button submitBtn = new Button("Submit");
        add(submitBtn, 1,6);

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
}
