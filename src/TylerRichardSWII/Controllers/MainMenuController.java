package TylerRichardSWII.Controllers;


import TylerRichardSWII.AllClass.Appointment;
import TylerRichardSWII.AllClass.Contact;
import TylerRichardSWII.AllClass.Customer;
import TylerRichardSWII.AllClass.Main;
import TylerRichardSWII.Util.TimeConverter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import TylerRichardSWII.Util.dbCommands;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.sql.*;
import java.time.*;

import java.time.format.DateTimeFormatter;

import java.time.temporal.TemporalAdjusters;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Stream;

/**
 * MainMenuController is the first controller that launches after the login attempt was successful. This is where the main actions
 * of the application take place
 */
public class MainMenuController implements dbCommands, Initializable {
    @FXML
    public RadioButton monthlyRadio;
    @FXML
    public TableView calendarTable;
    @FXML
    public RadioButton weeklyRadio;
    @FXML
    public Button addApptButton;
    @FXML
    public Button modifyApptButton;
    @FXML
    public Button deleteApptButton;
    @FXML
    public Button addCustomerButton;
    @FXML
    public Button modifyCustomer;
    @FXML
    public TableView<Customer> customerTable;
    @FXML
    public TableColumn customerIDCol;
    @FXML
    public TableColumn customerNameCol;
    @FXML
    public TableColumn AddressCol;
    @FXML
    public TableColumn postalCodeCol;
    @FXML
    public TableColumn phoneNumberCol;
    @FXML
    public TableColumn apptIDCol;
    @FXML
    public TableColumn titleCol;
    @FXML
    public TableColumn descCol;
    @FXML
    public TableColumn locationCol;
    @FXML
    public TableColumn contactCol;
    @FXML
    public TableColumn typeCol;
    @FXML
    public TableColumn startCol;
    @FXML
    public TableColumn endCol;
    @FXML
    public TableColumn apptCustomerID;
    @FXML
    public RadioButton allRadio;
    @FXML
    public Label upcomingAppointmentLabel;
    @FXML
    public TableColumn userIDCol;
    @FXML
    public RadioButton SearchRadioButton;
    @FXML
    public TextField SearchBox;

    private Connection conn;
    private final ObservableList<Customer> allCustomers = FXCollections.observableArrayList();
    private final ObservableList<Contact> allContacts = FXCollections.observableArrayList();
    private final ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    private final ObservableList<Appointment> weekAppointments = FXCollections.observableArrayList();
    private final ObservableList<Appointment> monthAppointments = FXCollections.observableArrayList();
    private ObservableList<Appointment> searchAppointmentResults=FXCollections.observableArrayList();
    private ObservableList<Customer> searchCustomerResults=FXCollections.observableArrayList();
    private static Customer selectedCustomer;
    private static Appointment selectedAppointment;
    private final Alert errorMessage = new Alert(Alert.AlertType.ERROR);
    private final TimeConverter Time = new TimeConverter();
    private String contactName;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z");

    @Override
    public void initialize(URL url, ResourceBundle rb) {

       //Queries the DB for customers, appointments and contact
        try {
            getCustomer();
            getAppointments();
            getContacts();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
        }
        //sets the default radio button for the radio group to all appointment button
        allRadio.setSelected(true);
        SearchRadioButton.setVisible(false);
        //sets the customerTable to all the all customers list that gets populated when the getCustomers method is called
        customerTable.setItems(allCustomers);
        customerIDCol.setCellValueFactory(new PropertyValueFactory<Customer, Integer>("customerID"));
        customerNameCol.setCellValueFactory(new PropertyValueFactory<Customer, String>("name"));
        AddressCol.setCellValueFactory(new PropertyValueFactory<Customer, String>("address"));
        postalCodeCol.setCellValueFactory(new PropertyValueFactory<Customer, String>("postalCode"));
        phoneNumberCol.setCellValueFactory(new PropertyValueFactory<Customer, String>("phoneNumber"));

        //this sets the list to the allappointments list that is created when the getAppointments method is called
        calendarTable.setItems(allAppointments);
        apptIDCol.setCellValueFactory(new PropertyValueFactory<Appointment, Integer>("appointmentID"));
        titleCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("title"));
        descCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("description"));
        locationCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("location"));
        contactCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("contactName"));
        typeCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("type"));
        startCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("startDateString"));
        endCol.setCellValueFactory(new PropertyValueFactory<Appointment, String>("endDateString"));
        apptCustomerID.setCellValueFactory(new PropertyValueFactory<Appointment, Integer>("customerID"));
        userIDCol.setCellValueFactory(new PropertyValueFactory<Appointment, Integer>("userID"));
        //this is the method that sets the appointment reminder label at the top of the application window
        upcomingAppointment();


    }


    /**
     * @param actionEvent
     * @throws IOException
     * handles events for when the add customer button is clicked
     */
    public void addCustomerClicked(ActionEvent actionEvent) throws IOException {
        //calls the hideStage method and then creates a new Main to then call the FXML loader
        hideStage();
        Main main = new Main();
        main.loadFXML("/TylerRichardSWII/fxmlscenes/addCustomer.fxml", "AMCalandar Manager - Add Customer");

    }


    /**
     * this method is use to quickly call in different parts of the controller to hide the current controller
     */
    public void hideStage() {
        Stage thisStage = (Stage) addCustomerButton.getScene().getWindow();
        thisStage.hide();
    }

    /**
     * @param actionEvent
     * @throws IOException
     * This method handles events for when the modify customer button is clicked
     */
    public void modifyCustomer(ActionEvent actionEvent) throws IOException {
        //checks the see if the table has an item selected. If it does, loads the new scene if not displays error


        if (!customerTable.getSelectionModel().isEmpty()) {
            selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
            Main main = new Main();
            main.loadFXML("/TylerRichardSWII/fxmlscenes/ModifyCustomer.fxml", "AMCalandar Manager - Modify Customer");
            hideStage();
        } else {
            errorMessage.setContentText("No user Selected");
            errorMessage.setTitle("Selected User Error");
            errorMessage.showAndWait();
        }

    }

    /**
     * @param actionEvent
     * @throws SQLException
     * This handles events for when the remove customer button is clicked
     */
    public void removeCustomerClicked(ActionEvent actionEvent) throws SQLException {
        //checks the see if the table has a selected customer. If so it prompts an alert letting the user know that
        //all appointments related to the customer will be deleted along with the customer. If the user confirms
        //delete it calls the removeCustomer method else displays error
        if (!customerTable.getSelectionModel().isEmpty()) {
            selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
            errorMessage.setAlertType(Alert.AlertType.CONFIRMATION);
            errorMessage.setContentText("Are you sure you want delete Customer: " + selectedCustomer.getName() + "\n" +
                    "Deleting this customer will delete all associated appointments");
            Optional<ButtonType> pressed = errorMessage.showAndWait();


            if (pressed.get() == ButtonType.OK) {
                removeCustomer(selectedCustomer);
            }
        } else {
            errorMessage.setContentText("No user Selected");
            errorMessage.setTitle("Selected User Error");
            errorMessage.showAndWait();
        }

    }


    /**
     * @param actionEvent
     * @throws IOException
     * This method handles events for when the add appointment button is clicked
     */
    public void AddAppointmentClick(ActionEvent actionEvent) throws IOException {
        //calls a main object to call the FXML load and the hides the class.
        Main menu = new Main();
        menu.loadFXML("/TylerRichardSWII/fxmlscenes/AddAppointment.fxml", "AMCalander Manger - Add Appointments");
        hideStage();
    }

    /**
     * @param actionEvent
     * this handles if the monthly view Radio button is selected
     * Lambda function here to do for each on the all appointments list
     */
    public void monthlyViewSelected(ActionEvent actionEvent) {
        //clears the monthappointments list just to make sure its clear prior to loading it with new data
        monthAppointments.clear();
        //gets the current ZoneId and then gets the current day and finally gets the first day of month and last day of
        //month based off of the current date
        ZoneId currentZone = ZoneId.systemDefault();
        LocalDate currentDay = LocalDate.now(currentZone);
        LocalDate firstDayOfMonth = currentDay.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDayOfMonth = currentDay.with(TemporalAdjusters.lastDayOfMonth());
        //loops though the allappointmnets list and cheacks if the appointment start date is after or equal to first date of month
        //and if the start time is before the last day of month or equal to last day of month and then adds it list
        allAppointments.forEach((a)->{if ((a.getStartTime().toLocalDate().isAfter(firstDayOfMonth) || a.getStartTime().toLocalDate().isEqual(firstDayOfMonth))
                    && (a.getStartTime().toLocalDate().isBefore(lastDayOfMonth) || a.getStartTime().toLocalDate().isEqual(lastDayOfMonth))) {
            monthAppointments.add(a);
            calendarTable.setItems(monthAppointments);}
                });


    }

    /**
     * @param actionEvent
     * handles the view all selected radio button.
     */
    public void viewAllSelected(ActionEvent actionEvent) {
        calendarTable.setItems(allAppointments);


    }


    /**
     * @param actionEvent
     * handles events when the weekly view is selected from the radio buttons
     * Lambda function here to do for each on the all appointments list
     */
    public void weeklyViewSelected(ActionEvent actionEvent) {
        //clears weekly appointments to make sure no left over data is there
        weekAppointments.clear();
        //gets the current date and then gets the previous or same sunday and next or same upcoming saturday
        ZoneId currentZone = ZoneId.systemDefault();
        LocalDate currentDay = LocalDate.now(currentZone);
        LocalDate previousSunday = currentDay.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        LocalDate nextSaturday = currentDay.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY));

            allAppointments.forEach((a) ->{if ((a.getStartTime().toLocalDate().isAfter(previousSunday) || a.getStartTime().toLocalDate().isEqual(previousSunday))
                    && (a.getStartTime().toLocalDate().isBefore(nextSaturday) || a.getStartTime().toLocalDate().isEqual(nextSaturday))) {
                weekAppointments.add(a); calendarTable.setItems(weekAppointments);}
            });
        }




    /**
     * @param actionEvent
     * @throws IOException
     * handle events for when the modify appt button is clicked.
     */
    public void ModifyApptClicked(ActionEvent actionEvent) throws IOException {

        //make sure the table has a selection and if so hides the stage and calls the fxml loader and sets the selected
        //appointment to the item selected. This will be stored local but then passed to new controller with a method
        if (!calendarTable.getSelectionModel().isEmpty()) {
            selectedAppointment = (Appointment) calendarTable.getSelectionModel().getSelectedItem();
            Main main = new Main();
            main.loadFXML("/TylerRichardSWII/fxmlscenes/ModifyAppointment.fxml", "AMCalandar Manager - Modify Appointment");
            hideStage();
        } else {
            errorMessage.setContentText("No Appointment Selected");
            errorMessage.setTitle("Selected Appointment Error");
            errorMessage.showAndWait();
        }

    }

    /**
     * @param actionEvent
     * @throws SQLException
     * handles events for to delete appointment button is selected
     */
    public void deleteApptClicked(ActionEvent actionEvent) throws SQLException {
        //makes sure that the selection is not empty. If not prompts the user for conformation of deletion. If okay the
        //the appointment is removed via the removeAppointment method. else diaply error message
        if (!calendarTable.getSelectionModel().isEmpty()) {
            selectedAppointment = (Appointment) calendarTable.getSelectionModel().getSelectedItem();
            errorMessage.setAlertType(Alert.AlertType.CONFIRMATION);
            errorMessage.setContentText("Are you sure you want to delete AppointmentID: " +selectedAppointment.getAppointmentID()
                    + " Title: " +  selectedAppointment.getTitle() + " Type: " + selectedAppointment.getType());
            Optional<ButtonType> pressed = errorMessage.showAndWait();
            if (pressed.get() == ButtonType.OK) {
                removeAppointments(selectedAppointment);
            }


        } else {
            errorMessage.setContentText("No Appointment Selected");
            errorMessage.setTitle("Selected Appointment Error");
            errorMessage.showAndWait();
        }
    }

    public void SearchClick(ActionEvent actionEvent) {
        searchAppointmentResults.clear();
        searchCustomerResults.clear();
        try {
            String searchString = SearchBox.getText();
            for (Appointment current : allAppointments) {
                Field[] allFields = current.getClass().getDeclaredFields();
                for(Field currentField : allFields){
                    currentField.setAccessible(true);
                    if(currentField.get(current).toString().toLowerCase().contains(searchString.toLowerCase())){
                        System.out.println(currentField.get(current));
                        searchAppointmentResults.add(current);
                        break;
                    }
                }

            }

            for (Customer current : allCustomers){
                Field[] allFields = current.getClass().getDeclaredFields();
                for(Field currentField : allFields){
                    currentField.setAccessible(true);
                    if(currentField.get(current).toString().toLowerCase().contains(searchString.toLowerCase())){
                        searchCustomerResults.add(current);
                        break;
                    }
                }

            }
        } catch (SecurityException | IllegalAccessException e) {
            e.printStackTrace();
        }
        if(searchCustomerResults.size()!=0){
            customerTable.setItems(searchCustomerResults);
        }

        if(searchAppointmentResults.size()!=0){
            calendarTable.setItems(searchAppointmentResults);
            SearchRadioButton.setSelected(true);


        }
    }

    public void SearchBubbleClicked(ActionEvent actionEvent) {
    }





    /**
     * @param actionEvent
     * @throws IOException
     * handles events for when you the reports button is clicked
     */
    public void reportsClicked(ActionEvent actionEvent) throws IOException {
        //calls main method to call loader method and then hides stage
        Main main = new Main();
        main.loadFXML("/TylerRichardSWII/fxmlscenes/Reports.fxml", "AMCalandar Manager - Reports");
        hideStage();
    }

    /**
     * @return the selectedAppointment this is used for the modify appt controllers to be able to get the selected appointment
     * from the main menu
     */
    public Appointment returnSelectedAppointment() {
        return selectedAppointment;
    }

    /**
     * @return the selectedCustomer this is used for the modify customer controller to be able to get the selected customer
     */
    public Customer returnSelectedCustomer() {
        return selectedCustomer;
    }

    /**
     * This method is used for the upcoming appointment notification
     */
    public void upcomingAppointment(){
        //gets local date and time and adds 15 minutes to it.
        ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.systemDefault());
        ZonedDateTime plusMinutes = currentTime.plusMinutes(15);

        //loops through all appointments if one is found that is before the 15 minutes or = to the 15 minutes it sets the label
        //and breaks the loop if it does not find one it sets the label to alert of no upcoming appointments
        for (Appointment a : allAppointments) {
            if ((a.getStartTime().isBefore(plusMinutes) || a.getStartTime().isEqual(plusMinutes)) && a.getEndTime().isAfter(plusMinutes)) {
                upcomingAppointmentLabel.setText("Upcoming Appointment: \n Appointment ID:" +a.getAppointmentID() + " Start Time: "
                + a.getStartTime().format(formatter) );
                upcomingAppointmentLabel.setTextFill(Color.RED);
                upcomingAppointmentLabel.setLayoutX(450);
                upcomingAppointmentLabel.setLayoutY(0);
                break;

           }else{
                upcomingAppointmentLabel.setText("No upcoming Appointments");
                upcomingAppointmentLabel.setLayoutX(500);
            }
        }
    }

    /**
     * @throws SQLException
     * for use to connect to db
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
     * gets all customers from databse to store in list
     */
    @Override
    public void getCustomer() throws SQLException {
        Customer currentCustomer;
        connectDB();
        try (PreparedStatement ps = conn.prepareStatement("SELECT * from customers")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String dbName = rs.getString("Customer_Name");
                String[] fullName = dbName.split(" ");
                currentCustomer = new Customer(fullName[0], fullName[1], rs.getNString("Address"),
                        rs.getNString("Postal_Code"), rs.getNString("Phone"),
                        rs.getInt("Division_ID"), rs.getInt("Customer_ID"));
                allCustomers.add(currentCustomer);


            }

        } catch (ArrayIndexOutOfBoundsException e) {
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
     * used to get all contacts and adds them to a list of all contacts
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
     * gets all appointments and adds them to the list of all appointments
     */
    @Override
    public void getAppointments() throws SQLException {
        connectDB();
        getContacts();
        allAppointments.clear();
        try (PreparedStatement ps = conn.prepareStatement("SELECT * from appointments")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                LocalDateTime start = (LocalDateTime) rs.getObject("Start");
                LocalDateTime end = (LocalDateTime) rs.getObject("End");
                ZonedDateTime localStart = Time.convertToLocalTime(start);
                ZonedDateTime localEnd = Time.convertToLocalTime(end);
                localStart.format(formatter);
                localEnd.format(formatter);
                Appointment existingAppointments = new Appointment(rs.getInt("Appointment_ID"), rs.getNString("Title"),
                        rs.getNString("Description"), rs.getNString("Location"), rs.getNString("type"),
                        localStart, localEnd, rs.getInt("Customer_ID"), rs.getInt("Contact_ID"),
                        rs.getInt("user_ID"));
                existingAppointments.setContactName(rs.getInt("Contact_ID"), allContacts);
                existingAppointments.setStartDateString(localStart);
                existingAppointments.setEndDateString(localEnd);
                allAppointments.add(existingAppointments);


            }
        }


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
     * @throws SQLException
     * removes a customer and any associated appointments that are for that customer
     */
    @Override
    public void removeCustomer(Customer customerRemove) throws SQLException {

        //connects to DB and deletes appointments with the same CustomerID as the Customer that is being deleted
        connectDB();
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM appointments WHERE Customer_ID = ?")) {
            ps.setInt(1, customerRemove.getCustomerID());
            ps.execute();

        }

        //deletes specified customer after the appointments for that customer are deleted.
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM customers WHERE Customer_ID = ?")) {
            ps.setInt(1, customerRemove.getCustomerID());
            ps.execute();
            allAppointments.clear();
            getAppointments();
            allCustomers.clear();
            getCustomer();
        }


    }

    /**
     * @param appointmentRemove
     * @throws SQLException
     *
     */
    @Override
    public void removeAppointments(Appointment appointmentRemove) throws SQLException {
        connectDB();
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM appointments WHERE Appointment_ID = ?")) {
            ps.setInt(1, appointmentRemove.getAppointmentID());
            ps.execute();
            getAppointments();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}