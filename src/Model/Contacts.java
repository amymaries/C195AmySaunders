package Model;

/**
 * This class is used to model Contact objects from the DB.
 */

public class Contacts {

    private int contactID;
    private String contactName;
    private String contactEmail;

    /**
     * The constructor for Contacts creates a new Contact object.
     * @param contactID int sets contact id
     * @param contactName string sets contact name
     * @param contactEmail string sets contact email
     */
    public Contacts(int contactID, String contactName, String contactEmail) {
        this.contactID = contactID;
        this.contactName = contactName;
        this.contactEmail = contactEmail;
    }

    /**
     * @return contact id
     */
    public int getContactID() {
        return contactID;
    }

    /**
     * @return contact name
     */
    public String getContactName() {
        return contactName;
    }

    /**
     * Overrides standard toString method
     * @return string in a user-friendly format
     */
    @Override
    public String toString() {
        return "[" + contactID + "] " + contactName;
    }
}
