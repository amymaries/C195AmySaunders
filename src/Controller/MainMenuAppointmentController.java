package Controller;

/**
 * @author ASaunders
 */

import Model.*;
import Utils.DBQuery;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ResourceBundle;

/**
 * This controller controls the MainMenuAppointment Screen.
 */

public class MainMenuAppointmentController implements Initializable {


    Stage stage;
    Parent scene;
    DBQuery dbQuery = new DBQuery();
    private ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    private ObservableList<Appointment> allAppointments = dbQuery.getAllAppointments();
    private ObservableList<Appointment> monthlyAppointments = FXCollections.observableArrayList();
    private ObservableList<Appointment> weeklyAppointments = FXCollections.observableArrayList();
    ObservableList<String> appointmentTypes = FXCollections.observableArrayList("Board Meeting", "Staff Meeting", "Audition", "Masterclass", "Music Lesson");
    private static String currentUser;




    @FXML
    private Button AddAppointment;

    @FXML
    private Button ModifyAppointment;

    @FXML
    private Button AddCustomer;

    @FXML
    private Button UpdateCustomer;

    @FXML
    private Button Reports;

    @FXML
    private MenuItem MenuItemAddCustomer;

    @FXML
    private TableView<Appointment> MSAppointmentsTableView;

    @FXML
    private TableColumn<Appointment, Integer> MSApptIDCol;

    @FXML
    private TableColumn<Appointment, Integer> MSCustIDCol;

    @FXML
    private TableColumn<Appointment, String> MSTitleCol;

    @FXML
    private TableColumn<Appointment, String> MSDescriptionCol;

    @FXML
    private TableColumn<Appointment, String> MSContactCol;

    @FXML
    private TableColumn<Appointment, String> MSLocationCol;

    @FXML
    private TableColumn<Appointment, String> MSTypeCol;

    @FXML
    private TableColumn<Appointment, LocalDateTime> MSStartTimeCol;//not sure about Timestamp (sql) datatype

    @FXML
    private TableColumn<Appointment, LocalDateTime> MSEndTimeCol;


    /**
     * This method takes users to the AddAppointment screen.
     * @param event button clicked
     * @throws IOException possible exception
     */
    @FXML
    void OnActionAddAppointment(ActionEvent event) throws IOException {
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/View/AddAppointment.fxml"));
        stage.setTitle("Add Appointment");
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * This method takes users to the modifyAppointment screen.
     * @param event button clicked
     * @throws IOException possible exception
     * @throws SQLException possible exception
     */
    @FXML
    void OnActionModifyAppointment(ActionEvent event) throws IOException, SQLException {
        if(MSAppointmentsTableView.getSelectionModel().getSelectedItem() != null){
            Stage stage;
            Parent root;
            stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/ModifyAppointment.fxml"));
            root = loader.load();
            Scene scene = new Scene(root);
            stage.setTitle("Modify Appointment");
            stage.setScene(scene);
            stage.show();
            ModifyAppointmentController controller = loader.getController();
            Appointment selectedAppointment = MSAppointmentsTableView.getSelectionModel().getSelectedItem();
            controller.getSelectedAppointment(selectedAppointment, currentUser);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select an appointment to modify from the table.");
            alert.showAndWait();
        }
    }

    /**
     * This method allows users to exit the program.
     * @param event button clicked
     */
    @FXML
    void OnActionLogout(ActionEvent event) {
        System.exit(0);
    }


    /**
     * This method takes users to the AddCustomer screen.
     * @param event button clicked
     * @throws IOException possible exception
     */
    @FXML
    void OnActionShowAddCustomerScreen(ActionEvent event) throws IOException {
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/View/AddCustomer.fxml"));
        stage.setTitle("Add Customer");
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * This method takes users to the UpdateCustomers screen.
     * @param event button clicked
     * @throws IOException possible exception
     */
    @FXML
    void OnActionShowUpdateCustomerScreen(ActionEvent event) throws IOException {
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/View/ModifyCustomer.fxml"));
        stage.setTitle("Modify Customer");
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * This method takes users to the Reports screen.
     * @param event button clicked
     * @throws IOException possible exception
     */
    @FXML
    void OnActionShowReportsMenu(ActionEvent event) throws IOException {
        stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/View/Reports.fxml"));
        stage.setTitle("Reports");
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * This method allows users to see all appointments for the current (not upcoming) week.
     * It always starts at Sunday of the week in which the user is searching.
     * @param event button clicked
     */
    @FXML
    void OnActionWeeklyAppointments(ActionEvent event){
        getTableData();
        MSAppointmentsTableView.setItems(dbQuery.getWeeklyAppoinments());

    }

    /**
     * This method allows users to see all appointments for the current (not upcoming) month.
     * It always starts on the first of the month in which the user is searching.
     * @param event button clicked
     */
    @FXML
    void OnActionMonthlyAppointments(ActionEvent event) {
        //MSAppointmentsTableView.getItems().clear();
        getTableData();
        MSAppointmentsTableView.setItems(dbQuery.getMonthlyAppointments());
    }

    /**
     * This method allows users to see all appointments.
     * @param event button clicked
     */
    @FXML
    void OnActionViewAllAppointments(ActionEvent event) {
        getTableData();
        MSAppointmentsTableView.setItems(allAppointments);
    }

    /**
     * This method checks for appointments within the next 15 minutes and warns user.
     * @param username username
     * @throws SQLException possible exception
     */
    public void imminentAppointment(String username) throws SQLException {
        this.currentUser = username;
        ObservableList<Appointment> allAppointments = dbQuery.getAllAppointments();
        ObservableList<User> allUsers = dbQuery.getAllUsers();
        Appointment imminentAppointment = null;
        User user = null;
        //get username entered at login
        for(User u : allUsers){
            if(u.getUserName().equals(username)){
                user = u;
            }
        }
        //get possible appointment within 15 minutes
        for(Appointment a : allAppointments){
            long timeDifference = ChronoUnit.MINUTES.between(LocalDateTime.now(), a.getAppointmentStart());
            assert user != null;
            if(a.getUserID() == user.getUserID() && timeDifference > 0 && timeDifference <= 15) {
                imminentAppointment = a;
            }
        }

        //display message in dialog box if there is an imminent appointment.
        if(imminentAppointment != null){
            LocalDate appointmentDate = imminentAppointment.getAppointmentStart().toLocalDate();
            LocalTime appointmentTime = imminentAppointment.getAppointmentStart().toLocalTime();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Upcoming Appointment");
            alert.setHeaderText("You have an appointment within 15 minutes.");
            alert.setContentText("Appointment ID: " + imminentAppointment.getAppointmentID() + "\n" +
                    " Date: " + appointmentDate + "\n" +
                    " Time: " + appointmentTime);
            alert.showAndWait();
        }
        //if no appointment was found, display no upcoming appointment reminder
        else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Upcoming Appointment");
            alert.setHeaderText("You have no upcoming appointments.");
            alert.showAndWait();
        }

    }

    /**
     * This method populated the table with data.
     */
    public void getTableData() {
        MSApptIDCol.setCellValueFactory(new PropertyValueFactory<Appointment, Integer>("appointmentID"));
        MSCustIDCol.setCellValueFactory(new PropertyValueFactory<Appointment, Integer>("customerID"));
        MSTitleCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("appointmentTitle"));
        MSDescriptionCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("description"));
        MSContactCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("contactName"));
        MSLocationCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("location"));
        MSTypeCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("appointmentType"));
        MSStartTimeCol.setCellValueFactory(new PropertyValueFactory<Appointment, LocalDateTime>("appointmentStart"));
        MSEndTimeCol.setCellValueFactory(new PropertyValueFactory<Appointment, LocalDateTime>("appointmentEnd"));
    }

    /**
     * Initializes the MainMenuAppointmentController class and populates the appointments table.
     * @param url required
     * @param resourceBundle required
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //configure the TableView to get data from the DB!
        getTableData();
        MSAppointmentsTableView.setItems(allAppointments);







    }

}
