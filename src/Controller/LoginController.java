package Controller;

import Model.Appointment;
import Model.User;
import Utils.DBConnection;
import Utils.DBQuery;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.ZoneId;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/**author ASaunders
 This controller controls the initial Login Form.*/


public class LoginController implements Initializable {


    ResourceBundle rb = ResourceBundle.getBundle("Languages/languages", Locale.getDefault());




    @FXML
    private Label LoginFormHeaderLabel;

    @FXML
    private TextField UsernameTextField;

    @FXML
    private Label UsernameLabel;

    @FXML
    private PasswordField PasswordTextField;

    @FXML
    private Label PasswordLabel;

    @FXML
    private Label LoginStatus;

    @FXML
    private Button LoginButton;

    @FXML
    private Label ZoneLabel;



    /**
     * OnActionLogin verifies the username and password with the SQL customers table,
     * record the attempt in login_activity.txt, and open the Main Menu Appointment Scheduler.
     * @param event login button clicked
     * @throws IOException possible
     * @throws SQLException possible
     */
    @FXML
    void OnActionLogin(ActionEvent event) throws IOException, SQLException {
        String username = UsernameTextField.getText();
        String password = PasswordTextField.getText();
        boolean isValid = validation(username, password);
        FileWriter fileWriter = new FileWriter("login_activity.txt", true);
        BufferedWriter logger = new BufferedWriter(fileWriter);
        Timestamp loginTime = new Timestamp(System.currentTimeMillis());
        String successString = rb.getString("LoginStatusSuccess");
        String errorString = rb.getString("LoginStatusFailed");

        if(isValid){
            LoginStatus.setText(successString);
            logger.write(username + " logged in successfully at: " + loginTime);
            logger.newLine();
            logger.close();
            //imminentAppointment returns an appointment if there is one in the next 15 minutes.
            // popup dialog displays no upcoming appointments OR an appointmentID, date and time.
            Stage stage;
            Parent root;
            stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/MainMenuAppointment.fxml"));
            stage.setTitle("Appointment Management System");
            root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            MainMenuAppointmentController controller = loader.getController();
            controller.imminentAppointment(username);
        } else {
            LoginStatus.setText(errorString);
            logger.write(username + " attempted to login at: " + loginTime);
            logger.newLine();
            logger.close();
          }
    }


    /**
     * This method validates the user-information against information stored in the DB.
     * @param username username
     * @param password password
     * @return boolean valid or not
     */
    public boolean validation(String username, String password){
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            String query = "SELECT * FROM users WHERE User_Name ='" + username + "' AND Password ='" + password + "'";
            ResultSet resultSet = statement.executeQuery(query);
            if(resultSet.next()){
                return true;
            } else {
                return false;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }


    /**
     * setTextLabels method sets the text for the various labels on the login form
     * in a hashtable so the program can be partionally internationalized.
     */
    @FXML
    public void setTextLabels() {
        LoginFormHeaderLabel.setText(rb.getString("LoginFormHeaderLabel"));
        UsernameLabel.setText(rb.getString("UsernameLabel"));
        PasswordLabel.setText(rb.getString("PasswordLabel"));
        LoginButton.setText(rb.getString("LoginButton"));
        LoginStatus.setText(rb.getString("LoginStatus"));
        ZoneLabel.setText(rb.getString("ZoneLabel"));
    }


    /**
     * Initializes the Login controller class.
     * @param url required
     * @param resourceBundle required
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        String string = ZoneId.systemDefault().toString();
        ZoneLabel.setText("Your location: " + string);
        setTextLabels();
    }
}

