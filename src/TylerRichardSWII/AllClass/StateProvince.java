package TylerRichardSWII.AllClass;

/**
 * Class is to be used with Customer to the State data.
 */
public class StateProvince {
    String Name;
    int CountryID;
    int DivisionID;

    /**
     * @param name name of the state or providence
     * @param divisionID id of the state or providence
     * @param countryID - country id that the providence is in
     */
    public StateProvince(String name, int divisionID, int countryID) {
        Name = name;
        CountryID = countryID;
        DivisionID = divisionID;
    }

    /**
     * @return returns the name of the state or providence
     */
    public String getName() {
        return Name;
    }

    /**
     * @param name sets the name of the state or providence
     */
    public void setName(String name) {
        Name = name;
    }

    /**
     * @return returns the countryID that the state or providence is in
     */
    public int getCountryID() {
        return CountryID;
    }

    /**
     * @param countryID sets the countryID that the state or providence is in
     */
    public void setCountryID(int countryID) {
        CountryID = countryID;
    }

    /**
     * @return returns the Division id of the state or providence
     */
    public int getDivisionID() {
        return DivisionID;
    }

    /**
     * @param divisionID sets the divisiob id of the state or providence
     */
    public void setDivisionID(int divisionID) {
        DivisionID = divisionID;
    }


}

