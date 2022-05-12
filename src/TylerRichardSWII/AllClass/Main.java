package TylerRichardSWII.AllClass;


import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static javafx.fxml.FXMLLoader.load;

/**
 * Main method of application
 */
public class Main extends Application {


    @Override
    public void start(Stage mainStage) throws Exception {
        loadFXML("/TylerRichardSWII/fxmlscenes/Login.fxml", "AMCalandar Manager - Login");

    }

    public static void Main(String[] args) {
        launch(args);

    }

    /**
     * @param fileName Name of FXML to load
     * @param title - Title to set on the menu of the application
     * @throws IOException
     */
    public void loadFXML(String fileName, String title) throws IOException {
        Stage mainStage = new Stage();
        Parent mainParent = load(getClass().getResource(fileName));
        Scene mainScene = new Scene(mainParent);
        mainStage.setScene(mainScene);
        mainStage.setTitle(title);
        mainStage.show();
    }

}