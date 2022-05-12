package TylerRichardSWII.Controllers;


import TylerRichardSWII.AllClass.Appointment;
import TylerRichardSWII.AllClass.Customer;
import TylerRichardSWII.AllClass.Main;
import TylerRichardSWII.Util.TimeConverter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import TylerRichardSWII.Util.dbCommands;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.*;

import static java.util.Arrays.stream;


/**
 * Login controller handles events from the login screen that is launched after the main method is launched
 */
public class loginController implements Initializable, dbCommands {
    @FXML
    public TextField usernameField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public Button loginButton;
    @FXML
    public Button exitButton;
    @FXML
    public Label locationLabel;
    @FXML
    public Button registerNow;
    @FXML
    public Label userNameLabel;
    @FXML
    public Label passwordLabel;
    @FXML
    public Label locLabel;
    @FXML
    public Label titleLabel;
    @FXML
    public Label newUserLabel;

    private final String language = Locale.getDefault().getLanguage();
    private Connection conn;
    private final Alert errorMessage = new Alert(Alert.AlertType.NONE);
    private static String un;


    /**
     * @param url
     * @param rb
     * default method that handles events on launch of sceene
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //if the launguage of the computer is french sets the login screen to french along with text and error message
        if (language.contains("fr")) {
            titleLabel.setText("Système De Gestion De Calendrier");
            userNameLabel.setText("Nom d'utilisateur:");
            userNameLabel.setLayoutX(170);
            passwordLabel.setText("Le mot de passe:");
            passwordLabel.setLayoutX(175);
            loginButton.setText("Connexion");
            exitButton.setText("Sortir");
            locLabel.setText("lieu");
        }
        TimeConverter location = new TimeConverter();
        locationLabel.setText(location.getTimeZone());


    }


    /**
     * @param actionEvent
     * @throws SQLException
     * @throws IOException
     * Handles the events when the login button is clicked
     */
    public void loginButtonClicked(ActionEvent actionEvent) throws SQLException, IOException {
        //calls the validateLogin method and the login Attempt method. If login is true it hides the login stage and
        //loads the main stage
        boolean login = validateLogin();
        logAttempts(login);
        if (login) {
            hideStage();
            Main menu = new Main();
            menu.loadFXML("/TylerRichardSWII/fxmlscenes/MainMenu.fxml", "AMCalendar Manager - Main Menu");

        }


    }

    /**
     * @param actionEvent closes the application if the exit button is clicked
     */
    public void exitButtonClicked(ActionEvent actionEvent) {
        System.exit(0);
    }


    /**
     * @param login is if the login in was successful or failed
     */
    public void logAttempts(boolean login) {
        //converts the current time to utc if login is true sets the string to success or if its false sets the string
        //to failed then it tries to write to the log file.
        TimeConverter time = new TimeConverter();
        String utcTime = time.convertToUTC();
        String username = usernameField.getText();
        String successOrFail = "N/A";
        if (login) {
            successOrFail = "Success";
        } else {
            successOrFail = "Failed";
        }
        try (BufferedWriter logWriter = new BufferedWriter(new FileWriter("login_activity.txt", true))) {
            logWriter.write("Username: " + username + " , " + "Date: " + utcTime + " , " + "Status: " + successOrFail + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * HideStage method is used throughout the application to quickly call to hide the currrent stage.
     */
    public void hideStage() {
        Stage thisStage = (Stage) userNameLabel.getScene().getWindow();
        thisStage.hide();
    }

    /**
     * @return the username that was logged in with to other controllers to use to log info in DB
     */
    public String passUserName() {
        return un;
    }


    /**
     * Part of the dbCommands interface
     */
    @Override
    public void connectDB() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/client_schedule";
        String username = "sqlUser";
        String password = "Passw0rd!";
        conn = DriverManager.getConnection(url, username, password);

    }

    /**
     * not used in this controller
     * @param newCustomer
     * @throws SQLException
     */
    @Override
    public void addCustomer(Customer newCustomer) throws SQLException {
    }

    /**
     * @throws SQLException
     * not used in this controller
     */
    @Override
    public void getCountries() throws SQLException {
    }

    /**
     * @throws SQLException
     * not used in this controller
     */
    @Override
    public void getStateOrProvince() throws SQLException {
    }

    /**
     * Part of the dbCommands interface used to send the info entered the login screen to the db to validate info
     * @return
     * @throws SQLException
     */
    @Override
    public boolean validateLogin() throws SQLException {
        //calls the connectdb command and grabs the username and password from the fields
        connectDB();
        String username = usernameField.getText();
        String password = passwordField.getText();

        // tries to query the username and password from the username field and provides the data collected above to
        //see if any results are found
        try (PreparedStatement ps = conn.prepareStatement("SELECT User_Name,Password FROM users" +
                " WHERE User_Name = ? AND Password  = ? ")) {
            ps.setObject(1, username);
            ps.setObject(2, password);
            ResultSet rs = ps.executeQuery();
            //returns true if results are found
            if (rs.next()) {
                un = rs.getNString("User_Name");
                return true;
            // returns an error message in english if the login failed and system language is english
            } else {
                errorMessage.setAlertType(Alert.AlertType.ERROR);
                if (Locale.getDefault().getLanguage().contains("en")) {
                    errorMessage.setContentText("Invalid username or password");
                    errorMessage.setTitle("Invalid Login");
                    errorMessage.showAndWait();
                    return false;

                    //returns error message in false if the system language is french and the login failed
                } else if (Locale.getDefault().getLanguage().contains("fr")) {
                    errorMessage.setContentText("Nom d'utilisateur ou mot de passe inclus");
                    errorMessage.setTitle("Identifiant invalide");
                    errorMessage.showAndWait();
                    return false;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;

    }

    /**
     * @throws SQLException
     * not used in this controller
     */
    @Override
    public void getCustomer() throws SQLException {
    }

    /**
     * @param newAppointment
     * @throws SQLException
     * not used in this controller
     */
    @Override
    public void addAppointment(Appointment newAppointment) throws SQLException {
    }

    /**
     * @throws SQLException
     * not used in this controller
     */
    @Override
    public void getContacts() throws SQLException {
    }

    /**
     * @throws SQLException
     * not used in this controller
     */
    @Override
    public void getAppointments() throws SQLException {
    }

    /**
     * @throws SQLException
     * not used in this controller
     */
    @Override
    public void getUserID() throws SQLException {
    }

    /**
     * @param customerRemove
     * not used in this controller
     */
    @Override
    public void removeCustomer(Customer customerRemove) {
    }

    /**
     * @param appointmentRemove
     * not used in this controller
     */
    @Override
    public void removeAppointments(Appointment appointmentRemove) {
    }
}

