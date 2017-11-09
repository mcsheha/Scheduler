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

    public void initialize(){
        choiceBox.getItems().addAll("Weekly", "Monthly");
        choiceBox.setValue("Weekly");
    }

    public void setMainApp (MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    public void showAddPerson() throws IOException{
        mainApp.showAddCustomerScreen();

    }





}

