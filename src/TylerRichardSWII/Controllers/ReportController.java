package TylerRichardSWII.Controllers;

import TylerRichardSWII.AllClass.Appointment;
import TylerRichardSWII.AllClass.Contact;
import TylerRichardSWII.AllClass.Customer;
import TylerRichardSWII.AllClass.Main;
import TylerRichardSWII.Util.TimeConverter;
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

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.DateFormatSymbols;
import java.time.*;
import java.util.Calendar;
import java.util.ResourceBundle;




/**
 * controller used for reports GUI
 */
public class ReportController implements dbCommands, Initializable {





    /**
     * this inner class is used to generate the first required report
     */
    public class reportOne {
        private Integer month;
        private String type;
        private Integer count;


        /**
         * @param month - month to search for
         * @param type - type of appointment
         * @param count - count of those types of appointments
         */
        public reportOne(Integer month, String type, Integer count) {
            this.month = month;
            this.type = type;
            this.count = count;

        }

        /**
         * @return returns month
         */
        public Integer getMonth() {
            return month;
        }

        /**
         * @param month sets months
         */
        public void setMonth(Integer month) {
            this.month = month;
        }

        /**
         * @return returns type of appointment
         */
        public String getType() {
            return type;
        }

        /**
         * @param type sets type of appointment
         */
        public void setType(String type) {
            this.type = type;
        }

        /**
         * @return returns the amount of appointments created of this type
         */
        public Integer getCount() {
            return count;
        }

        /**
         * @param count sets the count for how many of the same type of appointments were done
         *
         */
        public void setCount(Integer count) {
            this.count = count;
        }
    }

    @FXML
    public TableView reportTable;
    @FXML
    public TableColumn column1;
    @FXML
    public TableColumn column2;
    @FXML
    public TableColumn column3;
    @FXML
    public TableColumn column4;
    @FXML
    public TableColumn column5;
    @FXML
    public TableColumn column6;
    @FXML
    public TableColumn column7;
    @FXML
    public ComboBox ReportCBox;
    @FXML
    public Label subReportType;
    @FXML
    public ComboBox subreportTypeCBox;
    @FXML
    public Button backButton;


    private Connection conn;
    ObservableList<String> reportTypeList = FXCollections.observableArrayList();
    ObservableList<String> subReportList = FXCollections.observableArrayList();
    ObservableList<Contact> allContacts = FXCollections.observableArrayList();
    ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    ObservableList<Appointment> filteredAppointments = FXCollections.observableArrayList();
    ObservableList<reportOne> report = FXCollections.observableArrayList();
    TimeConverter time = new TimeConverter();

    /**
     * @param url
     * @param resourceBundle
     * handles launch events
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //gets the contacts and appointments as these are going to be used to generate the report
        try {
            getContacts();
            getAppointments();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //sets the list to the type of available reports
        reportTypeList.add("Appointments by Type and Month");
        reportTypeList.add("Appointments by Contact");
        reportTypeList.add("Today's Appointments");
        ReportCBox.setItems(reportTypeList);
        //hides the report submenu by default and all columns
        subReportType.setVisible(false);
        subreportTypeCBox.setVisible(false);
        column1.setVisible(false);
        column2.setVisible(false);
        column3.setVisible(false);
        column4.setVisible(false);
        column5.setVisible(false);
        column6.setVisible(false);
        column7.setVisible(false);



    }


    /**
     * @param actionEvent
     * @throws SQLException
     * handles events for the report type selected
     */
    public void selectedReportType(ActionEvent actionEvent) throws SQLException {
        String choice = null;
        //gets the report type user is looking for
        if (!ReportCBox.getSelectionModel().isEmpty()) {
            choice = (String) ReportCBox.getSelectionModel().getSelectedItem();

        }
        //if coice is type and month adds the months to the subreportlist to allow the user to select the month they want
        //report on.
        if (choice.equalsIgnoreCase("Appointments by Type and Month")) {

            subReportList.clear();
            subReportList.add("January");
            subReportList.add("February");
            subReportList.add("March");
            subReportList.add("April");
            subReportList.add("May");
            subReportList.add("June");
            subReportList.add("July");
            subReportList.add("August");
            subReportList.add("September");
            subReportList.add("October");
            subReportList.add("November");
            subReportList.add("December");
            subreportTypeCBox.setItems(subReportList);
            subreportTypeCBox.setVisible(true);
            subReportType.setVisible(true);
            subReportType.setText("Month:");
            subReportType.setLayoutX(45);

            //if user wants appointment by contacts this shows the contacts avaiable from the DB
        } else if (choice.equalsIgnoreCase("Appointments by Contact")) {
            subReportList.clear();
            subreportTypeCBox.setItems(allContacts);
            subReportType.setText("Contact:");
            subReportType.setLayoutX(40);
            subreportTypeCBox.setVisible(true);
            subReportType.setVisible(true);

            //if user wants a list of todays appointments this will get them from the all appointments list.
        } else if (choice.equalsIgnoreCase("Today's Appointments")) {
            subReportType.setVisible(false);
            subreportTypeCBox.setVisible(false);
            reportTable.getItems().clear();
            filteredAppointments.clear();
            for (Appointment a : allAppointments) {
                if (time.getLocalTimeNow().toLocalDate().isEqual(a.getStartTime().toLocalDate())) {
                    filteredAppointments.add(a);
                }
            }
            //this sets the table for the todays appointments reports
            reportTable.setItems(filteredAppointments);
            column1.setText("Appt ID");
            column1.setVisible(true);
            column1.setPrefWidth(50);
            column1.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
            column2.setText("Title");
            column2.setVisible(true);
            column2.setPrefWidth(75);
            column2.setCellValueFactory(new PropertyValueFactory<>("title"));
            column3.setText("Type");
            column3.setVisible(true);
            column3.setPrefWidth(75);
            column3.setCellValueFactory(new PropertyValueFactory<>("type"));
            column4.setText("Description");
            column4.setVisible(true);
            column4.setPrefWidth(100);
            column4.setCellValueFactory(new PropertyValueFactory<>("description"));
            column5.setText("Start");
            column5.setVisible(true);
            column5.setPrefWidth(125);
            column5.setCellValueFactory(new PropertyValueFactory<>("startDateString"));
            column6.setText("End");
            column6.setVisible(true);
            column6.setPrefWidth(125);
            column6.setCellValueFactory(new PropertyValueFactory<>("endDateString"));
            column7.setText("Cust ID");
            column7.setVisible(true);
            column7.setPrefWidth(50);
            column7.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        }
    }


    /**
     * @param actionEvent
     * handles selection events from the submenu options.
     */
    public void selectedSubReportType(ActionEvent actionEvent) {

        Integer numMonth = 0;

        try {
            //if appointment by month is selected sets the sub label to be months and then sets visable the two columns
            //needed for the report.
            if (!subreportTypeCBox.getSelectionModel().isEmpty() &&
                    ReportCBox.getSelectionModel().getSelectedItem().equals("Appointments by Type and Month")) {
                reportTable.getItems().clear();
                column1.setVisible(true);
                column2.setVisible(true);
                column3.setVisible(false);
                column4.setVisible(false);
                column5.setVisible(false);
                column6.setVisible(false);
                column7.setVisible(false);
                column1.setText("Appointment Type");
                column2.setText("Number of Appointments");
                column1.setPrefWidth(200);
                column2.setPrefWidth(200);
                if (!subreportTypeCBox.getSelectionModel().isEmpty()) {
                    String month = subreportTypeCBox.getSelectionModel().getSelectedItem().toString();
                    //switch statement takes the selected month and assigns it the int value of the month selected
                    switch (month) {
                        case "January":
                            numMonth = 1;
                            break;
                        case "February":
                            numMonth = 2;
                            break;
                        case "March":
                            numMonth = 3;
                            break;
                        case "April":
                            numMonth = 4;
                            break;
                        case "May":
                            numMonth = 5;
                            break;
                        case "June":
                            numMonth = 6;
                            break;
                        case "July":
                            numMonth = 7;
                            break;
                        case "August":
                            numMonth = 8;
                            break;
                        case "September":
                            numMonth = 9;
                            break;
                        case "October":
                            numMonth = 10;
                            break;
                        case "November":
                            numMonth = 11;
                            break;
                        case "December":
                            numMonth = 12;
                            break;

                    }
                }
                //the method below will query the database for this information
                getTypeAndMonthReport(numMonth);

                //this handles if the main report is appointments by contacts. It sets the submenu label to be contact
                //and the cbox to be filled with the contact name. Once a contact is selected it compares the selected name to
                //all names then gets the contact id and compares all appointments contactID with the selected. When found
                //adds it to the filteredAppointments list.
            } else if (!subreportTypeCBox.getSelectionModel().isEmpty() &&
                    ReportCBox.getSelectionModel().getSelectedItem().equals("Appointments by Contact")) {
                reportTable.getItems().clear();
                filteredAppointments.clear();
                String contactName = subreportTypeCBox.getSelectionModel().getSelectedItem().toString();
                for (Contact c : allContacts) {
                    if (c.getName().equalsIgnoreCase(contactName)) {
                        for (Appointment a : allAppointments) {
                            if (c.getContactID() == a.getContactID()) {
                                filteredAppointments.add(a);
                            }
                        }
                    }
                }

                //sets the table and columns for the filtered appointments list
                reportTable.setItems(filteredAppointments);
                column1.setText("Appt ID");
                column1.setVisible(true);
                column1.setPrefWidth(50);
                column1.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
                column2.setText("Title");
                column2.setVisible(true);
                column2.setPrefWidth(75);
                column2.setCellValueFactory(new PropertyValueFactory<>("title"));
                column3.setText("Type");
                column3.setVisible(true);
                column3.setPrefWidth(75);
                column3.setCellValueFactory(new PropertyValueFactory<>("type"));
                column4.setText("Description");
                column4.setVisible(true);
                column4.setPrefWidth(100);
                column4.setCellValueFactory(new PropertyValueFactory<>("description"));
                column5.setText("Start");
                column5.setVisible(true);
                column5.setPrefWidth(125);
                column5.setCellValueFactory(new PropertyValueFactory<>("startDateString"));
                column6.setText("End");
                column6.setVisible(true);
                column6.setPrefWidth(125);
                column6.setCellValueFactory(new PropertyValueFactory<>("endDateString"));
                column7.setText("Cust ID");
                column7.setVisible(true);
                column7.setPrefWidth(50);
                column7.setCellValueFactory(new PropertyValueFactory<>("customerID"));

            }

        } catch (NumberFormatException | SQLException e) {
            e.printStackTrace();
        }


    }
    public void backButtonClicked(ActionEvent actionEvent) throws IOException {
        //hides stage and loads main meun

        hideStage();
        Main main = new Main();
        main.loadFXML("/TylerRichardSWII/fxmlscenes/MainMenu.fxml", "AMCalandar Manager - Main Menu");
    }
    public void hideStage() {
        Stage thisStage = (Stage) backButton.getScene().getWindow();
        thisStage.hide();
    }


    /**
     * @param month
     * @throws SQLException
     * Query the Database for the first required report
     */
    private void getTypeAndMonthReport(int month) throws SQLException {
        //clears the reports list. query the DB and gets the month from start and counts the appointment ID and
        //filters the results based off of the month that was selected above finally it groups them by type.
        //creates a report object for each entry found and then adds it to the reports list. also sets the table
        connectDB();
        report.clear();

        try (PreparedStatement ps = conn.prepareStatement("SELECT month(Start) as Month, count(Appointment_ID) as Count" +
                " ,Type FROM appointments WHERE Month(Start) = ? GROUP BY Month(start),type")) {
            ps.setInt(1, month);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reportOne newReport = new reportOne(rs.getInt("Month"), rs.getNString("Type"), rs.getInt("Count"));
                report.add(newReport);

            }
        }
        reportTable.setItems(report);
        column1.setCellValueFactory(new PropertyValueFactory<>("type"));
        column2.setCellValueFactory(new PropertyValueFactory<>("count"));
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
     * gets all contacts stores them in list
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
     * gets all appointment stores them in list
     */
    @Override
    public void getAppointments() throws SQLException {
        connectDB();
        try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM appointments")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                LocalDateTime start = (LocalDateTime) rs.getObject("Start");
                LocalDateTime end = (LocalDateTime) rs.getObject("End");
                ZonedDateTime finalStart = time.convertToLocalTime(start);
                ZonedDateTime finalEnd = time.convertToLocalTime(end);
                Appointment newAppointment = new Appointment(rs.getInt("Appointment_ID"),
                        rs.getNString("Title"), rs.getString("Description"),
                        rs.getNString("Location"), rs.getNString("Type"), finalStart, finalEnd,
                        rs.getInt("Customer_ID"), rs.getInt("Contact_ID"),rs.getInt("User_ID"));
                newAppointment.setStartDateString(finalStart);
                newAppointment.setEndDateString(finalEnd);
                allAppointments.add(newAppointment);

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
     * not used
     */
    @Override
    public void removeCustomer(Customer customerRemove) throws SQLException {
    }

    /**
     * @param appointmentRemove
     * @throws SQLException
     * not used
     */
    @Override
    public void removeAppointments(Appointment appointmentRemove) throws SQLException {
    }


}


