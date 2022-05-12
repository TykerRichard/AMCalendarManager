package TylerRichardSWII.AllClass;

/**
 * Contact Class used to hold contact Objects to be used with appointments and reporting
 */
public class Contact {
    private int ContactID;
    private String Name;
    private String Email;

    /**
     * constructor that sets all fields
     * @param contactID - Id of the contact
     * @param name - name of the contact
     * @param email - email of the contact
     */
    public Contact(int contactID, String name, String email) {
        ContactID = contactID;
        Name = name;
        Email = email;
    }

    /**
     * @return returns the contactID
     */
    public int getContactID() {
        return ContactID;
    }

    /**
     * @param contactID sets the contact ID
     */
    public void setContactID(int contactID) {
        ContactID = contactID;
    }

    /**
     * @return returns the name of the contact
     */
    public String getName() {
        return Name;
    }

    /**
     * @param name sets the name of the contact
     */
    public void setName(String name) {
        Name = name;
    }

    /**
     * @return the email address of the contact
     */
    public String getEmail() {
        return Email;
    }

    /**
     * @param email sets the email of the contact
     */
    public void setEmail(String email) {
        Email = email;
    }

    /**
     * @return overrides the default toString method to return the name field instead of the instance of the object
     */
    @Override
    public String toString() {
        return this.Name;
    }
}

