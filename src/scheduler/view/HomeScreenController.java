package scheduler.view;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import scheduler.MainApp;

import java.io.IOException;


public class HomeScreenController {



    @FXML
    private ChoiceBox<String> choiceBox;

    @FXML
    private Button addCustomer;


    private static MainApp mainApp;
    private static String currentUserName;


/*

    public HomeScreenController (String usr) {
        this.currentUserName = usr;
    }
*/



    public void initialize(){
        choiceBox.getItems().addAll("Weekly", "Monthly");
        choiceBox.setValue("Weekly");

        System.out.println("The current user when HomeScreenController is first initialized is: " + currentUserName);
    }

    public void setMainApp (MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    public void showAddPerson() throws IOException{
        mainApp.showAddCustomerScreen(currentUserName);

    }

    public String getCurrentUserName() {
        return currentUserName;
    }

    public void setCurrentUserName(String currentUserName) {
        this.currentUserName = currentUserName;
    }
}

