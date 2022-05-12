package TylerRichardSWII.Util;

import TylerRichardSWII.AllClass.Appointment;
import TylerRichardSWII.AllClass.Customer;

import java.sql.SQLException;

/**
 * interfaced used by all the controllers for executing DB actions
 */
public interface dbCommands{
    void connectDB() throws SQLException;
    void addCustomer(Customer newCustomer) throws SQLException;
    void getCountries() throws SQLException;
    void getStateOrProvince() throws SQLException;
    boolean validateLogin() throws SQLException;
    void getCustomer() throws SQLException;
    void addAppointment(Appointment newAppointment) throws SQLException;
    void getContacts() throws SQLException;
    void getAppointments() throws SQLException;
    void getUserID() throws SQLException;
    void removeCustomer(Customer customerRemove) throws SQLException;
    void removeAppointments(Appointment appointmentRemove) throws SQLException;
}
