package Model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author ASaunders
 */
import Utils.DBQuery;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.SQLException;
import java.time.*;

/**
 * The Appointment class is used for modeling
 * appointment objects from the database
 */
public class Appointment {

    private int appointmentID;
    private String appointmentTitle;
    private String description;
    private String location;
    private String appointmentType;
    private LocalDateTime appointmentStart;
    private LocalDateTime appointmentEnd;
    private LocalDate date;
    private int customerID;
    private String customerName;
    private int userID;
    private String userName;
    private int contactID;
    private String contactName;

    /**
     * The appointment constructor creates a new appointment.
     * @param appointmentID int value for appointment id
     * @param appointmentTitle string value for appointment title
     * @param description string description of appointment
     * @param location string location of appointment
     * @param appointmentType string type of appointment
     * @param appointmentStart LocalDateTime object sets appointment start
     * @param appointmentEnd LocalDateTime object sets appointment end
     * @param customerID int value for customer id
     * @param customerName string customer name
     * @param userID int value for user id
     * @param userName string user name
     * @param contactID int value for contact id
     * @param contactName string contact name
     */

    public Appointment(int appointmentID, String appointmentTitle, String description, String location, String appointmentType, LocalDateTime appointmentStart, LocalDateTime appointmentEnd, int customerID, String customerName, int userID, String userName, int contactID, String contactName) {
        this.appointmentID = appointmentID;
        this.appointmentTitle = appointmentTitle;
        this.description = description;
        this.location = location;
        this.appointmentType = appointmentType;
        this.appointmentStart = appointmentStart;
        this.appointmentEnd = appointmentEnd;
        this.date = this.appointmentStart.toLocalDate();
        this.customerID = customerID;
        this.customerName = customerName;
        this.userID = userID;
        this.userName = userName;
        this.contactID = contactID;
        this.contactName = contactName;
    }

    /**
     * @return current appointment ID as int
     */
    public int getAppointmentID() {
        return appointmentID;
    }


    /**
     * @return customer ID as int
     */
    public int getCustomerID() {
        return customerID;
    }


    /**
     * @return user ID as int
     */
    public int getUserID() {
        return userID;
    }

    /**
     * @param userID user ID to set
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }

    /**
     * @return appointment title as string
     */
    public String getAppointmentTitle() {
        return appointmentTitle;
    }


    /**
     * @return appointment description as string
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return appointment location as string
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location appointment location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return int contact ID
     */
    public int getContactID() {
        return contactID;
    }

    /**
     * @return selected appointment type
     */
    public String getAppointmentType() {
        return appointmentType;
    }

    /**
     * @return date of appointment
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * @param date sets appointment date
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * @return start date/time of appointment
     */
    public LocalDateTime getAppointmentStart() {
        return appointmentStart;
    }

    /**
     * @param appointmentStart start date/time of appointment to set
     */
    public void setAppointmentStart(LocalDateTime appointmentStart) {
        this.appointmentStart = appointmentStart;
    }

    /**
     * @return end date/time of appointment
     */
    public LocalDateTime getAppointmentEnd() {
        return appointmentEnd;
    }

    /**
     * @return current appointment contact name as String
     */
    public String getContactName() {
        return contactName;
    }

    /**
     * @param contactName contactName to set
     */
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    /**
     * This method is called by the Add Appointment and Modify Appointment classes.
     * It checks for conflicts with existing appointments before allowing users to create/update appointments.
     * @param customerID the id of the customer for whom the appointment is being made
     * @param appointmentStart date/time start of the proposed appointment
     * @param appointmentEnd date/time end of the proposed appointment
     * @return any possibly conflicting appointment
     * @throws SQLException possible exception
     */
    public static Appointment scheduleOverlaps(int customerID, LocalDateTime appointmentStart, LocalDateTime appointmentEnd) throws SQLException {
        Appointment appointmentConflict = null;
        DBQuery dbQuery = new DBQuery();
        ObservableList<Appointment> allCustomerAppointments = dbQuery.getAppointmentsPerCustomer(customerID);

        for (Appointment a : allCustomerAppointments) {
            if(appointmentStart.isAfter(a.getAppointmentStart()) && appointmentStart.isBefore(a.getAppointmentEnd()) ||
            appointmentEnd.isAfter(a.getAppointmentStart()) && appointmentEnd.isBefore(a.getAppointmentEnd()) ||
            appointmentStart.isBefore(a.getAppointmentStart()) && appointmentEnd.isAfter(a.getAppointmentEnd()) ||
            appointmentStart.equals(a.getAppointmentStart()) && appointmentEnd.equals(a.getAppointmentEnd()) ||
            appointmentStart.equals(a.getAppointmentStart()) || appointmentEnd.equals(a.getAppointmentEnd())) {
                appointmentConflict = a;
            }
        }
        return appointmentConflict;
    }


}
