package Model;
/**
 * @author ASaunders
 */

import Utils.DBQuery;
import javafx.collections.ObservableList;
import java.sql.SQLException;

/**
 * This Customer class is used to model customer objects from DB
 */
public class Customer {

    private int customerId;
    private String customerName;
    private String address;
    private String postalCode;
    private String phone;
    private int divisionId;
    private String country;
    private String division;


    /**
     * Customer constructor creates new customer objects.
     * @param customerId int customer id value
     * @param customerName string customer name
     * @param address string address of customer object
     * @param postalCode string postal code of customer object
     * @param phone string phone number
     * @param divisionId int id of first-level-division
     * @param division name of first-level-division
     * @param country string name of country
     */
    public Customer(int customerId, String customerName, String address, String postalCode, String phone, int divisionId, String division, String country) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.address = address;
        this.postalCode = postalCode;
        this.phone = phone;
        this.divisionId = divisionId;
        this.division = division;
        this.country = country;
    }

    /**
     * @return country name
     */
    public String getCountry() {
        return country;
    }

    /**
     * @return first-level-division name
     */
    public String getDivision() {
        return division;
    }

    /**
     * @return customer name
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * @return customer address as string
     */
    public String getAddress() {
        return address;
    }

    /**
     * @return string customer postal code
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * @return customer phone number as string
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone set customer phone number
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Overrides toString method for more user-readable format.
     * @return customer name and id
     */
    @Override
    public String toString() {
        return "[" + customerId + "] " + customerName;
    }

    /**
     * @return customer id as int
     */
    public int getCustomerId() {
        return customerId;
    }

    /**
     * This method is used to get observable list of all customers in DB
     * @return observable list of customers
     * @throws SQLException possible
     */
    public ObservableList<Customer> getAllCustomers() throws SQLException {
        DBQuery dbQuery = new DBQuery();
        ObservableList<Customer> allCustomers = dbQuery.getAllCustomers();
        return allCustomers;
    }
}
