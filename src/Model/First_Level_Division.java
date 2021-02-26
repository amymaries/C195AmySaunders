package Model;

/**
 * @author ASaunders
 */

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * This class models state/province (first-level-division) objects from the DB.
 */
public class First_Level_Division {

    private int divisionID;
    private String division;
    private LocalDateTime createDate;
    private String createdBy;
    private Timestamp lastUpdate;
    private String lastUpdatedBy;
    private int countryID;

    /**
     * FLD constructor creates new FLD objects.
     * @param countryID int country id
     * @param division string division name
     */
    public First_Level_Division(int countryID, String division) {
        this.countryID = countryID;
        this.division = division;
    }

    /**
     * @return division name as string
     */
    public String getDivision() {
        return this.division;
    }

    /**
     * @return int country id
     */
    public int getCountryID() {
        return countryID;
    }

    /**
     * Overrides toString method for better formatting
     * @return division name
     */
    @Override
    public String toString() {
        return division;
    }

}
