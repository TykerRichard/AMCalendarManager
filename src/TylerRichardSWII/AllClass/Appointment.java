package TylerRichardSWII.AllClass;

import javafx.beans.Observable;
import javafx.collections.ObservableList;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


/**
 * Appointment Class is used to create appointment objects
 */
public class Appointment {
    private int appointmentID;
    private String title;
    private String description;
    private String type;
    private String location;
    private ZonedDateTime startTime;
    private ZonedDateTime endTime;
    private Integer customerID;
    private Integer contactID;
    private String contactName;
    private String startDateString;
    private String endDateString;
    private Integer userID;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");


    /**
     * @param appointmentID - appointment ID of the appointment
     * @param title - title of the appointment
     * @param description - description of the appointment
     * @param location - location of the appointment
     * @param type - type of appointment
     * @param startTime - start date and time of the appointment
     * @param endTime - end date and time of the appointment
     * @param customerID - Customer ID that is associated with the appointment
     * @param contactID - contact ID that is associated with the appointment
     */
    public Appointment(int appointmentID, String title, String description, String location, String type,
                       ZonedDateTime startTime, ZonedDateTime endTime, int customerID, int contactID,int userID) {
        this.appointmentID = appointmentID;
        this.title = title;
        this.description = description;
        this.type = type;
        this.startTime = startTime;
        this.endTime = endTime;
        this.customerID = customerID;
        this.location = location;
        this.contactID = contactID;
        this.userID=userID;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    /**
     * @return the appointmentID associated with the appointment
     */
    public int getAppointmentID() {
        return appointmentID;
    }


    /**
     * @param appointmentID - sets the appointmentID
     */
    public void setAppointmentID(int appointmentID) {
        this.appointmentID = appointmentID;
    }

    /**
     * @return the title associated with the appointment
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title sets the title of the appointment
     */
    public void setTitle(String title) {
        this.title = title;
    }


    /**
     * @return returns the description of the appointment
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description sets the description of the appointment
     */
    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * @return returns the type of appointment
     */
    public String getType() {
        return type;
    }

    /**
     * @param type sets the type of appointment
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return returns the Start Time and Date of the appointment
     */
    public ZonedDateTime getStartTime() {
        return this.startTime;
    }


    /**
     * @param startTime sets the ZonedDateTime of the appointment
     */
    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    /**
     * @return returns the end date and time of the appointment
     */
    public ZonedDateTime getEndTime() {
        return this.endTime;
    }

    /**
     * @param endTime sets ZonedDateTime of the appointment
     */
    public void setEndTime(ZonedDateTime endTime) {
        this.endTime = endTime;
    }


    public void getStartDate() {
        this.startTime.format(formatter);
    }

    /**
     * @return retruns the location of the appointment
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location sets the location of the appointment
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return returns the customer ID of the appointment
     */
    public int getCustomerID() {
        return customerID;
    }

    /**
     * @param customerID sets the appointment ID of the appointment
     */
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    /**
     * @return returns the contact ID of the appointment
     */
    public int getContactID() {
        return contactID;
    }

    /**
     * @param clientID sets the clientId of the appointment
     */
    public void setContactID(int clientID) {
        this.contactID = clientID;
    }

    /**
     *searches the List of contacts and once it finds the contactID that matches the selected Contact then sets the name
     *  @param selectedContactID - brings in the selected contactID
     * @param allContacts brings in a list of all contacts
     *
     */
    public void setContactName(int selectedContactID, ObservableList<Contact> allContacts) {
        for (Contact c : allContacts) {
            if (c.getContactID() == selectedContactID) {
                this.contactName = c.getName();
                break;
            }
        }
    }

    /**
     * @return returns the contact name
     */
    public String getContactName() {
        return this.contactName;
    }

    /**
     * @param time takes in a date and time and then formats said date and time and sets it.
     */
    public void setStartDateString(ZonedDateTime time) {
        this.startDateString = time.format(formatter);
    }

    /**
     * @return returns the formatted string of the date and time
     */
    public String getStartDateString() {
        return this.startDateString;
    }

    /**
     * @param time takes in a ZonedDateTime and formats it and sets the variable
     */
    public void setEndDateString(ZonedDateTime time) {
        this.endDateString = time.format(formatter);
    }

    /**
     * @return returns the formatted string of the ZonedDateTime
     */
    public String getEndDateString() {
        return this.endDateString;
    }


}






