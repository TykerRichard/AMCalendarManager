package TylerRichardSWII.Controllers;

import TylerRichardSWII.AllClass.Appointment;
import TylerRichardSWII.AllClass.Contact;
import TylerRichardSWII.AllClass.Customer;
import TylerRichardSWII.AllClass.Main;
import TylerRichardSWII.Util.TimeConverter;
import TylerRichardSWII.Util.dbCommands;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import javax.security.auth.callback.NameCallback;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.*;
import java.time.chrono.Chronology;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * controller used for modifying appointments
 */
public class ModifyAppointmentController implements dbCommands, Initializable {

    @FXML
    public TextField titleField;
    @FXML
    public TextField typeField;
    @FXML
    public TextField locationField;
    @FXML
    public TextField descriptionField;
    @FXML
    public Button cancelButton;
    @FXML
    public ComboBox contactCBox;
    @FXML
    public ComboBox customerCBox;
    @FXML
    public DatePicker startDateBox;
    @FXML
    public DatePicker endDateBox;
    @FXML
    public ComboBox startMinCBox;
    @FXML
    public ComboBox startHourCBox;
    @FXML
    public ComboBox endHourCBox;
    @FXML
    public ComboBox endMinCBox;
    @FXML
    public Label currentTimeZone;
    @FXML
    public Label currentTimeZone2;
    @FXML
    public TextField appointmentIDField;

    private Connection conn;
    private final ObservableList<Contact> allContacts = FXCollections.observableArrayList();
    private final ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    private final ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    private final ObservableList<String> hours = FXCollections.observableArrayList();
    private final ObservableList<String> minutes = FXCollections.observableArrayList();
    private int appointmentID;
    private final TimeConverter time = new TimeConverter();
    private final Alert errorMessage = new Alert(Alert.AlertType.ERROR);
    private Appointment newAppointment;
    private int contactID;
    private int customerID;
    private String userName;
    private int userID;
    private Appointment selectedAppointment;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    /**
     * @param url
     * @param rb
     * handles events to process when controller first launches
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //gets the username from the login screen
        loginController login = new loginController();
        userName = login.passUserName();

        currentTimeZone.setText(time.getTimeZone());
        currentTimeZone2.setText(time.getTimeZone());
        //gets the hours and minutes to add back the hours and minutes selection
        for (int i = 00; i < 60; i++) {
            if (i < 24) {
                hours.add(String.format("%02d", i));

            }
            if (i < 60 && i % 15 == 0) {
                minutes.add(String.format("%02d", i));
            }
        }
        try {
            //gets all customers,contacts,appointments,and current logged in userID
            getCustomer();
            getContacts();
            getAppointments();
            getUserID();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //gets the selected appointment from the main menu
        MainMenuController newMain = new MainMenuController();
        selectedAppointment = newMain.returnSelectedAppointment();

        //sets the field to the data of the appointment that was passed from main menu
        titleField.setText(selectedAppointment.getTitle());
        typeField.setText(selectedAppointment.getType());
        locationField.setText(selectedAppointment.getLocation());
        descriptionField.setText(selectedAppointment.getDescription());
        startDateBox.setValue(selectedAppointment.getStartTime().toLocalDate());
        startHourCBox.setItems(hours);
        //for the hour and minute CBox it turns it to a string to display a double 00 or 01 instead of just 0 or 1 by
        //converting it to a string
        startHourCBox.setValue(String.format("%02d", Integer.valueOf(selectedAppointment.getStartTime().toLocalTime().getHour())));
        startMinCBox.setItems(minutes);
        startMinCBox.setValue(String.format("%02d", Integer.valueOf(selectedAppointment.getStartTime().toLocalTime().getMinute())));
        startDateBox.setValue(selectedAppointment.getEndTime().toLocalDate());
        endDateBox.setValue(selectedAppointment.getEndTime().toLocalDate());
        endHourCBox.setItems(hours);
        endHourCBox.setValue(String.format("%02d", Integer.valueOf(selectedAppointment.getEndTime().toLocalTime().getHour())));
        endMinCBox.setItems(minutes);
        endMinCBox.setValue(String.format("%02d", Integer.valueOf(selectedAppointment.getEndTime().toLocalTime().getMinute())));
        customerCBox.setItems(allCustomers);
        contactCBox.setItems(allContacts);
        //gets the name of customer and client to set them in the cboxes
        for (Customer c : allCustomers) {
            if (c.getCustomerID() == selectedAppointment.getCustomerID()) {
                customerCBox.setValue(c.getName());
            }
        }
        for (Contact c : allContacts) {
            if (c.getContactID() == selectedAppointment.getContactID()) {
                contactCBox.setValue(c.getName());
            }
        }
        appointmentIDField.setText(Integer.toString(selectedAppointment.getAppointmentID()));
        appointmentID=selectedAppointment.getAppointmentID();


    }

    /**
     * @param actionEvent
     * @throws SQLException
     * handles events for when the saved button is clicked
     */
    public void saveApptClicked(ActionEvent actionEvent) throws SQLException {
        //clears all appointments and gets them again just to verify fresh data
        allAppointments.clear();
        getAppointments();

        //does all the same checking that was performed by the add appointment controller
        boolean titleCheck = checkString(titleField.getText(), "Title ");
        boolean descriptionCheck = checkString(descriptionField.getText(), "Description ");
        boolean typeCheck = checkString(typeField.getText(), "Type ");
        boolean locationCheck = checkString(locationField.getText(), "Location ");
        LocalDate startDate;
        LocalDate endDate;
        boolean conflictingAppt = false;
        ZonedDateTime startTime = ZonedDateTime.now(ZoneId.of("UTC"));
        ZonedDateTime endTime = ZonedDateTime.now(ZoneId.of("UTC"));
        try {
            startDate = startDateBox.getValue();
            endDate = endDateBox.getValue();
            LocalTime openHour = LocalTime.of(Integer.parseInt((startHourCBox.getValue().toString())),
                    Integer.parseInt((startMinCBox.getValue().toString())));
            LocalTime closeHour = LocalTime.of(Integer.parseInt((endHourCBox.getValue().toString())),
                    Integer.parseInt((endMinCBox.getValue().toString())));
            startTime = time.convertToUTC(startDate, openHour);
            endTime = time.convertToUTC(endDate, closeHour);
            conflictingAppt = time.modifyAppointmentCheck(startTime, endTime, allAppointments, selectedAppointment.getAppointmentID());
        } catch (NullPointerException e) {
            if (startHourCBox.getSelectionModel().isEmpty() || startMinCBox.getSelectionModel().isEmpty()) {
                errorMessage.setTitle("Error - Start Time");
                errorMessage.setContentText("Verify start time");
            } else if (endHourCBox.getSelectionModel().isEmpty() || endMinCBox.getSelectionModel().isEmpty()) {
                errorMessage.setTitle("Error - End Time");
                errorMessage.setContentText("Verify end  time");
            } else {
                errorMessage.setTitle("Error - Start/End Date ");
                errorMessage.setContentText("Verify Start / End date of meeting");
            }
            errorMessage.showAndWait();
        }
        if (titleCheck && descriptionCheck && typeCheck && locationCheck && conflictingAppt) {
            try {
                for (Contact c : allContacts) {
                    if (c.getName().equals(contactCBox.getSelectionModel().getSelectedItem().toString())) {
                        contactID = c.getContactID();

                    }
                }
                for (Customer c : allCustomers) {
                    if (c.getName().equals(customerCBox.getSelectionModel().getSelectedItem().toString())) {
                        customerID = c.getCustomerID();

                    }
                }
                newAppointment = new Appointment(appointmentID, titleField.getText(), descriptionField.getText(),
                        locationField.getText(), typeField.getText(), startTime, endTime, customerID, contactID,userID);
                addAppointment(newAppointment);
                Main newMain = new Main();
                newMain.loadFXML("/TylerRichardSWII/fxmlscenes/MainMenu.FXML", "AMCalandar Manager - Main Menu");
                hideStage();
            } catch (NullPointerException | IOException e) {
                if (customerCBox.getSelectionModel().isEmpty()) {
                    errorMessage.setTitle("Customer Error");
                    errorMessage.setContentText("Customer field is empty");
                    errorMessage.showAndWait();
                } else if (contactCBox.getSelectionModel().isEmpty()) {
                    errorMessage.setTitle("Contact Error");
                    errorMessage.setContentText("Contact field is empty");
                    errorMessage.showAndWait();
                }
            }

        }


    }


    /**
     * @param actionEvent
     * handles events for when the cancel button is clicked
     */
    public void cancelButtonClicked(ActionEvent actionEvent) throws IOException {
        //hides stage and loads main meun
        hideStage();
        Main main = new Main();
        main.loadFXML("/TylerRichardSWII/fxmlscenes/MainMenu.fxml", "AMCalandar Manager - Main Menu");
    }



    /**
     * @param name - string to check
     * @param field - name of field to dispaly error for
     * @return
     */
    public boolean checkString(String name, String field) {
        if (name.isBlank()) {
            Alert errorMessage = new Alert(Alert.AlertType.ERROR);
            errorMessage.setContentText(field + "is blank");
            errorMessage.setTitle(field + "error");
            errorMessage.showAndWait();
            return false;
        } else return true;
    }

    /**
     * method to quickly hide stage
     */
    public void hideStage() {
        Stage thisStage = (Stage) titleField.getScene().getWindow();
        thisStage.hide();
    }

    /**
     * @throws SQLException
     * connects to DB
     */
    @Override
    public void connectDB() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/client_schedule";
        String username = "sqlUser";
        String password = "Passw0rd!";
        conn = DriverManager.getConnection(url, username, password);
    }

    /**
     * @param newCustomer
     * @throws SQLException
     * not used
     */
    @Override
    public void addCustomer(Customer newCustomer) throws SQLException {
    }

    /**
     * @throws SQLException
     * not used
     */
    @Override
    public void getCountries() throws SQLException {
    }

    /**
     * @throws SQLException
     * not used
     */
    @Override
    public void getStateOrProvince() throws SQLException {
    }

    /**
     * @return
     * not used
     * @throws SQLException
     */
    @Override
    public boolean validateLogin() throws SQLException {
        return false;
    }

    /**
     * @throws SQLException
     * gets all customers puts them in a list
     */
    @Override
    public void getCustomer() throws SQLException {
        connectDB();
        String fullName;
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM customers")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                fullName = rs.getNString("Customer_Name");
                String[] splitName = fullName.split(" ");
                Customer newCustomer = new Customer(splitName[0], splitName[1], rs.getNString("Address"),
                        rs.getNString("Postal_Code"), rs.getNString("Phone"),
                        rs.getInt("Division_ID"), rs.getInt("Customer_ID"));
                allCustomers.add(newCustomer);


            }

        }
    }


    /**
     * @param newAppointment
     * @throws SQLException
     * this updates the current selected appointment
     */
    @Override
    public void addAppointment(Appointment newAppointment) throws SQLException {
        connectDB();
        try (PreparedStatement ps = conn.prepareStatement("Update appointments SET Title = ?, Description = ?, " +
                "Location = ?, Type = ?, Start = ?, End= ? ,Last_Update = ?, Last_Updated_By = ?, Customer_ID = ?," +
                "User_ID= ?,Contact_ID = ? WHERE Appointment_ID = ?")) {
            System.out.println(newAppointment.getAppointmentID());
            ps.setString(1, newAppointment.getTitle());
            ps.setString(2, newAppointment.getDescription());
            ps.setString(3, newAppointment.getLocation());
            ps.setString(4, newAppointment.getType());
            ps.setObject(5, newAppointment.getStartTime().format(formatter));
            ps.setObject(6, newAppointment.getEndTime().format(formatter));
            ps.setString(7, time.convertToUTC().replace("UTC",""));
            ps.setString(8, userName);
            ps.setInt(9, customerID);
            ps.setInt(10, userID);
            ps.setInt(11, contactID);
            ps.setInt(12, appointmentID);
            ps.execute();

        }
    }

    /**
     * @throws SQLException
     * gets contacts puts them all in a list
     */
    @Override
    public void getContacts() throws SQLException {
        connectDB();
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM contacts")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Contact newContact = new Contact(rs.getInt("Contact_ID"),
                        rs.getNString("Contact_Name"), rs.getNString("Email"));
                allContacts.add(newContact);

            }

        }
    }

    /**
     * @throws SQLException
     * gets all appointments puts them in a list
     */
    @Override
    public void getAppointments() throws SQLException {
        connectDB();
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM appointments")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                LocalDateTime start = (LocalDateTime) rs.getObject("Start");
                LocalDateTime end = (LocalDateTime) rs.getObject("End");
                LocalTime startTime = start.toLocalTime();
                LocalDate startDate = start.toLocalDate();
                LocalTime endTime = end.toLocalTime();
                LocalDate endDate = end.toLocalDate();
                ZonedDateTime finalStart = ZonedDateTime.of(startDate, startTime, ZoneId.of("UTC"));
                ZonedDateTime finalEnd = ZonedDateTime.of(endDate, endTime, ZoneId.of("UTC"));
                Appointment newAppointment = new Appointment(rs.getInt("Appointment_ID"),
                        rs.getNString("Title"), rs.getString("Description"),
                        rs.getNString("Location"), rs.getNString("Type"), finalStart, finalEnd,
                        rs.getInt("Customer_ID"), rs.getInt("Contact_ID"),rs.getInt("User_ID"));
                allAppointments.add(newAppointment);

            }
        }
    }

    /**
     * @throws SQLException
     * gets the user id from the database using the name that was passed from main controller
     */
    @Override
    public void getUserID() throws SQLException {
        connectDB();
        try (PreparedStatement ps = conn.prepareStatement("SELECT User_ID FROM users WHERE User_Name = ?")) {
            ps.setString(1, userName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                userID = rs.getInt("User_ID");
            }
        }
    }

    /**
     * @param customerRemove
     * not used
     */
    @Override
    public void removeCustomer(Customer customerRemove) {
    }

    /**
     * @param appointmentRemove
     * not used
     */
    @Override
    public void removeAppointments(Appointment appointmentRemove) {
    }

}

