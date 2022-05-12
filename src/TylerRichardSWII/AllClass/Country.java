package TylerRichardSWII.AllClass;

import java.sql.*;


/**
 * Country class is used to store country information for creation of Customers
 */
public class Country {
    private String name;
    private int countryID;
    private Connection conn;

    /**
     * @param name - name of the country
     * @param countryID - Id of the country
     */
    public Country(String name, int countryID) {
        this.name = name;
        this.countryID = countryID;

    }

    /**
     * empty constructor
     */
    public Country() {
    }

    /**
     * @return returns the name of the country
     */
    public String getName() {
        return name;
    }

    /**
     * @param name sets the name of the country
     */
    private void setName(String name) {
        this.name = name;
    }

    /**
     * @return returns the countryID
     */
    public int getCountryID() {
        return countryID;

    }

    /**
     * @param countryID sets the country ID.
     */
    private void setCountryID(int countryID) {
        this.countryID = countryID;
    }

}
