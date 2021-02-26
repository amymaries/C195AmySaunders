package Utils;

import Model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * This class contains all the necessary DB queries.*/

public class DBQuery {

    private PreparedStatement preparedStatement;


    /***
     * setPreparedStatement method creates a statement object whose job is to receive a connection.
     * @param conn the connection
     * @param sqlStatement the sql statement
     * @throws SQLException possible
     */
    public void setPreparedStatement(Connection conn, String sqlStatement) throws SQLException {
        preparedStatement = conn.prepareStatement(sqlStatement);
    }


    /**
     * getCustomerFLD method uses Division (the State/Province string) from FLD table
     * to find and return the int Division_ID, which is used to saveCustomers in AddCustomerController.
     * @param customerFLD the State/Province name (String) from the comboBox
     * @return int Division_ID
     */
    public int getCustomerFLD(First_Level_Division customerFLD) {
        int division_ID = 0;
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            String sqlc = "SELECT Division_ID FROM first_level_divisions WHERE Division = '" + customerFLD + "'";
            ResultSet resultSet = statement.executeQuery(sqlc);

            while (resultSet.next()) {
                division_ID= resultSet.getInt("Division_ID");
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return division_ID;
    }


    /**
     * getCustomer method uses the customerID to return a customerName
     * @param customerId customerID
     * @return customer
     */
    public Customer getCustomerName(int customerId) {
        try{
            Statement statement = DBConnection.getConnection().createStatement();
            String customerQuery = "SELECT * FROM customers JOIN first_level_divisions ON customers.Division_ID = first_level_divisions.Division_ID JOIN countries ON first_level_divisions.Country_ID = countries.Country_ID WHERE Customer_ID='" + customerId + "'";
            ResultSet resultSet = statement.executeQuery(customerQuery);

            while (resultSet.next()) {
                String customerName = resultSet.getString("Customer_Name");
                String address = resultSet.getString("Address");
                String postalCode = resultSet.getString("Postal_Code");
                String phone = resultSet.getString("Phone");
                int divisionId = resultSet.getInt("Division_ID");
                String division = resultSet.getString("Division");
                String country = resultSet.getString("Country");
                Customer customer = new Customer(customerId, customerName, address, postalCode, phone, divisionId, division, country);

                statement.close();
                return customer;
            }
        } catch (SQLException e){
            e.printStackTrace();
        } return null;
    }

    /**
     * this method uses the string username to return a user object
     * @param username username parameter from method
     * @return customer
     */
    public User getUserfromUsername(String username){
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            String query = "SELECT * FROM users WHERE User_Name='" + username + "'";
            ResultSet resultSet = statement.executeQuery(query);
            if(resultSet.next()){
                User user  = new User();
                user.setUserName(resultSet.getString("User_Name"));
                user.setUserID(resultSet.getInt("User_ID"));
                statement.close();
                return user;
            }
        } catch (SQLException e){
            e.printStackTrace();
        } return null;
    }

    /**
     * Magical method to get all Customers from the DB
     * @return Observable list of all customers
     * @throws SQLException possible
     */
    public ObservableList<Customer> getAllCustomers() throws SQLException {
        ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
        try{
            String allCustomersQuery = "SELECT * FROM customers JOIN first_level_divisions ON customers.Division_ID = first_level_divisions.Division_ID JOIN countries ON first_level_divisions.Country_ID = countries.Country_ID";
            PreparedStatement ps = DBConnection.getConnection().prepareStatement(allCustomersQuery);
            ResultSet resultSet = ps.executeQuery(allCustomersQuery);
            //work through result set, one row at a time.
            while (resultSet.next()) {
                int customerId = resultSet.getInt("Customer_ID");
                String customerName = resultSet.getString("Customer_Name");
                String address = resultSet.getString("Address");
                String postalCode = resultSet.getString("Postal_Code");
                String phone = resultSet.getString("Phone");
                int divisionId = resultSet.getInt("Division_ID");
                String division = resultSet.getString("Division");
                String country = resultSet.getString("Country");
                //create the new customer
                Customer customer = new Customer(customerId, customerName, address, postalCode, phone, divisionId, division, country);
                //add new customer to the list
                allCustomers.add(customer);
            }
            //preparedStatement.close();
            return allCustomers;
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method uses customers name and id to return an observableList of customers
     * @return observableList of customer objects
     * @throws SQLException possible
     */
    public ObservableList<Customer> getCustomersNameID() throws SQLException {
        ObservableList<Customer> allCustomersNameID = FXCollections.observableArrayList();
        String users = "SELECT * FROM customers JOIN first_level_divisions ON customers.Division_ID = first_level_divisions.Division_ID JOIN countries ON first_level_divisions.Country_ID = countries.Country_ID";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(users);
        ResultSet resultSet = ps.executeQuery(users);
        while(resultSet.next()) {
            allCustomersNameID.add(new Customer(resultSet.getInt("Customer_ID"),
                    resultSet.getString("Customer_Name"),
                    resultSet.getString("Address"),
                    resultSet.getString("Postal_Code"),
                    resultSet.getString("Phone"),
                    resultSet.getInt("Division_ID"),
                    resultSet.getString("Division"),
                    resultSet.getString("Country")));
        }
        ps.close();
        return allCustomersNameID;
    }

    /**
     * This method creates new customer objects and saves them to the DB, returning the auto-generated customer id.
     * @param Customer_Name string customer name
     * @param Address string customer address
     * @param Postal_Code string postal code
     * @param Phone string customer phone number
     * @param Division_ID int id of the customer's first-level-division (state/province)
     * @return auto-generated customer id
     */
    public int createCustomer(String Customer_Name, String Address, String Postal_Code, String Phone, int Division_ID) {
        int Customer_ID = 0;
        try {
            String sqlcc = "INSERT INTO customers VALUES (NULL, ?, ?, ?, ?, NULL, NULL, NULL, NULL, ?)";
            //create a PreparedStatement
            PreparedStatement pscc = DBConnection.getConnection().prepareStatement(sqlcc, Statement.RETURN_GENERATED_KEYS);
            //fill in question marks
            pscc.setString(1, Customer_Name);
            pscc.setString(2, Address);
            pscc.setString(3, Postal_Code);
            pscc.setString(4, Phone);
            pscc.setInt(5, Division_ID);
            //execute preparedStatement
            pscc.execute(); //saves the entry into the  customers table in DB
            //getGeneratedKey
            ResultSet rs = pscc.getGeneratedKeys();
            rs.next();//bump it up one so it's index is 1
            Customer_ID = rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    return Customer_ID;
    }


    /**
     * This method updates an existing customer object
     * @param Customer_Name string customer name
     * @param Address string customer address
     * @param Postal_Code string postal code
     * @param Phone string customer phone number
     * @param Division_ID firs-level-division id
     * @param Customer_ID customer id
     */
    public void updateCustomer(String Customer_Name, String Address, String Postal_Code, String Phone, int Division_ID, int Customer_ID){
        try{
            String sqlUpdateCust = "UPDATE customers SET Customer_Name=?, Address=?, Postal_Code=?, Phone=?, Division_ID=? WHERE Customer_ID= ?";
            PreparedStatement psuc = DBConnection.getConnection().prepareStatement(sqlUpdateCust);
            //fill in question marks
            psuc.setString(1, Customer_Name);
            psuc.setString(2, Address);
            psuc.setString(3, Postal_Code);
            psuc.setString(4, Phone);
            psuc.setInt(5, Division_ID);
            psuc.setInt(6, Customer_ID);
            //execute Query
            psuc.execute();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * This method gets all the first-level-divisions(states/provinces)
     * @return observableList of fld's
     * @throws SQLException possible
     */
    public ObservableList<First_Level_Division> getAllStates() throws SQLException {
        ObservableList<First_Level_Division> allStates = FXCollections.observableArrayList();
        String states = "SELECT * FROM first_level_divisions";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(states);
        ResultSet resultSet = ps.executeQuery(states);
        while(resultSet.next()){
            allStates.add(new First_Level_Division(resultSet.getInt("Country_ID"), resultSet.getString("Division")));
        }
        ps.close();
        return allStates;
    }

    /**
     * This method gets all countries
     * @return observableList of all Countries
     * @throws SQLException possible
     */
    public ObservableList<Countries> getAllCountries() throws SQLException {
        ObservableList<Countries> allCountries = FXCollections.observableArrayList();
        String countries = "SELECT * FROM countries";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(countries);
        ResultSet resultSet = ps.executeQuery(countries);
        while(resultSet.next()) {
            allCountries.add(new Countries(resultSet.getString("Country"), resultSet.getInt("Country_ID")));
        }
        ps.close();
        return allCountries;
    }

    /**
     * This method gets all Contact objects
     * @return observableList of all contacts
     * @throws SQLException possible
     */
    public ObservableList<Contacts> getAllContacts() throws SQLException {
        ObservableList<Contacts> allContacts = FXCollections.observableArrayList();
        String contacts = "SELECT * FROM contacts";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(contacts);
        ResultSet resultSet = ps.executeQuery(contacts);
        while(resultSet.next()) {
            allContacts.add(new Contacts(resultSet.getInt("Contact_ID"), resultSet.getString("Contact_Name"), resultSet.getString("Email")));
        }
        ps.close();
        return allContacts;
    }

    /**
     * This method gets all User objects
     * @return observableList of all users
     * @throws SQLException possible
     */
    public ObservableList<User> getAllUsers() throws SQLException {
        ObservableList<User> allUsers = FXCollections.observableArrayList();
        String users = "SELECT * FROM users";
        PreparedStatement ps = DBConnection.getConnection().prepareStatement(users);
        ResultSet resultSet = ps.executeQuery(users);
        while(resultSet.next()) {
            allUsers.add(new User(resultSet.getInt("User_ID"), resultSet.getString("User_Name")));
        }
        ps.close();
        return allUsers;
    }

    /**
     * This method deletes a customer, first deleting appointments associated with customer, then customer
     * @param customerId the id of the customer to delete
     */
    public void deleteCustomer(int customerId){ //parameter is in both tables
        //delete all associated appointments FIRST, then the customer
        try {
            //mySQL statement deleting appointments first
            String sqlappt = "DELETE FROM appointments WHERE Customer_ID=?";
            PreparedStatement pst1 = DBConnection.getConnection().prepareStatement(sqlappt);
            pst1.setInt(1, customerId);
            pst1.execute();
            //mSql statement deleting customer
            String sqlCust = "DELETE FROM customers WHERE Customer_ID=?";
            PreparedStatement pst2 = DBConnection.getConnection().prepareStatement(sqlCust);
            pst2.setInt(1, customerId);
            pst2.execute();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    /**
     * This method deletes a selected appointment
     * @param appointmentId the id of the appointment to delete
     */
    public void deleteAppointment(int appointmentId){ //parameter is in both tables
        try {
            String sqlappt = "DELETE FROM appointments WHERE Appointment_ID=?";
            PreparedStatement pst1 = DBConnection.getConnection().prepareStatement(sqlappt);
            pst1.setInt(1, appointmentId);
            pst1.execute();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    /**
     * This method creates an appointment
     * @param Title appointment title
     * @param Description description
     * @param Location location
     * @param Type type
     * @param Start appointment date/time start
     * @param End appointment date/time end
     * @param customerId customer id
     * @param userId user id
     * @param contactId contact id
     * @return the auto-generated appointment id of the newly created appointment
     */
    public int createAppointment(String Title, String Description, String Location, String Type,
                                 LocalDateTime Start, LocalDateTime End, int customerId, int userId, int contactId) {

        int appointmentId = 0;
        try { //Cannot add or update a child row: a foreign key constraint fails
            String sqlca = "INSERT INTO appointments(Title, Description, Location, Type, Start, End, Customer_ID, User_ID, Contact_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

            //create a PreparedStatement
            PreparedStatement psca = DBConnection.getConnection().prepareStatement(sqlca, Statement.RETURN_GENERATED_KEYS);
            //fill in question marks
            psca.setString(1, Title);
            psca.setString(2, Description);
            psca.setString(3, Location);
            psca.setString(4, Type);
            psca.setTimestamp(5, Timestamp.valueOf(Start));
            psca.setTimestamp(6, Timestamp.valueOf(End));
            psca.setInt(7, customerId);
            psca.setInt(8, userId);
            psca.setInt(9, contactId);
            //execute preparedStatement
            psca.execute(); //saves the entry into the  customers table in DB
            //getGeneratedKey
            ResultSet rs = psca.getGeneratedKeys();
            rs.next();//bump it up one so it's index is 1
            appointmentId = rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        } return appointmentId;
    }

    /**
     * This method updates a user-selected appointment
     * @param appointmentId selected appointment id
     * @param appointmentTitle title
     * @param description description
     * @param location location
     * @param appointmentType type
     * @param appointmentStartTime start date/time
     * @param appointmentEndTime end date/time
     * @param customerId customer id
     * @param userId user id
     * @param contactId contact id
     * @throws SQLException possible
     */
    public void updateAppointment(int appointmentId, String appointmentTitle, String description, String location, String appointmentType,
                      LocalDateTime appointmentStartTime, LocalDateTime appointmentEndTime, int customerId, int userId, int contactId) throws SQLException {
        String updateStatement = "UPDATE appointments SET Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?";

        PreparedStatement ps = DBConnection.getConnection().prepareStatement(updateStatement);

        ps.setString(1, appointmentTitle);
        ps.setString(2, description);
        ps.setString(3, location);
        ps.setString(4, appointmentType);
        ps.setTimestamp(5, Timestamp.valueOf(appointmentStartTime));
        ps.setTimestamp(6, Timestamp.valueOf(appointmentEndTime));
        ps.setInt(7, customerId);
        ps.setInt(8, userId);
        ps.setInt(9, contactId);
        ps.setInt(10, appointmentId);

        ps.execute();
    }

    /**
     * This method retrieves all appointment objects from DB
     * @return observableList of appointments
     */
    public ObservableList<Appointment> getAllAppointments(){
        ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
        try { //int appointmentID, String appointmentTitle, String description, String location, String appointmentType, LocalDateTime appointmentStart, LocalDateTime appointmentEnd, LocalDate date, int customerID, String customerName, int userID, String userName, int contactID, String contactName) {

            String query = "SELECT Appointment_ID, Title, Description, Location, Type, Start, End, appointments.Customer_ID, Customer_Name, appointments.Contact_ID, Contact_Name, appointments.User_ID, User_Name FROM appointments, customers, contacts, users WHERE customers.Customer_ID=appointments.Customer_ID AND contacts.Contact_ID=appointments.Contact_ID AND users.User_ID=appointments.User_ID";
            PreparedStatement preparedStatement = DBConnection.getConnection().prepareStatement(query);
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.getResultSet();
            while (resultSet.next()){
                allAppointments.add(new Appointment(resultSet.getInt("Appointment_ID"),
                        resultSet.getString("Title"),
                        resultSet.getString("Description"),
                        resultSet.getString("Location"),
                        resultSet.getString("Type"),
                        resultSet.getTimestamp("Start").toLocalDateTime(),
                        resultSet.getTimestamp("End").toLocalDateTime(),
                        resultSet.getInt("Customer_ID"),
                        resultSet.getString("Customer_Name"),
                        resultSet.getInt("User_ID"),
                        resultSet.getString("User_Name"),
                        resultSet.getInt("Contact_ID"),
                        resultSet.getString("Contact_Name")));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } return allAppointments;
    }

    /**
     * This method retrieves all appointments during the month in which user searches. Not appointments for the next 30 days
     * but appointments for the month in which search takes place
     * @return observableList of appointments during search month
     */
    public ObservableList<Appointment> getMonthlyAppointments() {
        int month = LocalDate.now().getMonthValue();
        ObservableList<Appointment> monthlyAppointments = FXCollections.observableArrayList();
        try {
            String query = "SELECT Appointment_ID, Title, Description, Location, Type, Start, End, appointments.Customer_ID, Customer_Name, appointments.Contact_ID, Contact_Name, appointments.User_ID, User_Name FROM appointments, customers, contacts, users WHERE customers.Customer_ID=appointments.Customer_ID AND contacts.Contact_ID=appointments.Contact_ID AND users.User_ID=appointments.User_ID AND month(start) = ?";
            PreparedStatement statement = DBConnection.getConnection().prepareStatement(query);
            statement.setInt(1, month);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                monthlyAppointments.add(new Appointment(resultSet.getInt("Appointment_ID"),
                        resultSet.getString("Title"),
                        resultSet.getString("Description"),
                        resultSet.getString("Location"),
                        resultSet.getString("Type"),
                        resultSet.getTimestamp("Start").toLocalDateTime(),
                        resultSet.getTimestamp("End").toLocalDateTime(),
                        resultSet.getInt("Customer_ID"),
                        resultSet.getString("Customer_Name"),
                        resultSet.getInt("User_ID"),
                        resultSet.getString("User_Name"),
                        resultSet.getInt("Contact_ID"),
                        resultSet.getString("Contact_Name")));
            }
            statement.close();
            return monthlyAppointments;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method retrieves all appointments during the week in which user searches. Not appointments for the next 7 days
     * but appointments for the remainder of the week in which search takes place
     * @return observableList of appointments during search week
     */
    public ObservableList<Appointment> getWeeklyAppoinments() {
        LocalDateTime start = LocalDateTime.now();
        while(start.getDayOfWeek()!= DayOfWeek.SUNDAY){
            start = start.minusDays(1);
        }
        Timestamp startTS = Timestamp.valueOf(start);
        Timestamp endTS = Timestamp.valueOf(start.plusWeeks(1));
        ObservableList<Appointment> weeklyAppointments = FXCollections.observableArrayList();
        try {
            String query = "SELECT Appointment_ID, Title, Description, Location, Type, Start, End, appointments.Customer_ID, Customer_Name, appointments.Contact_ID, Contact_Name, appointments.User_ID, User_Name FROM appointments, customers, contacts, users WHERE customers.Customer_ID=appointments.Customer_ID AND contacts.Contact_ID=appointments.Contact_ID AND users.User_ID=appointments.User_ID AND Start >= ?  AND Start <= ?";
            PreparedStatement statement = DBConnection.getConnection().prepareStatement(query);
            statement.setTimestamp(1, startTS);
            statement.setTimestamp(2, endTS);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                        weeklyAppointments.add(new Appointment(resultSet.getInt("Appointment_ID"),
                        resultSet.getString("Title"),
                        resultSet.getString("Description"),
                        resultSet.getString("Location"),
                        resultSet.getString("Type"),
                        resultSet.getTimestamp("Start").toLocalDateTime(),
                        resultSet.getTimestamp("End").toLocalDateTime(),
                        resultSet.getInt("Customer_ID"),
                        resultSet.getString("Customer_Name"),
                        resultSet.getInt("User_ID"),
                        resultSet.getString("User_Name"),
                        resultSet.getInt("Contact_ID"),
                        resultSet.getString("Contact_Name")));
            }
            statement.close();
            return weeklyAppointments;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method retrieves all appointments for one customer from DB
     * @param customerId customer id for whom appointments are being retrieved
     * @return observableList of all appointments for one customer
     */
    public ObservableList<Appointment> getAppointmentsPerCustomer (int customerId) {
        ObservableList<Appointment> appointmentsPerCustomer = FXCollections.observableArrayList();
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            String query = "SELECT Appointment_ID, Title, Description, Location, Type, Start, End, appointments.Customer_ID, Customer_Name, appointments.Contact_ID, Contact_Name, appointments.User_ID, User_Name FROM appointments, customers, contacts, users WHERE customers.Customer_ID=appointments.Customer_ID AND contacts.Contact_ID=appointments.Contact_ID AND appointments.User_ID=users.User_ID AND appointments.Customer_ID = '" + customerId + "'";
            //String query = "SELECT * FROM appointments WHERE Customer_ID = '" + customerId + "'";
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()){
                appointmentsPerCustomer.add(new Appointment(resultSet.getInt("Appointment_ID"),
                        resultSet.getString("Title"),
                        resultSet.getString("Description"),
                        resultSet.getString("Location"),
                        resultSet.getString("Type"),
                        resultSet.getTimestamp("Start").toLocalDateTime(),
                        resultSet.getTimestamp("End").toLocalDateTime(),
                        resultSet.getInt("Customer_ID"),
                        resultSet.getString("Customer_Name"),
                        resultSet.getInt("User_ID"),
                        resultSet.getString("User_Name"),
                        resultSet.getInt("Contact_ID"),
                        resultSet.getString("Contact_Name")));
            }
            statement.close();
            return appointmentsPerCustomer;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method retrieves all appointments for a single contact from DB
     * @param contactId the id of the contact for whom appointments are being retrieved
     * @return observableList of appointments
     */
    public ObservableList<Appointment> getAppointmentsPerContact (int contactId) {
        ObservableList<Appointment> contactAppointments = FXCollections.observableArrayList();
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            String query = "SELECT Appointment_ID, Title, Description, Location, Type, Start, End, appointments.Customer_ID, Customer_Name, appointments.Contact_ID, Contact_Name, appointments.User_ID, User_Name FROM appointments, customers, contacts, users WHERE customers.Customer_ID=appointments.Customer_ID AND contacts.Contact_ID=appointments.Contact_ID AND appointments.User_ID=users.User_ID AND appointments.Contact_ID='" + contactId + "'";
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()){
                contactAppointments.add(new Appointment(resultSet.getInt("Appointment_ID"),
                        resultSet.getString("Title"),
                        resultSet.getString("Description"),
                        resultSet.getString("Location"),
                        resultSet.getString("Type"),
                        resultSet.getTimestamp("Start").toLocalDateTime(),
                        resultSet.getTimestamp("End").toLocalDateTime(),
                        resultSet.getInt("Customer_ID"),
                        resultSet.getString("Customer_Name"),
                        resultSet.getInt("User_ID"),
                        resultSet.getString("User_Name"),
                        resultSet.getInt("Contact_ID"),
                        resultSet.getString("Contact_Name")));
            }
            statement.close();
            return contactAppointments;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * This method retrieves all appointments for a particular user from DB
     * @param userId id of user for whom appointments are being retrieved
     * @return observableList of appointments
     */
    public ObservableList<Appointment> getAppointmentsPerUser (int userId) {
        ObservableList<Appointment> userAppointments = FXCollections.observableArrayList();
        try {
            Statement statement = DBConnection.getConnection().createStatement();
            String query = "SELECT Appointment_ID, Title, Description, Location, Type, Start, End, appointments.Customer_ID, Customer_Name, appointments.Contact_ID, Contact_Name, appointments.User_ID, User_Name FROM appointments, customers, contacts, users WHERE customers.Customer_ID=appointments.Customer_ID AND contacts.Contact_ID=appointments.Contact_ID AND appointments.User_ID=users.User_ID AND appointments.User_ID='" + userId + "'";
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()){
                userAppointments.add(new Appointment(resultSet.getInt("Appointment_ID"),
                        resultSet.getString("Title"),
                        resultSet.getString("Description"),
                        resultSet.getString("Location"),
                        resultSet.getString("Type"),
                        resultSet.getTimestamp("Start").toLocalDateTime(),
                        resultSet.getTimestamp("End").toLocalDateTime(),
                        resultSet.getInt("Customer_ID"),
                        resultSet.getString("Customer_Name"),
                        resultSet.getInt("User_ID"),
                        resultSet.getString("User_Name"),
                        resultSet.getInt("Contact_ID"),
                        resultSet.getString("Contact_Name")));
            }
            statement.close();
            return userAppointments;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * This method retrieves all appointments for a single month in order to filter by appointmentType and use in Report1
     * @param month the month of the search
     * @return observableList of appointments
     */
    public ObservableList<Appointment> getAppointmentsByMonth (int month) {
        ObservableList<Appointment> appointmentsByMonth = FXCollections.observableArrayList();
        try {
            String query = "SELECT Appointment_ID, Title, Description, Location, Type, Start, End, appointments.Customer_ID, Customer_Name, appointments.Contact_ID, Contact_Name, appointments.User_ID, User_Name FROM appointments, customers, contacts, users WHERE customers.Customer_ID=appointments.Customer_ID AND contacts.Contact_ID=appointments.Contact_ID AND users.User_ID=appointments.User_ID AND month(start) = ?";
            PreparedStatement statement = DBConnection.getConnection().prepareStatement(query);
            statement.setInt(1, month);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                appointmentsByMonth.add(new Appointment(resultSet.getInt("Appointment_ID"),
                        resultSet.getString("Title"),
                        resultSet.getString("Description"),
                        resultSet.getString("Location"),
                        resultSet.getString("Type"),
                        resultSet.getTimestamp("Start").toLocalDateTime(),
                        resultSet.getTimestamp("End").toLocalDateTime(),
                        resultSet.getInt("Customer_ID"),
                        resultSet.getString("Customer_Name"),
                        resultSet.getInt("User_ID"),
                        resultSet.getString("User_Name"),
                        resultSet.getInt("Contact_ID"),
                        resultSet.getString("Contact_Name")));
            }
            statement.close();
            return appointmentsByMonth;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
