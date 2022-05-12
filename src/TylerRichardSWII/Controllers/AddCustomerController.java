package TylerRichardSWII.Controllers;


import TylerRichardSWII.AllClass.Appointment;
import TylerRichardSWII.AllClass.Country;
import TylerRichardSWII.AllClass.Customer;
import TylerRichardSWII.AllClass.Main;
import TylerRichardSWII.AllClass.StateProvince;
import TylerRichardSWII.Util.TimeConverter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import TylerRichardSWII.Util.dbCommands;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;


/**
 * This controller handles the events that occur on the AddCustomer GUI
 */
public class AddCustomerController implements dbCommands, Initializable {
    private static Connection conn;
    @FXML
    public TextField firstNameField;
    @FXML
    public TextField lastNameField;
    @FXML
    public Button cancelButton;
    @FXML
    public Button addCustomerButton;
    @FXML
    public TextField customerIDLabel;
    @FXML
    public ComboBox countryCBox;
    @FXML
    public ComboBox stateCBox;
    @FXML
    public TextField addressField;
    @FXML
    public Label stateLabel;
    @FXML
    public TextField phoneField;
    @FXML
    public TextField postalCodeField;
    @FXML
    public Label postalCodeLabel;


    private final ObservableList<Country> allCountry = FXCollections.observableArrayList();
    private final ObservableList<StateProvince> allState = FXCollections.observableArrayList();
    private final ObservableList<StateProvince> filteredStates = FXCollections.observableArrayList();
    private int countryID;
    private int divisionID;
    private int customerID;
    Alert errorMessage = new Alert(Alert.AlertType.ERROR);

    /**
     * @param url
     * @param resourceBundle
     * handles the events when the controller first loads
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //queries the db for countries and states when the gui first loads
        try {
            getCountries();
            getStateOrProvince();
            getCustomer();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * @param actionEvent
     * @throws SQLException
     * handles events for when the country is selected
     */
    public void countrySelected(ActionEvent actionEvent) throws SQLException {
        String selectedCountry = countryCBox.getValue().toString();
        //gets the selected Country from the list and then compares the names. once found it sets the country ID to
        //the Id of the selected list
        for (Country country : allCountry) {
            if (country.getName().equals(selectedCountry)) {
                countryID = country.getCountryID();
            }
        }
        //clears the filtered states list to make sure no data is saved in the list
        //getStateOrProvince();
        filteredStates.clear();
        //loops though all the states and compares each state country Id to the selected country ID and then once found
        //adds it to a list of filtered states.
        for (StateProvince s : allState) {
            if (s.getCountryID() == countryID) {
                filteredStates.add(s);
            }
        }
        //this is just for visual for the Cbox so that if the size of the list is 5 or less there is not a bunch of white space
        if (filteredStates.size() <= 5) {
            stateCBox.setVisibleRowCount(5);
        } else {
            stateCBox.setVisibleRowCount(10);
        }



    }





    /**
     * @param event
     * @throws SQLException
     * this handles populating the country List.
     */
    public void displayCountry(Event event) throws SQLException {
        //honestly This should have been done in the initialization however this populates the country list when the box
        //is clicked
        if (countryCBox.getItems().isEmpty()) {
            for (Country country : allCountry) {
                countryCBox.getItems().add(country.getName());
            }
        }
    }

    /**
     * @param actionEvent
     * @throws SQLException
     * this handles events for when the StateCBox is clicked (choice is selected)
     */
    public void stateCBoxClicked(ActionEvent actionEvent) throws SQLException {
       //tries to get the CBox data and then compares the name to each name in the filtered states list
        //once  found sets the divisionID of the state
        try {
            String selectedState = stateCBox.getValue().toString();
            for (StateProvince s : filteredStates) {
                if (s.getName().equals(selectedState)) {
                    divisionID = s.getDivisionID();
                }
            }
        } catch (NullPointerException e) {
        }
    }

    /**
     * @param event
     * @throws SQLException
     * handles events for when the StateCBox is showing
     */
    public void stateCBoxShowing(Event event) throws SQLException {
        //clears the statecbox to make sure old states are not saved to it.
        stateCBox.getItems().clear();
        //if its empty it populates the Cbox with the filtered states liss
        if (stateCBox.getItems().isEmpty()) {
            for (StateProvince s : filteredStates) {
                stateCBox.getItems().add(s.getName());

            }

        }
    }

    /**
     * @param actionEvent
     * @throws SQLException
     * @throws IOException
     * Handles events for when the add customer button is clicked
     */
    public void addCustomerClicked(ActionEvent actionEvent) throws SQLException, IOException {
        //getCustomer();
        //calls Stringcheck and CheckNumber methods which are defined later in the code. Passes through the text from
        //the fields and the field name to display an error in those boxes when it checks them
        boolean firstNameCheck = checkString(firstNameField.getText(), "First Name ");
        boolean lastNameCheck = checkString(lastNameField.getText(), "Last Name ");
        boolean addressCheck = checkString(addressField.getText(), "Address ");
        boolean phoneCheck = checkNumber(phoneField.getText(), "Phone number ");
        boolean postalCodeCheck = checkNumber(postalCodeField.getText(), "Postal Code ");

        //if all those checks are passed then a new customer object is created with the info from those fields
        //the calls the addCustomer from dbCommands and hides the stage and loads the main stage
        if (firstNameCheck && lastNameCheck && addressCheck && phoneCheck && postalCodeCheck &&
                !countryCBox.getSelectionModel().isEmpty() && !stateCBox.getSelectionModel().isEmpty()) {
            Customer newCustomer = new Customer(firstNameField.getText(), lastNameField.getText(), addressField.getText(),
                    postalCodeField.getText(), phoneField.getText(), divisionID, ++customerID);
            addCustomer(newCustomer);
            hideStage();
            Main main = new Main();
            main.loadFXML("/TylerRichardSWII/fxmlscenes/MainMenu.fxml", "CSM - Main Menu");
        } else if (countryCBox.getSelectionModel().isEmpty()) {
            errorMessage.setTitle("Country Selection Error");
            errorMessage.setContentText("No country selected");
            errorMessage.showAndWait();
        } else if (stateCBox.getSelectionModel().isEmpty()) {
            errorMessage.setTitle("State / Prov Selection Error");
            errorMessage.setContentText("No State or Prov selected");
            errorMessage.showAndWait();

        }
    }

    /**
     * @param actionEvent
     * @throws IOException
     * handles events for when the cancel button is clicked.
     */
    public void cancelButtonClicked(ActionEvent actionEvent) throws IOException {
        //hides stage and loads main meun
        hideStage();
        Main main = new Main();
        main.loadFXML("/TylerRichardSWII/fxmlscenes/MainMenu.fxml", "AMCalendar - Main Menu");
    }

    /**
     * @param number number to check (as string) since its stored in the DB as varChar
     * @param field this is so that an error Message can be displayed with where the error of the filed is at
     * @return
     */
    public boolean checkNumber(String number, String field) {

        //if field is blank it shows error.
        if (number.isBlank()) {
            errorMessage.setContentText(field + "is Blank");
            errorMessage.setTitle(field + "error");
            errorMessage.showAndWait();
            return false;
            //if the field has data but the date contains non-numerical data in the phone field it display an invalid data type
        } else if (number.matches("[a-zA-Z]+") && field.contains("Phone number")) {
            errorMessage.setContentText(field + "has invalid data");
            errorMessage.setTitle(field + "error");
            errorMessage.showAndWait();
            return false;
        } else return true;
    }

    /**
     * @param name String to check
     * @param field field name to display error message
     * @return
     */
    public boolean checkString(String name, String field) {
       // if field is blank display error message otherwise return true
        if (name.isBlank()) {
            Alert errorMessage = new Alert(Alert.AlertType.ERROR);
            errorMessage.setContentText(field + "is blank");
            errorMessage.setTitle(field + "error");
            errorMessage.showAndWait();
            return false;
        } else return true;
    }

    /**
     * quick method to hide stage used multiple times
     */
    public void hideStage() {
        Stage thisStage = (Stage) firstNameField.getScene().getWindow();
        thisStage.hide();
    }

    /**
     * @throws SQLException
     * used to connect to DB
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
     * used to add Customer  to the Database
     */
    @Override
    public void addCustomer(Customer newCustomer) throws SQLException {
       //time converter class is used to convert local time to UTC for some fields in DB.
        //log in controller class created to pass the logined username to the DB for the created by field
        TimeConverter time = new TimeConverter();
        connectDB();
        loginController username = new loginController();
        String user = username.passUserName();
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");

        try (PreparedStatement ps = conn.prepareStatement("INSERT INTO customers(Customer_ID,Customer_Name,Address," +
                "Postal_Code,Phone,Create_Date,Created_By,Last_Update,Last_Updated_By,Division_ID) values(?, ?, ?, ? ," +
                "?, ?, ?, ?, ?, ?)")) {
            ps.setInt(1, newCustomer.getCustomerID());
            ps.setString(2, newCustomer.getName());
            ps.setString(3, newCustomer.getAddress());
            ps.setString(4, newCustomer.getPostalCode());
            ps.setString(5, newCustomer.getPhoneNumber());
            ps.setString(6, time.convertToUTC().replace("UTC",""));
            ps.setString(7, user);
            ps.setString(8, time.convertToUTC().replace("UTC",""));
            ps.setString(9, user);
            ps.setInt(10, newCustomer.getDivisionID());
            ps.execute();

        }
    }

    /**
     * @throws SQLException
     * gets the list of countries from the database and adds them to a list
     */
    @Override
    public void getCountries() throws SQLException {
        connectDB();
        try (PreparedStatement ps = conn.prepareStatement("SELECT Country, Country_ID FROM countries")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Country newCountry = new Country(rs.getNString("Country"), rs.getInt("Country_ID"));
                allCountry.add(newCountry);
            }
        }
    }

    /**
     * @throws SQLException
     * gets the list of states from the databse and adds them to a list
     */
    @Override
    public void getStateOrProvince() throws SQLException {
        connectDB();
        try (PreparedStatement ps = conn.prepareStatement("SELECT Division,Division_ID, Country_ID FROM " +
                "first_level_divisions")) {
            ResultSet rs = ps.executeQuery();
            allState.clear();
            while (rs.next()) {
                StateProvince newState = new StateProvince(rs.getNString("Division"),
                        rs.getInt("Division_ID"), rs.getInt("Country_ID"));
                allState.add(newState);
            }
        }
    }

    /**
     * @throws SQLException
     * this gets the max customerId in the database to implement increase the userID by 1
     */
    @Override
    public void getCustomer() throws SQLException {
        connectDB();
        try (PreparedStatement ps = conn.prepareStatement("SELECT max(Customer_ID) Customer_ID FROM customers")) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                customerID = rs.getInt("Customer_ID");
            }
        }

    }

    /**
     * @param newAppointment
     * @throws SQLException
     * not used
     */
    @Override
    public void addAppointment(Appointment newAppointment) throws SQLException {
    }

    /**
     * @throws SQLException
     * not used
     */
    @Override
    public void getContacts() throws SQLException {
    }

    /**
     * @throws SQLException
     * not used
     */
    @Override
    public void getAppointments() throws SQLException {
    }

    /**
     * @throws SQLException
     * not used
     */
    @Override
    public void getUserID() throws SQLException {
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

    /**
     * @return
     * not used
     */
    @Override
    public boolean validateLogin() {
        return false;
    }


}
