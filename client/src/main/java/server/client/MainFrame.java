package server.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFrame extends Application
{
    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(Stage stage)
    {
        stage.setTitle("Secure Client");
        Client client = new Client();
        stage.setScene(new Scene(client, 400, 500));
        stage.show();
    }
}
