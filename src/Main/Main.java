package Main;

/**
 * @author ASaunders
 * */

import Utils.DBConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Main launches the app, taking the user to the login screen and setting the language.
 */

public class Main extends Application {

    /**
     * The start method presents user with the Login screen
     * @param primaryStage very first stage
     * @throws Exception possible
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../View/Login.fxml"));
        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();

    }


    /***
     * Main method runs first, setting default language and opening the DB connection
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ResourceBundle rb = ResourceBundle.getBundle("Languages/languages", Locale.getDefault());

        DBConnection.startConnection();

        launch(args);//anything after launch will only be called once after all other parts of the app are closed.

        DBConnection.closeConnection();
    }
}


