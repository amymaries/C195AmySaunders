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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.*;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * This controller controls the ModifyAppointment screen, which allows users to update or delete appointments.
 */

public class ModifyAppointmentController implements Initializable {

    Stage stage;
    Parent scene;
    Appointment appointment;
    DBQuery dbQuery = new DBQuery();



    @FXML
    private TextField ApptIDTF;

    @FXML
    private ComboBox<User> UserComboBox;

    @FXML
    private ComboBox<Customer> CustomerComboBox;


    @FXML
    private TextField TitleTF;

    @FXML
    private TextField DescriptionTF;

    @FXML
    private TextField LocationTF;

    @FXML
    private ComboBox<Contacts> ContactComboBox;

    @FXML
    private ComboBox<String> AppointmentTypeComboBox;

    @FXML
    private DatePicker DatePicker;

    @FXML
    private ComboBox<LocalTime> StartComboBox;

    @FXML
    private ComboBox<LocalTime> EndComboBox;

    @FXML
    private Label ModApptMessage;


    /**
     * This method returns user to the main screen.
     * @param event cancel button clicked
     * @throws IOException possible exception
     */
    @FXML
    void OnActionCancel(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "This will clear text field values. Would you like to proceed?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource("/View/MainMenuAppointment.fxml"));
            stage.setTitle("Appointment Management System");
            stage.setScene(new Scene(scene));
            stage.show();
        }
    }

    /**
     * This method allows user to delete an appointment from the DB.
     * @param event delete button clicked
     * @throws IOException possible exception
     */
    @FXML
    void OnActionDeleteAppointment(ActionEvent event) throws IOException {
        int appointmentId = Integer.parseInt(ApptIDTF.getText());
        String appointmentType = AppointmentTypeComboBox.getValue();
        Alert alert = new Alert(Alert.AlertType.WARNING, "You are deleting this appointment. Would you like to proceed?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            dbQuery.deleteAppointment(appointmentId);
            ModApptMessage.setText("Appointment successfully deleted.");
            alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Appointment Deleted");
            alert.setHeaderText("Your appointment was deleted.");
            alert.setContentText("Appointment ID: " + appointmentId + "\n" +
                    "Type: " + appointmentType);
            result = alert.showAndWait();
            if(result.isPresent() && result.get() == ButtonType.OK){
                    stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                    scene = FXMLLoader.load(getClass().getResource("/view/MainMenuAppointment.fxml"));
                    stage.setTitle("Appointment Scheduling System");
                    stage.setScene(new Scene(scene));
                    stage.show();
                }
            }

    }

    /**
     * This method allows user to update appointments after requisite validations.
     * @param event update button clicked
     */
    @FXML
    void OnActionUpdateAppointment(ActionEvent event) {
        //Ensure fields are filled
        if (ApptIDTF.getText().isEmpty() || UserComboBox.getValue() == null || CustomerComboBox.getValue() == null || TitleTF.getText().isEmpty()
                || DescriptionTF.getText().isEmpty() || LocationTF.getText().isEmpty() || ContactComboBox.getValue() == null || AppointmentTypeComboBox.getValue() == null
                || DatePicker.getValue() == null || StartComboBox.getValue() == null || EndComboBox.getValue() == null ){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setContentText("Please enter a valid value for each field.");
            alert.showAndWait();
        }else {
            try {
                ObservableList<User> allUsers = dbQuery.getAllUsers();
                ObservableList<Contacts> allContacts = dbQuery.getAllContacts();
                int appointmentId = Integer.parseInt(ApptIDTF.getText());
                String appointmentTitle = TitleTF.getText();
                String description = DescriptionTF.getText();
                String location = LocationTF.getText();
                String appointmentType = AppointmentTypeComboBox.getValue();
                LocalDate date = DatePicker.getValue();
                LocalTime startTime = StartComboBox.getValue();
                LocalTime endTime = EndComboBox.getValue();
                LocalDateTime appointmentStartTime = LocalDateTime.of(date, startTime);
                LocalDateTime appointmentEndTime = LocalDateTime.of(date, endTime);
                //I need the userId, contactId, customerId from their comboBoxes
                User u = UserComboBox.getValue();
                int userId = u.getUserID();
                Customer c = CustomerComboBox.getValue();
                int customerId = c.getCustomerId();
                Contacts contacts = ContactComboBox.getValue();
                int contactId = contacts.getContactID();
                //check for conflicting appointments
                Appointment conflictingAppointment = appointment.scheduleOverlaps(customerId, appointmentStartTime, appointmentEndTime);
                if(conflictingAppointment != null && conflictingAppointment.getAppointmentID() != Integer.parseInt(ApptIDTF.getText())){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Appointment Conflict");
                    alert.setHeaderText("This Customer already has an appointment at the selected time.");
                    alert.setContentText("Appointment ID: " + conflictingAppointment.getAppointmentID() + "\n" +
                            "Date: " + conflictingAppointment.getAppointmentStart().toLocalDate() + "\n" +
                            "Start: " + conflictingAppointment.getAppointmentStart().toLocalTime() + "\n" +
                            "End: " + conflictingAppointment.getAppointmentEnd().toLocalTime());
                    alert.showAndWait();
                    return;
                }
                //inside an if method to see if it updated first make it return a boolean
                dbQuery.updateAppointment(appointmentId, appointmentTitle, description, location, appointmentType,
                        appointmentStartTime, appointmentEndTime, customerId, userId, contactId);

                ModApptMessage.setText("Your appointment was updated.");
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Your appointment was updated.");
                Optional<ButtonType> result = alert.showAndWait();
                if(result.isPresent() && result.get() == ButtonType.OK){
                    stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                    scene = FXMLLoader.load(getClass().getResource("/view/MainMenuAppointment.fxml"));
                    stage.setTitle("Appointment Scheduling System");
                    stage.setScene(new Scene(scene));
                    stage.show();
                }
            } catch (NumberFormatException | IOException | SQLException e) {
                //setup error dialog box
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning Dialog");
                alert.setContentText("Please enter a valid value for each text field.");
                alert.showAndWait();
            }
        }


    }

    /**getSelectedAppointment method is used to auto-populate details from selectedAppointment on the MainScreen to the modify screen.
     @param selectedAppointment is the appointment selected from the mainScreen appointments table.
     @param currentUser the username passed in from loginController
     @throws SQLException possible
     */
    public void getSelectedAppointment (Appointment selectedAppointment, String currentUser) throws SQLException {
        this.appointment = selectedAppointment;

        ApptIDTF.setText(Integer.toString(this.appointment.getAppointmentID()));
        TitleTF.setText(this.appointment.getAppointmentTitle());
        DescriptionTF.setText(this.appointment.getDescription());
        LocationTF.setText(this.appointment.getLocation());
        AppointmentTypeComboBox.setValue(this.appointment.getAppointmentType());
        Customer customer = dbQuery.getCustomerName(this.appointment.getCustomerID());
        CustomerComboBox.setValue(customer);
        User user  = dbQuery.getUserfromUsername(currentUser);
        UserComboBox.setValue(user);

        for(Contacts contact: ContactComboBox.getItems()){
            if(contact.getContactID()==selectedAppointment.getContactID()){
                ContactComboBox.setValue(contact);
                break;
            }
        }
        LocalDate date = this.appointment.getAppointmentStart().toLocalDate();
        LocalTime start = this.appointment.getAppointmentStart().toLocalTime();
        LocalTime end = this.appointment.getAppointmentEnd().toLocalTime();
        DatePicker.setValue(date);
        StartComboBox.setValue(start);
        EndComboBox.setValue(end);
    }


    /**
     * Initialize is the first method run. It populates the comboboxes and makes the time conversion for the Start/End comboboxes.
     * @param url url
     * @param resourceBundle resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //fill Types ComboBox
        ObservableList<String> appointmentTypes = FXCollections.observableArrayList("Board Meeting", "Staff Meeting", "Audition", "Masterclass", "Music Lesson");
        AppointmentTypeComboBox.setItems(appointmentTypes);


        try {
            ObservableList<User> allUsers = dbQuery.getAllUsers();
            UserComboBox.setItems(allUsers);
            ObservableList<Customer> allCustomersNameID = dbQuery.getCustomersNameID();
            CustomerComboBox.setItems(allCustomersNameID);
            ObservableList<Contacts> allContacts = dbQuery.getAllContacts();
            ContactComboBox.setItems(allContacts);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


        //fill up Start and End times ComboBoxes
        LocalTime hcbhStart = LocalTime.of(8, 0);
        LocalTime hcbhEnd = LocalTime.of(22, 0);
        LocalDateTime bhldtStart = LocalDateTime.of(LocalDate.now(), hcbhStart);
        LocalDateTime bhldtEnd = LocalDateTime.of(LocalDate.now(), hcbhEnd);
        ZonedDateTime bhestStart = bhldtStart.atZone(ZoneId.of("America/New_York"));
        ZonedDateTime bhestEnd = bhldtEnd.atZone(ZoneId.of("America/New_York"));
        ZonedDateTime bhltStart = bhestStart.withZoneSameInstant(ZoneId.systemDefault());
        ZonedDateTime bhltEnd = bhestEnd.withZoneSameInstant(ZoneId.systemDefault());
        LocalTime bhStart = bhltStart.toLocalTime();
        LocalTime bhEnd = bhltEnd.toLocalTime();


        LocalTime first = bhStart;
        LocalTime last = bhStart.plusMinutes(30);
        while(first.isBefore(bhEnd.minusSeconds(1))){
            StartComboBox.getItems().add(first);
            EndComboBox.getItems().add(last);
            first = first.plusMinutes(30);
            last = first.plusMinutes(30);

        }

    }
}
