package Controller;

/**
 * @author ASaunders
 */

import Model.Countries;
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
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * This controller controls the Add Customer screen, which allows users to add customers.
 */

public class AddCustomerController implements Initializable {

    Stage stage;
    Parent scene;
    DBQuery dbQuery = new DBQuery();



    @FXML
    private TextField AddCustomerNameTextField;

    @FXML
    private TextField AddCustAddressTextField;

    @FXML
    private ComboBox<First_Level_Division> AddCustStateComboBox;

    @FXML
    private ComboBox<Countries> AddCustCountryComboBox;

    @FXML
    private TextField AddCustPostalCodeTextField;

    @FXML
    private TextField AddCustPhoneNumberTextField;

    @FXML
    private Label AddCustMessageLabel;

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

/**onActionSave method is used to save user-entered information which adds an appointment to the scheduler.
 @param event user clicks the 'Save' button on the Add Appointment Controller form.
 @throws IOException handled with try/catch Number Format exception e.
 @throws SQLException possible
 */
    @FXML
    void OnActionSave(ActionEvent event) throws IOException, SQLException {
        //ObservableList<Customer> allCustomers = dbQuery.getAllCustomers();
        try {
            if(!AddCustomerNameTextField.getText().isEmpty() && !AddCustAddressTextField.getText().isEmpty() && !AddCustPostalCodeTextField.getText().isEmpty() && !AddCustPhoneNumberTextField.getText().isEmpty() && AddCustStateComboBox.getValue() != null && AddCustCountryComboBox.getValue() != null){
                //Use GetGeneratedKey from DBQuery
                String customerName = AddCustomerNameTextField.getText();
                String customerAddress = AddCustAddressTextField.getText();
                //Get customerCity from the PostalCode?
                First_Level_Division customerFLD = AddCustStateComboBox.getValue();
                String customerPostalCode= AddCustPostalCodeTextField.getText();
                String customerPhone = AddCustPhoneNumberTextField.getText();
                //grab Division_ID. create a method to grab the string from combo box and convert it to the int ID
                int intCustomerFLD = dbQuery.getCustomerFLD(customerFLD);//use customerFLD input from comboBox to get intCustomerFLD
                //Customer customer = new Customer(customerName, customerAddress, customerPostalCode, customerPhone, intCustomerFLD);
                dbQuery.createCustomer(customerName, customerAddress, customerPostalCode, customerPhone, intCustomerFLD);
                //allCustomers.add(customer);
                AddCustMessageLabel.setText(customerName + " successfully added.");

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Congratulations! Your customer was added.");
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
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
        }
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
        int countryID = AddCustCountryComboBox.getSelectionModel().getSelectedItem().getCountryID();
        ObservableList<First_Level_Division> fldList = allStates.filtered(fld -> {
            if(fld.getCountryID()==countryID)
                return true;
            return false;
        });
        AddCustStateComboBox.setItems(fldList);
    }


    /**
     * initialize method called when Add Customer screen is loaded. It populates comboBoxes
     * with options for creating new customer objects.
     * @param url required
     * @param resourceBundle required
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //add values to comboBoxes
        ObservableList<Countries> countries = null;
        ObservableList<First_Level_Division> allStates = null;

        try {
            countries = dbQuery.getAllCountries();

            allStates = dbQuery.getAllStates();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        AddCustCountryComboBox.setItems(countries);
        AddCustStateComboBox.setItems(allStates);
   }
}
