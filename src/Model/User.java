package Model;

/**
 * @author ASaunders
 */

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * The user class is used to model user objects from the DB.
 */

public class User {

   private int userID;
   private String userName;
   private String userPassword;
   private LocalDateTime createDate;
   private String createdBy;
   private Timestamp lastUpdate;
   private String lastUpdatedBy;


    /**
     * The user constructor creates new user objects
     * @param userID int user id
     * @param userName string user name
     * @param userPassword string user password
     */
    public User(int userID, String userName, String userPassword) {
        this.userID = userID;
        this.userName = userName;
        this.userPassword = userPassword;
    }


    /**
     * Overloaded constructor
     * @param userId user id
     * @param userName username
     */
    public User(int userId, String userName) {
        this.userID = userId;
        this.userName = userName;
    }

    /**
     * default constructor
     */
    public User() {

    }

    /**
     * @return int user id
     */
    public int getUserID() {
        return userID;
    }

    /**
     * @param userID sets userId
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }

    /**
     * @return string username
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName string userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Override toString method to make it more user-readable
     * @return userId and userName in a string
     */
    @Override
    public String toString() {
        return "[" + userID + "] " + userName;
    }

}
