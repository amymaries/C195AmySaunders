package Model;

/**
 * The countries class models Country objects.
 */
public class Countries {

    private String country;
    private int countryID;

    /**
     * The constructor for Countries creates new Country objects.
     * @param country string name of country
     * @param countryID int country id
     */
    public Countries(String country, int countryID) {
        this.country = country;
        this.countryID = countryID;
    }

    /**
     * @return country name
     */
    public String getCountry() {
        return this.country;
    }

    /**
     * @return int country id
     */
    public int getCountryID() {
        return countryID;
    }

    /**
     * Overrides the standard toString format and provides a user-readable format
     * @return country
     */
    @Override
    public String toString() {
        return country;
    }
}
