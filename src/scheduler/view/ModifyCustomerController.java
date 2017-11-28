package scheduler.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import scheduler.MainApp;
import scheduler.Utilities.SQLParser;

import javax.xml.soap.Text;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import static java.time.ZoneOffset.UTC;

public class ModifyCustomerController {


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
        customerIdField.setText(String.valueOf(findLowestAvailableID("customer")));


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
            int customerId = findLowestAvailableID("customer");
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

            countryId = getCountryId(country);
            cityId = getCityId(city, countryId);
            addressId = getAddressId(streetAddress1, streetAddress2, cityId, postalCode, phone);
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
                psmt.setString(5, nowUtcAsString());
                psmt.setString(6, currentUser);
                psmt.setString(7, nowUtcAsString());
                psmt.setString(8, currentUser);
                psmt.executeUpdate();
                modifyCustomerScreenStage.close();
                HomeScreenController.addToCustomerList(customerId, name);

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


    public boolean checkIfInTable(String searchTerm, String columnName, String tableName) {
        boolean rtn = false;
        Connection dBase = mainApp.getDb().getConnection();

        String queryString = "SELECT " + columnName + " FROM " + tableName;

        try {
            Statement statement = dBase.prepareStatement(queryString);
            statement.executeQuery(queryString);
            ResultSet rs = statement.getResultSet();

            while(rs.next()){
                String tempString = rs.getString(columnName);
                if (Objects.equals(tempString,searchTerm)){
                    rtn = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rtn;
    }

    public int findLowestAvailableID (String tableName) {
        Connection dBase = MainApp.getDb().getConnection();

        String columnName = tableName + "Id";
        String queryString = "SELECT " + columnName + " FROM " + tableName +";";
        int rtnInt = 1;
        ArrayList<Integer> arrayList = new ArrayList<>();

        try {
            Statement statement = dBase.prepareStatement(queryString);
            statement.executeQuery(queryString);
            ResultSet rs = statement.getResultSet();

            while(rs.next()){
                arrayList.add(rs.getInt(columnName));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Collections.sort(arrayList);

        while (arrayList.contains(rtnInt)) {
            rtnInt++;
        }
        return rtnInt;


    }


    // Check if the specified country is already in the table and returns the country ID.
    // If not already in table calls addCountry and returns new country ID.
    public int getCountryId(String country) {
        int countryId = 0;
        Connection dBase = mainApp.getDb().getConnection();
        String queryString = "SELECT countryId FROM country where country = " + "'" + country + "';";

        int storedCountryId = 0;
        if (checkIfInTable(country, "country", "country")) {
            try {
                PreparedStatement psmt = dBase.prepareStatement(queryString);
                psmt.executeQuery();
                ResultSet rs = psmt.getResultSet();

                while (rs.next()) {
                    countryId = rs.getInt("countryId");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            countryId = addCountry(country);
        }

        return countryId;
    }


    //Adds new country to the DB
    private int addCountry(String countryToAdd) {
        int countryId = findLowestAvailableID("country");
        String currentUser = currentUserName;
        //String currentUser = "mike";


        String sql = "INSERT INTO country (countryId, country, createDate, createdBy, lastUpdate, lastUpdateBy) " +
                "VALUES (?,?,?,?,?,?)";

        PreparedStatement psmt = null;

        try {
            Connection dBase = mainApp.getDb().getConnection();
            psmt = dBase.prepareStatement(sql);
            psmt.setInt(1, countryId);
            psmt.setString(2, countryToAdd);
            psmt.setString(3, nowUtcAsString());
            psmt.setString(4, currentUser);
            psmt.setString(5, nowUtcAsString());
            psmt.setString(6, currentUser);
            psmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return countryId;
    }


    // Check if the specified city is already in the table and returns the city ID.
    // If not already in table calls addCity and returns new city ID.
    public int getCityId(String city, int countryId) {
        int cityId = 0;
        Connection dBase = mainApp.getDb().getConnection();
        String queryString = "SELECT cityId FROM city where city = " + "'" + city + "';";

        int storedCityId = 0;
        if (checkIfInTable(city, "city", "city")) {
            try {
                PreparedStatement psmt = dBase.prepareStatement(queryString);
                psmt.executeQuery();
                ResultSet rs = psmt.getResultSet();

                while (rs.next()) {
                    cityId = rs.getInt("cityId");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            cityId = addCity(city, countryId);
        }

        return cityId;
    }


    //Adds new city to the DB
    private int addCity(String cityToAdd, int countryId) {
        int cityId = findLowestAvailableID("city");
        String currentUser = currentUserName;
        //String currentUser = "mike";

        String sql = "INSERT INTO city (cityId, city, countryId, createDate, createdBy, lastUpdate, lastUpdateBy) " +
                "VALUES (?,?,?,?,?,?,?)";

        PreparedStatement psmt = null;

        try {
            Connection dBase = mainApp.getDb().getConnection();
            psmt = dBase.prepareStatement(sql);
            psmt.setInt(1, cityId);
            psmt.setString(2, cityToAdd);
            psmt.setInt(3, countryId);
            psmt.setString(4, nowUtcAsString());
            psmt.setString(5, currentUser);
            psmt.setString(6, nowUtcAsString());
            psmt.setString(7, currentUser);
            psmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cityId;
    }


    // Check if the specified address is already in the table and returns the address ID.
    // If not already in table calls addAddress and returns new address ID.
    public int getAddressId(String address, String address2, int cityId, String postalCode, String phone) {
        int addressId = 0;
        Connection dBase = mainApp.getDb().getConnection();
        String queryString = "SELECT addressId FROM address where address = " + "'" + address + "';";

        int storedAddressId = 0;
        if (checkIfInTable(address, "address", "address")) {
            try {
                PreparedStatement psmt = dBase.prepareStatement(queryString);
                psmt.executeQuery();
                ResultSet rs = psmt.getResultSet();

                while (rs.next()) {
                    addressId = rs.getInt("addressId");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            addressId = addAddress(address, address2, cityId, postalCode, phone);
        }

        return addressId;
    }


    //Adds new address to the DB
    private int addAddress(String addressToAdd, String address2ToAdd, int cityId, String postalCode, String phone) {
        int addressId = findLowestAvailableID("address");
        String currentUser = currentUserName;
        //String currentUser = "mike";

        String sql = "INSERT INTO address (addressId, address, address2, cityId, postalCode, phone, createDate, " +
                "createdBy, lastUpdate, lastUpdateBy) VALUES (?,?,?,?,?,?,?,?,?,?)";

        PreparedStatement psmt = null;

        try {
            Connection dBase = mainApp.getDb().getConnection();
            psmt = dBase.prepareStatement(sql);
            psmt.setInt(1, addressId);
            psmt.setString(2, addressToAdd);
            psmt.setString(3, address2ToAdd);
            psmt.setInt(4, cityId);
            psmt.setString(5, postalCode);
            psmt.setString(6, phone);
            psmt.setString(7, nowUtcAsString());
            psmt.setString(8, currentUser);
            psmt.setString(9, nowUtcAsString());
            psmt.setString(10, currentUser);
            psmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return addressId;
    }


    //returns current date and time in UTC as a string.
    public String nowUtcAsString() {

        LocalDateTime currentDateTime = LocalDateTime.now(UTC);
        return currentDateTime.toString().replace("T", " ").substring(0,21);

    }

    //returns current date and time in UTC as a string.
    public String nowLocalAsString () {

        LocalDateTime currentDateTime = LocalDateTime.now();
        return currentDateTime.toString().replace("T", " ").substring(0,21);

    }

}


