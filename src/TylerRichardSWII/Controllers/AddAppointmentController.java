package TylerRichardSWII.Controllers;


import TylerRichardSWII.Util.TimeConverter;
import TylerRichardSWII.Util.dbCommands;
import TylerRichardSWII.AllClass.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Controller used for adding appointments
 */
public class AddAppointmentController implements dbCommands, Initializable {

    @FXML
    public TextField titleField;
    @FXML
    public TextField typeField;
    @FXML
    public TextField locationField;
    @FXML
    public TextField descriptionField;
    @FXML
    public ComboBox contactCBox;
    @FXML
    public ComboBox customerCBox;
    @FXML
    public TextField endTimeField;
    @FXML
    public TextField startTimeField;
    @FXML
    public DatePicker startDateBox;
    @FXML
    public DatePicker endDateBox;
    @FXML
    public Button createApptButton;
    @FXML
    public ComboBox startHourCBox;
    @FXML
    public ComboBox startMinCBox;
    @FXML
    public ComboBox endHourCBox;
    @FXML
    public ComboBox endMinCBox;
    @FXML
    public Label currentTimeZone;
    @FXML
    public Label currentTimeZone2;
    public Button cancelButton;

    private final TimeConverter time = new TimeConverter();
    private final ObservableList<String> hours = FXCollections.observableArrayList();
    private final ObservableList<String> minutes = FXCollections.observableArrayList();
    private final ObservableList<Contact> allContacts = FXCollections.observableArrayList();
    private final ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    private final ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    private Connection conn;
    private final Alert errorMessage = new Alert(Alert.AlertType.WARNING);
    private int appointmentID;
    private Appointment newAppointment;
    private int customerID;
    private int userID;
    private int contactID;
    private String userName;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    /**
     * @param url
     * @param resourceBundle
     * events to process when controller is initialized
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //collects username from login screen
        loginController newLoginController = new loginController();
        userName = newLoginController.passUserName();
        //tries to connect to DB to get contacts customers and userID
        try {
            getContacts();
            getCustomer();
            getUserID();
            //sets the contact cbox  and the customer cbox after getting it from DB
            for (Contact c : allContacts) {
                contactCBox.getItems().add(c.getName());
            }
            for (Customer c : allCustomers) {
                customerCBox.getItems().add(c.getName());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //sets the hours and minute String to be used for the Start and end times
        for (int i = 00; i < 60; i++) {
            if (i < 24) {
                hours.add(String.format("%02d", i));

            }
            if (i < 60 && i % 15 == 0) {
                minutes.add(String.format("%02d", i));
            }
        }

        //sets the cbox to the hours and minutes that were created above and sets the labels to the local time zone
        //so that the user knows what time zone they are submitting their appointment under
        startHourCBox.setItems(hours);
        endHourCBox.setItems(hours);
        startMinCBox.setItems(minutes);
        endMinCBox.setItems(minutes);
        currentTimeZone.setText(time.getTimeZone());
        currentTimeZone2.setText(time.getTimeZone());


    }

    /**
     * @param actionEvent
     * @throws IOException
     * handles cancel button clicked
     */
    public void cancelButtonClicked(ActionEvent actionEvent) throws IOException {
        //calls main class to call the loadFXML method and hides the stage
        Main newMain = new Main();
        newMain.loadFXML("/TylerRichardSWII/fxmlscenes/MainMenu.FXML", "AMCalandar Manager - Main Menu");
        hideStage();
    }

    /**
     * @param actionEvent
     * @throws SQLException
     * handles the create Appointment button clicked
     */
    public void createApptClicked(ActionEvent actionEvent) throws SQLException {
        //clears appointments and then gets a new list of appointments. (this code was left I was planning to not close
        //the menu after an appointment was added. While not needed now being left for later modifications.
        allAppointments.clear();
        getAppointments();

        //check strings same method that was used for customers
        boolean titleCheck = checkString(titleField.getText(), "Title ");
        boolean descriptionCheck = checkString(descriptionField.getText(), "Description ");
        boolean typeCheck = checkString(typeField.getText(), "Type ");
        boolean locationCheck = checkString(locationField.getText(), "Location ");
        //initialization of both localDate and ZoneDateTime and boolean(IDE was throwing possible null further down)
        LocalDate startDate;
        LocalDate endDate;
        boolean conflictingAppt = false;
        ZonedDateTime startTime = ZonedDateTime.now(ZoneId.of("UTC"));
        ZonedDateTime endTime = ZonedDateTime.now(ZoneId.of("UTC"));


        try {
            //gets start Date and end date from date picker gets the open hour and close hour of appointment by grabbing the
            //date from the Cboxs once the LocalTime and dates are set the TimeConverter class (time) calls the convert to UTC.
            //conflictingAppts then calls the checkBusinessHours and passes the start and end time and the list of appointments to compare
            startDate = startDateBox.getValue();
            endDate = endDateBox.getValue();
            LocalTime openHour = LocalTime.of(Integer.parseInt((startHourCBox.getValue().toString())),
                    Integer.parseInt((startMinCBox.getValue().toString())));
            LocalTime closeHour = LocalTime.of(Integer.parseInt((endHourCBox.getValue().toString())),
                    Integer.parseInt((endMinCBox.getValue().toString())));
            startTime = time.convertToUTC(startDate, openHour);
            endTime = time.convertToUTC(endDate, closeHour);


            conflictingAppt = time.checkBusinessHours(startTime, endTime, allAppointments);
        } catch (NullPointerException e) {
          //throws errors if any of the CBoxes are empty
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
        //gets the results from the checks above for booleans and verifies that the Contact / Customer not empty and that
        //the appointment was not conflicting with another one
        if (titleCheck && descriptionCheck && typeCheck && locationCheck &&
                !customerCBox.getSelectionModel().isEmpty() && !contactCBox.getSelectionModel().isEmpty() && conflictingAppt) {
            try {
                //for loops turn the contact name into a contact ID to be passed to the creation of an appointment
                //which then will be passed to the command to be added to the database and hides stage and calls main menu
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
                newAppointment = new Appointment(++appointmentID, titleField.getText(), descriptionField.getText(),
                        locationField.getText(), typeField.getText(), startTime, endTime, customerID, contactID,userID);
                addAppointment(newAppointment);
                Main newMain = new Main();
                newMain.loadFXML("/TylerRichardSWII/fxmlscenes/MainMenu.FXML", "AMCalandar Manager - Main Menu");
                hideStage();
            } catch (NullPointerException | IOException e) {
            }

        //if contact and customer Field are blank it will prompt
        } else if (customerCBox.getSelectionModel().isEmpty()) {
            errorMessage.setTitle("Customer Error");
            errorMessage.setContentText("Customer field is empty");
            errorMessage.showAndWait();
        } else if (contactCBox.getSelectionModel().isEmpty()) {
            errorMessage.setTitle("Contact Error");
            errorMessage.setContentText("Contact field is empty");
            errorMessage.showAndWait();
        }


    }


    /**
     * @param name string to check
     * @param field field name to pass for error message
     * @return returns T/F based off of results
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
     * Method to quick be able to hide the stage
     */
    public void hideStage() {
        Stage thisStage = (Stage) titleField.getScene().getWindow();
        thisStage.hide();
    }


    /**
     * @throws SQLException
     * Connects to DB
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
     * @throws SQLException
     * not used
     */
    @Override
    public boolean validateLogin() throws SQLException {
        return false;
    }

    /**
     * @throws SQLException
     * gets all customers puts them into a list
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
     * add the appointment to the Database
     */
    @Override
    public void addAppointment(Appointment newAppointment) throws SQLException {
        connectDB();
        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO appointments(Appointment_ID,Title,Description," +
                "Location,Type,Start,End,Create_Date,Created_By,Last_Update,Last_Updated_By,Customer_ID,User_ID,Contact_ID)" +
                "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            ps.setInt(1, appointmentID);
            ps.setString(2, newAppointment.getTitle());
            ps.setString(3, newAppointment.getDescription());
            ps.setString(4, newAppointment.getLocation());
            ps.setString(5, newAppointment.getType());
            ps.setObject(6, newAppointment.getStartTime().format(formatter));
            ps.setObject(7, newAppointment.getEndTime().format(formatter));
            ps.setString(8, time.convertToUTC().replace("UTC",""));
            ps.setString(9, userName);
            ps.setString(10, time.convertToUTC().replace("UTC",""));
            ps.setString(11, userName);
            ps.setInt(12, customerID);
            ps.setInt(13, userID);
            ps.setInt(14, contactID);
            ps.execute();

        }
    }

    /**
     * @throws SQLException
     * get all contacts and add them to a list
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
     * gets all appointments adds them to a list
     * future modifications id grab just the appointments that have a date related to the date being submitted
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
        try (PreparedStatement ps = conn.prepareStatement("SELECT max(Appointment_ID) Appointment_ID FROM appointments")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                appointmentID = rs.getInt("Appointment_ID");
            }

        }
    }

    /**
     * @throws SQLException
     * gets the userID for the logged in user that was passed from the main menu
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