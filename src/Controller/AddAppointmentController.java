package Controller;

/**
 * @author ASaunders
 */

import Model.Appointment;
import Model.Contacts;
import Model.Customer;
import Model.User;
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
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * This controller controls the Update Appointment screen. It contains methods for validating, then adding/deleting appointments.
 */

public class AddAppointmentController implements Initializable {

    Stage stage;
    Parent scene;
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
    private Label AddApptMessage;

    @FXML
    private DatePicker DatePicker;

    @FXML
    private ComboBox<LocalTime> StartHourComboBox;

    @FXML
    private ComboBox<LocalTime> EndHourComboBox;






    /***
     * onActionCancel method returns user to the Main Appointment Screen form after verification.
     * @param event user clicks the 'Cancel' button on the AddAppointment Controller form.
     * @throws IOException possible IO exception
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

    /**onActionSave method is used to save user-entered information and add an appointment to the scheduler.
     @param event user clicks the 'Save' button on the Add Appointment Controller form. IOException handled with try/catch.
     */

    @FXML
    void OnActionSave(ActionEvent event) {
        //Ensure fields are filled
        if (UserComboBox.getValue() == null || CustomerComboBox.getValue() == null || TitleTF.getText().isEmpty() || DescriptionTF.getText().isEmpty()
                || LocationTF.getText().isEmpty() || ContactComboBox.getValue() == null || AppointmentTypeComboBox.getValue() == null
                || DatePicker.getValue() == null || StartHourComboBox.getValue() == null || EndHourComboBox.getValue() == null ){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setContentText("Please enter a valid value for each field.");
            alert.showAndWait();
        }else {
            try {
                String appointmentTitle = TitleTF.getText();
                String description = DescriptionTF.getText();
                String location = LocationTF.getText();
                String appointmentType = AppointmentTypeComboBox.getValue();
                LocalDate date = DatePicker.getValue();
                LocalTime startTime = StartHourComboBox.getValue();
                LocalTime endTime = EndHourComboBox.getValue();
                LocalDateTime appointmentStartTime = LocalDateTime.of(date, startTime);
                LocalDateTime appointmentEndTime = LocalDateTime.of(date, endTime);
                //I need the userId, contactId, customerId from their comboBoxes
                User u = UserComboBox.getValue();
                int userId = u.getUserID();
                Customer c = CustomerComboBox.getValue();
                int customerId = c.getCustomerId();
                Contacts contacts = ContactComboBox.getValue();
                int contactId = contacts.getContactID();
                //check for conflicting appointments //this gave me the wrong error, fill all fields
                Appointment conflictingAppointment = Appointment.scheduleOverlaps(customerId, appointmentStartTime, appointmentEndTime);
                if (conflictingAppointment != null) { //if (conflictingAppointment != null && conflictingAppointment.getAppointmentID() != Integer.parseInt(ApptIDTF.getText())) {ApptIDTF is empty at this point, creating an NumberFormatException for empty input string.
                    Alert alert = new Alert(Alert.AlertType.ERROR); //so where do I add a check for conflict with the same appointmentId?
                    alert.setTitle("Appointment Conflict");
                    alert.setHeaderText("This Customer already has an appointment at the selected time.");
                    alert.setContentText("Appointment ID: " + conflictingAppointment.getAppointmentID() + "\n" +
                            "Date: " + conflictingAppointment.getAppointmentStart().toLocalDate() + "\n" +
                            "Start: " + conflictingAppointment.getAppointmentStart().toLocalTime() + "\n" +
                            "End: " + conflictingAppointment.getAppointmentEnd().toLocalTime());
                    alert.showAndWait();
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                        scene = FXMLLoader.load(getClass().getResource("/view/MainMenuAppointment.fxml"));
                        stage.setTitle("Appointment Scheduling System");
                        stage.setScene(new Scene(scene));
                        stage.show();
                    }
                } else if (appointmentStartTime.isBefore(LocalDateTime.now()) || appointmentEndTime.isBefore(LocalDateTime.now()) || appointmentEndTime.isBefore(appointmentStartTime)){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Time/Date Error");
                    alert.setHeaderText("Please select a time/date in the future.");
                    alert.setContentText("Please make sure your appointment ends after it starts.");
                    alert.showAndWait();
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                        scene = FXMLLoader.load(getClass().getResource("/view/MainMenuAppointment.fxml"));
                        stage.setTitle("Appointment Scheduling System");
                        stage.setScene(new Scene(scene));
                        stage.show();
                    }
                } else {
                    dbQuery.createAppointment(appointmentTitle, description, location, appointmentType,
                            appointmentStartTime, appointmentEndTime, customerId, userId, contactId);
                    AddApptMessage.setText("Your appointment was added.");
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Your appointment was added.");
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                        scene = FXMLLoader.load(getClass().getResource("/view/MainMenuAppointment.fxml"));
                        stage.setTitle("Appointment Scheduling System");
                        stage.setScene(new Scene(scene));
                        stage.show();
                    }
                }

            } catch (NumberFormatException | IOException | SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Warning Dialog");
                alert.setContentText("Please enter a valid value for each text field.");
                alert.showAndWait();
            }
        }
    }

    /**
     * This method populates the Contacts comboBoxes, cell and list, via two lambda expressions. These lamdas
     * set the contact combobox (list has contactID included).
     * @throws SQLException possible
     */
    public void setContactComboBox() throws SQLException {
        DBQuery dbQuery = new DBQuery(); //instance
        ContactComboBox.setItems(dbQuery.getAllContacts());
        ContactComboBox.getSelectionModel().selectFirst();

        //lambda in place of Overriding the toString method
        //Factory for the List Cells
        Callback<ListView<Contacts>, ListCell <Contacts>> factory = lv -> new ListCell<>() {
            @Override
            protected void updateItem(Contacts item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "-" :( "[" + item.getContactID() + "] " + item.getContactName()));
            }
        };
        // Different Factory for button cell
        Callback <ListView<Contacts>, ListCell<Contacts>> cellFactory = lv -> new ListCell<>() {
            @Override
            protected void updateItem(Contacts item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getContactName());
            }
        };
        //use separate factories
        ContactComboBox.setCellFactory(factory);
        ContactComboBox.setButtonCell(cellFactory.call(null));

    }

    /**
     * initialize method called when Add Appointment screen is loaded. It
     * populates comboboxes with options for creating new appointment objects.
     * @param url required
     * @param resourceBundle required
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> appointmentTypes = FXCollections.observableArrayList("Board Meeting", "Staff Meeting", "Audition", "Masterclass", "Music Lesson");
        //List<String> appointmentTypes = Arrays.asList("Board Meeting", "Staff Meeting", "Audition", "Masterclass", "Music Lesson");
        //appointmentTypes.forEach(i -> System.out.println(i));
        ObservableList<Customer> allCustomersNameID = null;
        ObservableList<User> allUsers = null;
        //ObservableList<Contacts> allContacts = null;

        //fill Types ComboBox
        AppointmentTypeComboBox.setItems(appointmentTypes);//why won't comboBox take List?

        //fill Users ComboBox
        try {
            allUsers = dbQuery.getAllUsers();
            allCustomersNameID = dbQuery.getCustomersNameID();
            //allContacts = dbQuery.getAllContacts();
            setContactComboBox();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        UserComboBox.setItems(allUsers);

        //fill Customers ComboBox
        CustomerComboBox.setItems(allCustomersNameID);

        //fill up Contacts comboBox
        //ContactComboBox.setItems(allContacts);

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
            StartHourComboBox.getItems().add(first);
            EndHourComboBox.getItems().add(last);
            first = first.plusMinutes(30);
            last = first.plusMinutes(30);

        }
    }
}
