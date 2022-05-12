package TylerRichardSWII.Controllers;


import TylerRichardSWII.AllClass.Appointment;
import TylerRichardSWII.AllClass.Country;
import TylerRichardSWII.AllClass.Customer;
import TylerRichardSWII.AllClass.Main;
import TylerRichardSWII.AllClass.StateProvince;
import TylerRichardSWII.Controllers.loginController;
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
import java.util.ResourceBundle;


/**
 * controller used for modifying customers
 */
public class ModifyCustomerController implements dbCommands, Initializable {
    @FXML
    public TextField firstNameField;
    @FXML
    public TextField lastNameField;
    @FXML
    public Label stateLabel;
    @FXML
    public TextField addressField;
    @FXML
    public ComboBox stateCBox;
    @FXML
    public ComboBox countryCBox;
    @FXML
    public TextField customerIDField;
    @FXML
    public Button addCustomerButton;
    @FXML
    public Button cancelButton;
    @FXML
    public TextField phoneField;
    @FXML
    public TextField postalCodeField;
    @FXML
    public Label postalCodeLabel;
    @FXML
    public Button saveButtonClicked;
    @FXML
    public Label currentTimeZone;
    @FXML
    public Label currentTimeZone2;


    Connection conn;
    ObservableList<Country> allCountry = FXCollections.observableArrayList();
    ObservableList<StateProvince> allState = FXCollections.observableArrayList();
    private final ObservableList<StateProvince> filteredStates = FXCollections.observableArrayList();
    private int countryID;
    private int divisionID;
    private int CID;
    private Customer currentCustomer;
    private boolean countryStateCheck;
    Alert errorMessage = new Alert(Alert.AlertType.ERROR);
    TimeConverter time=new TimeConverter();


    /**
     * @param url
     * @param rb
     * handles actions on launch of controller
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        stateCBox.setVisibleRowCount(5);
        //tries to connect to DB to get countries and state/province and split the name
        try {
            //gets the selected user from the main menu controller.
            MainMenuController main = new MainMenuController();
            currentCustomer = main.returnSelectedCustomer();
            //gets the name of the customer selected and then splits it to first and last and stores in array
            String fullName = currentCustomer.getName();
            String[] splitName = fullName.split(" ");
            //gets the customer ID and turns it to a tring
            String customerID = String.valueOf(currentCustomer.getCustomerID());
            CID = currentCustomer.getCustomerID();
            getCountries();
            getStateOrProvince();

            //for loop that goes through all states and looks for the division ID of the states that the customer has set
            //once found sets the StateCbox to the name of that state
            for (StateProvince s : allState) {
                if (currentCustomer.getDivisionID() == s.getDivisionID()) {
                    stateCBox.setValue(s.getName());
                    divisionID = currentCustomer.getDivisionID();

                    //loops through all countries to find the country ID that matches the state country id once found
                    //sets the Cbox for the name of that country ID also sets the country id to be used few lines later
                    for (Country c : allCountry) {
                        if (s.getCountryID() == c.getCountryID()) {
                            countryCBox.setValue(c.getName());
                            countryID = s.getCountryID();


                        }
                    }
                }
                //while going through the states if the country ID matches that of the one set above it adds it to the filtered list
                if (s.getCountryID() == countryID) {
                    filteredStates.add(s);


                }
            }
            //sets the fields with data from the selected user
            firstNameField.setText(splitName[0]);
            lastNameField.setText(splitName[1]);
            phoneField.setText(currentCustomer.getPhoneNumber());
            addressField.setText(currentCustomer.getAddress());
            postalCodeField.setText(currentCustomer.getPostalCode());
            customerIDField.setText(customerID);




        } catch (NullPointerException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param actionEvent
     * @throws SQLException
     * handles event for when an option is selecetd in the countryCBox
     */
    public void countrySelected(ActionEvent actionEvent) throws SQLException {
        //tries to get the selected item and get the text and then compares it to all countries list and sets the Country ID

        try {
            String selectedCountry = countryCBox.getValue().toString();
            for (Country country : allCountry) {
                if (country.getName().equals(selectedCountry)) {
                    countryID = country.getCountryID();
            }
            //clears filtered state and then searches through all the states for matching Country id and addes it to the
            //filtered states list
            filteredStates.clear();
                for (StateProvince s : allState) {
                    if (s.getCountryID() == countryID) {
                        filteredStates.add(s);
                    }

                }
            }
            if (filteredStates.size() <= 5) {
                stateCBox.setVisibleRowCount(5);
            } else {
                stateCBox.setVisibleRowCount(10);
            }



        } catch (NullPointerException e) {
        }
    }

    /**
     * @param actionEvent
     * handles events when the stateCBox has a selected item
     */
    public void stateCBoxClicked(ActionEvent actionEvent) {
        //tries to get the selected text from the state cbox and the gets the division ID and sets it to be use to store in DB
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
     * handles events for when the StateCBox is showing
     */
    public void stateCBoxShowing(Event event) {
        //clears the list and sets it.
        stateCBox.getItems().clear();
        if (stateCBox.getItems().isEmpty()) {
            for (StateProvince s : filteredStates) {
                stateCBox.getItems().add(s.getName());

            }

        }
    }

    /**
     * @param event
     * @throws SQLException
     * handles events when the countryCBox is showing
     */
    public void displayCountry(Event event) throws SQLException {
        //clears and sets the list
        countryCBox.getItems().clear();
        if (countryCBox.getItems().isEmpty()) {
            for (Country country : allCountry) {
                countryCBox.getItems().add(country.getName());

            }
        }
    }

    /**
     * @param actionEvent
     * @throws SQLException
     * @throws IOException
     * handles events when the save button is clicked
     */
    public void saveButtonClicked(ActionEvent actionEvent) throws SQLException, IOException {

        //booleans check to validate date in the text fields and then passes the field name for data validation
        boolean firstNameCheck = checkString(firstNameField.getText(), "First Name ");
        boolean lastNameCheck = checkString(lastNameField.getText(), "Last Name ");
        boolean addressCheck = checkString(addressField.getText(), "Address ");
        boolean phoneCheck = checkNumber(phoneField.getText(), "Phone number ");
        boolean postalCodeCheck = checkNumber(postalCodeField.getText(), "Postal Code ");

        for(StateProvince s : filteredStates){
            if(s.getCountryID()== countryID && stateCBox.getValue().toString().equalsIgnoreCase(s.getName())){
                countryStateCheck=true;
                break;
            }else{

                countryStateCheck=false;

            }
        }

        if(!countryStateCheck){
            errorMessage.setContentText("State or Providence is not within country ");
            errorMessage.setTitle("State / Prov Error");
            errorMessage.showAndWait();
        }

        if (firstNameCheck && lastNameCheck && addressCheck && phoneCheck && postalCodeCheck && countryStateCheck) {
            Customer updateCustomer = new Customer(
                    firstNameField.getText(), lastNameField.getText(), addressField.getText(),
                    postalCodeField.getText(), phoneField.getText(), divisionID, CID);
            addCustomer(updateCustomer);
            hideStage();
            Main main = new Main();
            main.loadFXML("/TylerRichardSWII/fxmlscenes/MainMenu.fxml", "AMCalandar Manager - Main Menu");
        }

    }


    public void cancelButtonClicked(ActionEvent actionEvent) throws IOException {
        //hides stage and loads main menu
        hideStage();
        Main main = new Main();
        main.loadFXML("/TylerRichardSWII/fxmlscenes/MainMenu.fxml", "AMCalandar Manager - Main Menu");
    }

    /**
     * @param number  number to check as string
     * @param field field to display an error for
     * @return
     */
    private boolean checkNumber(String number, String field) {
        errorMessage = new Alert(Alert.AlertType.ERROR);

        //if number is blank then display error else if number contains a-z and field is phone number display error
        if (number.isBlank()) {
            errorMessage.setContentText(field + "is Blank");
            errorMessage.setTitle(field + "error");
            errorMessage.showAndWait();
            return false;
        } else if (number.matches("[a-zA-Z]+") && field.contains("Phone number")) {
            errorMessage.setContentText(field + "has invalid data");
            errorMessage.setTitle(field + "error");
            errorMessage.showAndWait();
            return false;
        } else return true;
    }




    /**
     * @param textToCheck string to check to see if it has data
     * @param field - field to display error message for
     * @return
     */
    private boolean checkString(String textToCheck, String field) {
        if (textToCheck.isBlank()) {
            Alert errorMessage = new Alert(Alert.AlertType.ERROR);
            errorMessage.setContentText(field + "is blank");
            errorMessage.setTitle(field + "error");
            errorMessage.showAndWait();
            return false;
        } else return true;
    }

    /**
     * Method used to hide the current stage
     */
    public void hideStage() {
        Stage thisStage = (Stage) firstNameField.getScene().getWindow();
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
     * adds the customer
     */
    @Override
    public void addCustomer(Customer newCustomer) throws SQLException {
        TimeConverter time = new TimeConverter();
        loginController username = new loginController();
        String user = username.passUserName();
        try (PreparedStatement ps = conn.prepareStatement("UPDATE customers SET Customer_Name = ?,Address =?," +
                "Postal_Code = ?, Phone = ?, Last_Update = ?, Last_Updated_By = ?, Division_ID = ? " +
                "WHERE Customer_ID = ? ")) {
            ps.setString(1, newCustomer.getName());
            ps.setString(2, newCustomer.getAddress());
            ps.setString(3, newCustomer.getPostalCode());
            ps.setString(4, newCustomer.getPhoneNumber());
            ps.setObject(5, time.convertToUTC().replace("UTC",""));
            ps.setString(6, user);
            ps.setInt(7, divisionID);
            ps.setInt(8, CID);
            ps.execute();


        }
    }

    /**
     * @throws SQLException
     * gets the countries
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
     * gets the states
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
     * not used
     */
    @Override
    public void getCustomer() throws SQLException {
        //try(PreparedStatement ps = conn.prepareStatement("SELECT"))
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


}
