package Controller;

/**
 * @author ASaunders
 */

import Model.Countries;
import Model.Customer;
import Model.First_Level_Division;
import Utils.DBQuery;
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
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * This controller controls the ModifyCustomer class.
 */

public class ModifyCustomerController implements Initializable {


    Stage stage;
    Parent scene;
    Customer selectedCustomer;
    DBQuery dbQuery = new DBQuery();




    @FXML
    private TextField CustomerIDTF;

    @FXML
    private TextField CustomerNameTF;

    @FXML
    private TextField AddressTF;

    @FXML
    private ComboBox<First_Level_Division> FLDComboBox;

    @FXML
    private ComboBox<Countries> CountriesComboBox;

    @FXML
    private TextField PostalCodeTF;

    @FXML
    private TextField PhoneNumberTF;

    @FXML
    private Label UpdateCustomerMessageLabel;

    @FXML
    private Label chooseCustomerMessageLabel;

    @FXML
    private TableView<Customer> CustomersTableView;

    @FXML
    private TableColumn<Customer, Integer> CustIDCol;

    @FXML
    private TableColumn<Customer, String> CustNameCol;

    @FXML
    private TableColumn<Customer, String> CustAddressCol;

    @FXML
    private TableColumn<First_Level_Division, String> CustCityCol;

    @FXML
    private TableColumn<Countries, String> CustCountryCol;

    @FXML
    private TableColumn<Customer, String> CustPostalCodeCol;

    /**
     * This method returns user to the main screen
     * @param event cancel button clicked
     * @throws IOException possible
     */
    @FXML
    void OnActionCancel(ActionEvent event) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "This action will return you to the appointment schedule. Would you like to proceed?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource("/View/MainMenuAppointment.fxml"));
            stage.setTitle("Appointment Management System");
            stage.setScene(new Scene(scene));
            stage.show();
        }
    }
    //this method just passes the information on over to the other half of the form where it can be edited and updated

    /**
     * This method gets the selectedCustomer from the customer table so user can update customer information
     * @param event updateCustomer button clicked
     * @throws SQLException possible
     */
    @FXML
    void OnActionUpdateCustomer(ActionEvent event) throws SQLException {
        //show an alert if the user has not selected a particular customer.
        //then auto-populates the Update Customer side of the screen -- it just moves the selected customer from the table over
        if(CustomersTableView.getSelectionModel().getSelectedItem() != null){
            Customer selectedCustomer = CustomersTableView.getSelectionModel().getSelectedItem();
            getSelectedCustomer(selectedCustomer);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a customer to update.");
            alert.showAndWait();
        }
    }


    /**
     * This method transfers all of the selectedCustomer information from the table on one side of the screen
     * to the fields and comboBoxes on the other side of the screen.
     * @param selectedCustomer customer selected to be updated
     * @throws SQLException possible
     */
    public void getSelectedCustomer(Customer selectedCustomer) throws SQLException {
        this.selectedCustomer = selectedCustomer;
        ObservableList<First_Level_Division> allStates = dbQuery.getAllStates();
        ObservableList<Countries> allCountries = dbQuery.getAllCountries();
        for(Countries c : allCountries){
            if(selectedCustomer.getCountry().equals(c.getCountry())){
                CountriesComboBox.getSelectionModel().select(c);
                break;
            }
        }
        for(First_Level_Division fld : allStates){
            if(selectedCustomer.getDivision().equals(fld.getDivision())){
                FLDComboBox.getSelectionModel().select(fld);
                break;
            }
        }
        FLDComboBox.setItems(allStates);
        CountriesComboBox.setItems(allCountries);
        CustomerIDTF.setText(Integer.toString(this.selectedCustomer.getCustomerId()));
        CustomerNameTF.setText(this.selectedCustomer.getCustomerName());
        AddressTF.setText(this.selectedCustomer.getAddress());
        PostalCodeTF.setText(this.selectedCustomer.getPostalCode());
        PhoneNumberTF.setText(this.selectedCustomer.getPhone());
    }



    /**
     * Lambda expression is used here both to filter the list and to increase readability.
     * This method filters the list of FLD's according to the country chosen in the previous comboBox.
     * @param event the selection of an item in the comboBox.
     * @throws SQLException possible
     */
    @FXML
    void OnActionSelectCountry(ActionEvent event) throws SQLException {
        ObservableList<First_Level_Division> allStates = dbQuery.getAllStates();
        int countryID = CountriesComboBox.getSelectionModel().getSelectedItem().getCountryID();
        ObservableList<First_Level_Division> fldList = allStates.filtered(fld -> {
            return fld.getCountryID() == countryID;
        });
        FLDComboBox.setItems(fldList);
    }

    /**
     * The deleteCustomer method allows the user to delete customers. It first deletes all associated appointments,
     * then the customer, then returns the user to the mainScreen.
     * @param event DeleteCustomer button clicked
     * @throws IOException possible exception
     */
    //use queries to FIRST delete all associated appointments, then the customer. Then return to the main screen.
    @FXML
    void OnActionDeleteCustomer (ActionEvent event) throws IOException {
        Customer selectedCustomer = CustomersTableView.getSelectionModel().getSelectedItem();
        int customerId = selectedCustomer.getCustomerId();
        String customerName = selectedCustomer.getCustomerName();

        if(selectedCustomer != null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Deleting a customer will delete his appointments. Would you like to proceed?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                dbQuery.deleteCustomer(customerId);
                UpdateCustomerMessageLabel.setText(customerName + " successfully deleted.");
                alert = new Alert(Alert.AlertType.CONFIRMATION, "Customer was deleted.");
                result = alert.showAndWait();
                if(result.isPresent() && result.get() == ButtonType.OK){
                    stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                    scene = FXMLLoader.load(getClass().getResource("/view/MainMenuAppointment.fxml"));
                    stage.setTitle("Appointment Scheduling System");
                    stage.setScene(new Scene(scene));
                    stage.show();
                }
                }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please select a customer to delete.");
            alert.showAndWait();
        }


    }

    /**
     * This method allows users to update customer information in the DB.
     * @param event Save button clicked
     */
    @FXML
    void OnActionSaveCustomerUpdates (ActionEvent event) {
        //save updates to customer using dbquery.updateCustomer();
        try {
            if(!CustomerNameTF.getText().isEmpty() && !AddressTF.getText().isEmpty() && !PostalCodeTF.getText().isEmpty() && !PhoneNumberTF.getText().isEmpty() && FLDComboBox.getValue() != null && CountriesComboBox.getValue() != null){
                int customerId = Integer.parseInt(CustomerIDTF.getText());
                String customerName = CustomerNameTF.getText();
                String customerAddress = AddressTF.getText();
                First_Level_Division customerFLD = FLDComboBox.getValue();
                String customerPostalCode= PostalCodeTF.getText();
                String customerPhone = PhoneNumberTF.getText();
                int intCustomerFLD = dbQuery.getCustomerFLD(customerFLD);
                dbQuery.updateCustomer(customerName, customerAddress, customerPostalCode, customerPhone, intCustomerFLD, customerId);
                UpdateCustomerMessageLabel.setText(customerName + " successfully updated.");

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Congratulations! Your customer was updated.");
                Optional<ButtonType> result = alert.showAndWait();
                if(result.isPresent() && result.get() == ButtonType.OK){
                    stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                    scene = FXMLLoader.load(getClass().getResource("/view/MainMenuAppointment.fxml"));
                    stage.setTitle("Appointment Scheduling System");
                    stage.setScene(new Scene(scene));
                    stage.show();
                }
            }  else {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Please enter a valid value for each text field.");
                alert.showAndWait();
            }
        } catch (NumberFormatException | IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * The initialize method is run first in any class. This method populates the customers table.
     * @param url url
     * @param resourceBundle rb
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            ObservableList<Customer> allCustomers = dbQuery.getAllCustomers();
            CustomersTableView.setItems(allCustomers);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        //Table and column values for TableView.
        //call getCustomersTableViewData method
        CustIDCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        CustNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        CustAddressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        CustCityCol.setCellValueFactory(new PropertyValueFactory<>("division"));
        CustCountryCol.setCellValueFactory(new PropertyValueFactory<>("country"));
        CustPostalCodeCol.setCellValueFactory(new PropertyValueFactory<>("postalCode"));

    }



}
