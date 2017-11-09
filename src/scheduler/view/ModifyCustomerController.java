package scheduler.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import scheduler.MainApp;

import java.io.IOException;

public class ModifyCustomerController {

    private Stage dialogStage;


    @FXML
    private TextField customerIdField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField street1Field;

    @FXML
    private TextField street2Field;

    @FXML
    private TextField cityField;

    @FXML
    private TextField countryField;

    @FXML
    private TextField postalCodeField;

    @FXML
    private TextField phoneNumberField;

    @FXML
    private Button save;

    @FXML
    private Button cancel;

    private Stage modifyCustomerScreenStage;

    private static MainApp mainApp;


    @FXML
    public void handleCancel() {
        modifyCustomerScreenStage.close();

    }

    public void initialize(){

    }

    public void setModifyCustomerScreenStage (Stage dialogStage){
        this.modifyCustomerScreenStage = dialogStage;
    }

    public void setMainApp (MainApp mainApp) {
        this.mainApp = mainApp;
    }

}
