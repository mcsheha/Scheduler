package scheduler.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import scheduler.MainApp;
import scheduler.Utilities.SQLParser;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ModifyCustomerController {

    private Stage dialogStage;

    @FXML
    private Label titleLabel;

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

    @FXML
    private CheckBox isActiveCheckBox;

    private Stage modifyCustomerScreenStage;

    private static MainApp mainApp;

    private static SQLParser sqlParser;

    private static String currentUserName;


    @FXML
    public void handleCancel() {
        modifyCustomerScreenStage.close();

    }

    public void initialize(){
        //sqlParser.setCurrentUserName(currentUserName);
        System.out.println("The currentUserName of sqlParser after initializing ModifyCustomerController is: " + sqlParser.getCurrentUserName());

    }

    public void setModifyCustomerScreenStage (Stage dialogStage){
        this.modifyCustomerScreenStage = dialogStage;
    }

    public void setMainApp (MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setSqlParser (SQLParser sqlParser, String userName) {
        this.sqlParser = sqlParser;
        this.currentUserName = userName;
    }


    public void setTitleLabel(String label) {
        titleLabel.setText(label);
    }



    public boolean isInputValid() {
        String errorMessage = "";
        boolean dataTypesCorrect = true;

        //Ensure no blank fields: ID, Name, Street address 1, City, Country, Postal Code, Phone)
        if (customerIdField.getText() == null || customerIdField.getText().length() == 0) {
            errorMessage += "No valid customer ID!\n";
        }
        if (nameField.getText() == null || nameField.getText().length() == 0) {
            errorMessage += "No valid customer name!\n";
        }
        if (street1Field.getText() == null || street1Field.getText().length() == 0) {
            errorMessage += "No valid street address!\n";
        }
        if (cityField.getText() == null || cityField.getText().length() == 0) {
            errorMessage += "No valid city!\n";
        }
        if (countryField.getText() == null || countryField.getText().length() == 0) {
            errorMessage += "No valid country!\n";
        }
        if (postalCodeField.getText() == null || postalCodeField.getText().length() == 0) {
            errorMessage += "No valid postal code!\n";
        }
        if (phoneNumberField.getText() == null || phoneNumberField.getText().length() == 0) {
            errorMessage += "No valid phone number!\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(modifyCustomerScreenStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);

            alert.showAndWait();

            return false;

        }

    }

    @FXML
    private void handleCustomerSave () {
        if (isInputValid()){
            //System.out.println("It's all good, baby!");
            int customerId = sqlParser.findLowestAvailableID("customer");
            String name = nameField.getText();
            String streetAddress1 = street1Field.getText();
            String streetAddress2 = street2Field.getText();
            String city = cityField.getText();
            String country = countryField.getText();
            String postalCode = postalCodeField.getText();
            String phone = phoneNumberField.getText();
            boolean isActiveChecked = isActiveCheckBox.isSelected();
            int isActive;
            if (isActiveChecked) {isActive = 1;} else {isActive = 0;}
            int countryId;
            int cityId;
            int addressId;

            countryId = sqlParser.getCountryId(country);
            cityId = sqlParser.getCityId(city, countryId);
            addressId = sqlParser.getAddressId(streetAddress1, streetAddress2, cityId, postalCode, phone);
            String currentUser = currentUserName;
            //String currentUser = "mike";


            if (!(countryId >= 1)) {
                System.out.println("The countryId is not greater than, or equal to 1");
            }
            if (!(cityId >= 1)) {
                System.out.println("The cityId is not greater than, or equal to 1");
            }
            if (!(addressId >= 1)) {
                System.out.println("The addressId is not greater than, or equal to 1");
            }

            String sql = "INSERT INTO customer (customerId, customerName, addressId, active, createDate, " +
                    "createdBy, lastUpdate, lastUpdateBy) VALUES (?,?,?,?,?,?,?,?)";

            PreparedStatement psmt = null;

            try {
                Connection dBase = mainApp.getDb().getConnection();
                psmt = dBase.prepareStatement(sql);
                psmt.setInt(1, customerId);
                psmt.setString(2, name);
                psmt.setInt(3, addressId);
                psmt.setInt(4, isActive);
                psmt.setString(5, sqlParser.nowUtcAsString());
                psmt.setString(6, currentUser);
                psmt.setString(7, sqlParser.nowUtcAsString());
                psmt.setString(8, currentUser);
                psmt.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
            }


        }

    }

    public static String getCurrentUserName() {
        return currentUserName;
    }

    public static void setCurrentUserName(String currentUserName) {
        ModifyCustomerController.currentUserName = currentUserName;
    }
}


