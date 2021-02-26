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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * This controller operates the reports screen.
 */

public class ReportsController implements Initializable {

    Stage stage;
    Parent scene;
    DBQuery dbQuery = new DBQuery();


    @FXML
    private ComboBox<String> MonthsComboBox;

    @FXML
    private ComboBox<Contacts> ContactsComboBox;

    @FXML
    private ComboBox<User> ConsultantsComboBox;

    @FXML
    private ComboBox<Customer> CustomersComboBox;

    @FXML
    private TableView<Reports.Report1> T1AppointmentTypesReportTable;

    @FXML
    private TableColumn<Reports.Report1, String> T1AppTypeCol;

    @FXML
    private TableColumn<Reports.Report1, Integer> T1CountCol;

    @FXML
    private TableView<Appointment> T2ContactScheduleTable;

    @FXML
    private TableColumn<Appointment, Integer> T2IDCol;

    @FXML
    private TableColumn<Appointment, String> T2TitleCol;

    @FXML
    private TableColumn<Appointment, String> T2TypeCol;

    @FXML
    private TableColumn<Appointment, String> T2DescriptionCol;

    @FXML
    private TableColumn<Appointment, Integer> T2CustIDCol;

    @FXML
    private TableColumn<Appointment, LocalDateTime> T2DateTimeCol;

    @FXML
    private TableColumn<Appointment, LocalDateTime> T2EndCol;

    @FXML
    private TableView<Appointment> T3CustomerScheduleTable;

    @FXML
    private TableColumn<?, ?> T3IDCol1;

    @FXML
    private TableColumn<?, ?> T3TitleCol;

    @FXML
    private TableColumn<?, ?> T3TypeCol;

    @FXML
    private TableColumn<?, ?> T3DescriptionCol1;

    @FXML
    private TableColumn<?, ?> T3CustIDCol;

    @FXML
    private TableColumn<?, ?> T3DateTimeCol;

    @FXML
    private TableView<Appointment> T4ConsultantScheduleTable;

    @FXML
    private TableColumn<?, ?> T4IDCol;

    @FXML
    private TableColumn<?, ?> T4TitleCol;

    @FXML
    private TableColumn<?, ?> T4TypeCol;

    @FXML
    private TableColumn<?, ?> T4DescriptionCol;

    @FXML
    private TableColumn<?, ?> T4CustIDCol;

    @FXML
    private TableColumn<?, ?> T4DateTimeCol;


    /**
     * The exit method allows user to return to the main screen.
     * @param event Return to Main Screen button clicked
     * @throws IOException possible exception
     */
    @FXML
    void OnActionExit(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "This will clear all reports. Would you like to proceed?");
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
     * This method controls the Consultant Schedule report.
     * @param event when a consultant is chosen from the combobox
     * @throws SQLException possible exception
     */
    @FXML
    void OnActionGetConsultants(ActionEvent event) throws SQLException {
        //Pull up the schedule by user(consultants)
        User user = ConsultantsComboBox.getValue();
        int userId = user.getUserID();

        T4IDCol.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        T4TitleCol.setCellValueFactory(new PropertyValueFactory<>("appointmentTitle"));
        T4TypeCol.setCellValueFactory(new PropertyValueFactory<>("appointmentType"));
        T4DescriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        T4CustIDCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        T4DateTimeCol.setCellValueFactory(new PropertyValueFactory<>("appointmentStart"));
        T4ConsultantScheduleTable.setItems(dbQuery.getAppointmentsPerUser(userId));
    }

    /**
     * This method controls the the Contacts Schedule report.
     * @param event when a contact is chosen from the combobox
     * @throws SQLException possible exception
     */
    @FXML
    void OnActionGetContacts(ActionEvent event) throws SQLException {
        //Pull up the schedule by contacts //use DB query to get allAppointments by contactId
        Contacts contact = ContactsComboBox.getValue();
        int contactId = contact.getContactID();
        //Populate Contacts Schedule Table
        T2IDCol.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        T2TitleCol.setCellValueFactory(new PropertyValueFactory<>("appointmentTitle"));
        T2TypeCol.setCellValueFactory(new PropertyValueFactory<>("appointmentType"));
        T2DescriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        T2CustIDCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        T2DateTimeCol.setCellValueFactory(new PropertyValueFactory<>("appointmentStart"));
        T2EndCol.setCellValueFactory(new PropertyValueFactory<>("appointmentEnd"));
        T2ContactScheduleTable.setItems(dbQuery.getAppointmentsPerContact(contactId));

    }

    /**
     * This method controls the Appointment Types report.
     * @param event when user chooses a month from the comboBox
     */
    @FXML
    void OnActionGetMonths(ActionEvent event) {
        String monthName = MonthsComboBox.getValue();
        int month = getMonthInt(monthName);
        ObservableList<Appointment> appointmentsByMonth = dbQuery.getAppointmentsByMonth(month);
        int boardMeeting = 0;
        int staffMeeting = 0;
        int audition = 0;
        int masterclass = 0;
        int musicLesson = 0;
        for(Appointment a: appointmentsByMonth){
            if (a.getAppointmentType().equals("Board Meeting")){
                boardMeeting++;
            }else if(a.getAppointmentType().equals("Staff Meeting")){
                staffMeeting++;
            }else if(a.getAppointmentType().equals("Audition")){
                audition++;
            }else if(a.getAppointmentType().equals("Masterclass")){
                masterclass++;
            }else if(a.getAppointmentType().equals("Music Lesson")){
                musicLesson++;
            }
        }
        ObservableList<Reports.Report1> rList = FXCollections.observableArrayList();
        rList.add(new Reports.Report1("Board Meeting", boardMeeting));
        rList.add(new Reports.Report1("Staff Meeting", staffMeeting));
        rList.add(new Reports.Report1("Audition", audition));
        rList.add(new Reports.Report1("Masterclass", masterclass));
        rList.add(new Reports.Report1("Music Lesson", musicLesson));
        T1AppointmentTypesReportTable.setItems(rList);
        T1AppTypeCol.setCellValueFactory(new PropertyValueFactory<>("appointmentType"));
        T1CountCol.setCellValueFactory(new PropertyValueFactory<>("appointmentCount"));

    }

    /**
     * This hashmap pairs up months with their equivalent integer so the next method can return an integer for a monthName.
     */
    private Map<String, Integer> monthMap = new HashMap<>(){
        {
            put("January", 1);
            put("February", 2);
            put("March", 3);
            put("April", 4);
            put("May", 5);
            put("June", 6);
            put("July", 7);
            put("August", 8);
            put("September", 9);
            put("October", 10);
            put("November", 11);
            put("December", 12);
        }
    };


    /**
     * This method changes the monthName string to an integer to be used in the previous method.
     * @param monthName the name of the month
     * @return the integer associated with the month
     */

    public int getMonthInt(String monthName) {
        return monthMap.get(monthName);
    }

    /*
    public int getMonthInt(String monthName){
        switch(monthName) {
            case "January":
                return 1;
            case "February":
                return 2;
            case "March":
                return 3;
            case "April":
                return 4;
            case "May":
                return 5;
            case "June":
                return 6;
            case "July":
                return 7;
            case "August":
                return 8;
            case "September":
                return 9;
            case "October":
                return 10;
            case "November":
                return 11;
            case "December":
                return 12;
        } return 0;
    }*/

    /**
     * This method controls the Customer Schedule report.
     * @param event when the user chooses a customer from the comboBox
     */
    @FXML
    void OnActionGetCustomerSchedule(ActionEvent event) {
        Customer customer = CustomersComboBox.getValue();
        int customerId = customer.getCustomerId();
        //Populate Customers Schedule Table
        T3IDCol1.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        T3TitleCol.setCellValueFactory(new PropertyValueFactory<>("appointmentTitle"));
        T3TypeCol.setCellValueFactory(new PropertyValueFactory<>("appointmentType"));
        T3DescriptionCol1.setCellValueFactory(new PropertyValueFactory<>("description"));
        T3CustIDCol.setCellValueFactory(new PropertyValueFactory<>("customerID"));//change this column to be more informative
        T3DateTimeCol.setCellValueFactory(new PropertyValueFactory<>("appointmentStart"));
        T3CustomerScheduleTable.setItems(dbQuery.getAppointmentsPerCustomer(customerId));
    }


    /**
     * This method populates the 4 comboBoxes on the main screen so the user can select choices.
      * @param url url
     * @param resourceBundle rb
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> allMonths = FXCollections.observableArrayList(Arrays.asList("January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"));

        try {
            ObservableList<Customer> allCustomers = dbQuery.getAllCustomers();
            ObservableList<Contacts> allContacts = dbQuery.getAllContacts();
            ObservableList<User> allUsers = dbQuery.getAllUsers();
            CustomersComboBox.setItems(allCustomers);
            ContactsComboBox.setItems(allContacts);
            ConsultantsComboBox.setItems(allUsers);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        MonthsComboBox.setItems(allMonths);
    }
}
