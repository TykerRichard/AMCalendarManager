package TylerRichardSWII.AllClass;

import javafx.scene.control.Alert;

import java.sql.Connection;

/**
 * Customer Class is used to create customer Objects
 */
public class Customer {
    private String name;
    private int customerID;
    private String address;
    private String postalCode;
    private String phoneNumber;
    private int divisionID;


    /**
     * empty constructor
     */
    public Customer() {
    }

    /**
     * @param firstName first name of customer
     * @param lastName last name of customer
     * @param address address of customer
     * @param postalCode postal code of customer
     * @param phoneNumber phone number of customer
     * @param divisionID divisionID of customer
     * @param customerID unique CustomerID
     */
    public Customer(String firstName, String lastName, String address, String postalCode, String phoneNumber, int divisionID, int customerID) {
        this.name = firstName + " " + lastName;
        this.address = address;
        this.customerID = customerID;
        this.postalCode = postalCode;
        this.phoneNumber = phoneNumber;
        this.divisionID = divisionID;

    }

    /**
     * @return returns customer name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name sets customer name
     */
    public void setName(String name) {
        this.name = name;

    }

    /**
     * @param customerID sets customer ID
     */
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    /**
     * @return returns customer ID
     */
    public int getCustomerID() {
        return customerID;
    }


    /**
     * @return returns customer address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address sets customer address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return returns postal code of customer
     */
    public String getPostalCode() {
        return this.postalCode;
    }

    /**
     * @param postalCode sets postal code of customer
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * @return returns the phone number of customer
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * @param phoneNumber sets the phone number of customer
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @return returns the divisionID (state or providence) of a customer
     */
    public int getDivisionID() {
        return divisionID;
    }

    /**
     * @param divisionID sets the divisionID (state or providence) of a customer
     */
    public void setDivisionID(int divisionID) {
        this.divisionID = divisionID;
    }

    /**
     * @return overrides the default toString to return the name of the customer instead of the object
     */
    @Override
    public String toString() {
        return this.name;
    }

}




