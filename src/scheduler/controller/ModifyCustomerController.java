package scheduler.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import scheduler.model.Customer;
import scheduler.model.DbConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

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

    //private static MainApp mainApp;

    private static String currentUserName;

    private boolean isNewCustomer = true;

    private Customer selectedCustomer;

    private static Connection dbConnect;

    private static CustomerTabController customerTabController;



    @FXML
    public void handleCancel() {
        modifyCustomerScreenStage.close();

    }

    public void initialize(){
        dbConnect = DbConnection.getInstance().getConnection();
        customerTabController = CustomerTabController.getInstance();

        //System.out.println("The currentUserName of sqlParser after initializing ModifyCustomerController is: " + sqlParser.getCurrentUserName());

    }

    public void setModifyCustomerScreenStage (Stage dialogStage){
        this.modifyCustomerScreenStage = dialogStage;
    }

    //public void setMainApp (MainApp mainApp) {
    //    this.mainApp = mainApp;
    //}
/*

    public void setMainApp () {
        this.mainApp = MainApp.getInstance();
    }
*/


    public void setIsActiveCheckBox (boolean bool) {
        isActiveCheckBox.setSelected(bool);
    }

    public boolean isNewCustomer() {
        return isNewCustomer;
    }


    public void setNewCustomer(boolean newCustomer) {
        isNewCustomer = newCustomer;
    }


    public void setTitleLabel(String label) {
        titleLabel.setText(label);
    }


    public Customer getSelectedCustomer() {
        return selectedCustomer;
    }

    public void setSelectedCustomer(Customer selectedCustomer) {
        this.selectedCustomer = selectedCustomer;
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

            // add new customer to database
            if (isNewCustomer) {

                String sql = "INSERT INTO customer (customerId, customerName, addressId, active, createDate, " +
                        "createdBy, lastUpdate, lastUpdateBy) VALUES (?,?,?,?,?,?,?,?)";

                PreparedStatement psmt = null;

                try {
                    psmt = dbConnect.prepareStatement(sql);
                    psmt.setInt(1, customerId);
                    psmt.setString(2, name);
                    psmt.setInt(3, addressId);
                    psmt.setInt(4, isActive);
                    psmt.setString(5, HomeScreenController.nowUtcAsString());
                    psmt.setString(6, currentUser);
                    psmt.setString(7, HomeScreenController.nowUtcAsString());
                    psmt.setString(8, currentUser);
                    psmt.executeUpdate();
                    modifyCustomerScreenStage.close();
                    customerTabController.addToCustomerList(customerId, name, addressId);

                } catch (SQLException e) {
                    e.printStackTrace();
                }

            // modifying existing customer, check current data against existing data
            } else {
                String sql = "UPDATE customer SET customerName = ?, addressId = ?, active = ?, lastUpdate = ?, " +
                        "lastUpdateBy = ? WHERE customerId = ?";

                PreparedStatement psmt = null;
                customerId = Integer.valueOf(customerIdField.getText());

                try {
                    psmt = dbConnect.prepareStatement(sql);
                    psmt.setString(1, name);
                    psmt.setInt(2, addressId);
                    psmt.setInt (3, isActive);
                    psmt.setString (4, HomeScreenController.nowUtcAsString());
                    psmt.setString (5, currentUser);
                    psmt.setInt (6, customerId);
                    psmt.executeUpdate();
                    modifyCustomerScreenStage.close();
                    // need to edit information from local observableList customerList in HomeScreenController
                    //HomeScreenController.getInstance().modifyCustomerInCustomerList(customerId, name, addressId);
                    customerTabController.updateTable(customerId, name, addressId);
                    // refresh show info


                } catch (SQLException e) {
                    e.printStackTrace();
                }

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

        String queryString = "SELECT " + columnName + " FROM " + tableName;

        try {
            Statement statement = dbConnect.prepareStatement(queryString);
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

        String columnName = tableName + "Id";
        String queryString = "SELECT " + columnName + " FROM " + tableName +";";
        int rtnInt = 1;
        ArrayList<Integer> arrayList = new ArrayList<>();

        try {
            Statement statement = dbConnect.prepareStatement(queryString);
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
        String queryString = "SELECT countryId FROM country where country = " + "'" + country + "';";

        int storedCountryId = 0;
        if (checkIfInTable(country, "country", "country")) {
            try {
                PreparedStatement psmt = dbConnect.prepareStatement(queryString);
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
            psmt = dbConnect.prepareStatement(sql);
            psmt.setInt(1, countryId);
            psmt.setString(2, countryToAdd);
            psmt.setString(3, HomeScreenController.nowUtcAsString());
            psmt.setString(4, currentUser);
            psmt.setString(5, HomeScreenController.nowUtcAsString());
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
        String queryString = "SELECT cityId FROM city where city = " + "'" + city + "';";

        int storedCityId = 0;
        if (checkIfInTable(city, "city", "city")) {
            try {
                PreparedStatement psmt = dbConnect.prepareStatement(queryString);
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
            psmt = dbConnect.prepareStatement(sql);
            psmt.setInt(1, cityId);
            psmt.setString(2, cityToAdd);
            psmt.setInt(3, countryId);
            psmt.setString(4, HomeScreenController.nowUtcAsString());
            psmt.setString(5, currentUser);
            psmt.setString(6, HomeScreenController.nowUtcAsString());
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
        String queryString = "SELECT addressId FROM address where address = " + "'" + address + "';";

        int storedAddressId = 0;
        if (checkIfInTable(address, "address", "address")) {
            try {
                PreparedStatement psmt = dbConnect.prepareStatement(queryString);
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
            psmt = dbConnect.prepareStatement(sql);
            psmt.setInt(1, addressId);
            psmt.setString(2, addressToAdd);
            psmt.setString(3, address2ToAdd);
            psmt.setInt(4, cityId);
            psmt.setString(5, postalCode);
            psmt.setString(6, phone);
            psmt.setString(7, HomeScreenController.nowUtcAsString());
            psmt.setString(8, currentUser);
            psmt.setString(9, HomeScreenController.nowUtcAsString());
            psmt.setString(10, currentUser);
            psmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return addressId;
    }

    private int customerIdToAddress (int customerId) {
        int addressId = -1;
        String sql = "SELECT FROM ";


        return addressId;
    }

/*

    //returns current date and time in UTC as a string.
    public String nowUtcAsString() {

        LocalDateTime currentDateTime = LocalDateTime.now(UTC);
        return currentDateTime.toString().replace("T", " ").substring(0,21);

    }
*/

    //returns current date and time in UTC as a string.
    public String nowLocalAsString () {

        LocalDateTime currentDateTime = LocalDateTime.now();
        return currentDateTime.toString().replace("T", " ").substring(0,21);

    }

    //setters and getter for TextField text
    public String getCustomerIdField() {
        return customerIdField.toString();
    }

    public void setCustomerIdField(String customerIdField) {
        this.customerIdField.setText(customerIdField);
    }

    public String getNameField() {
        return nameField.toString();
    }

    public void setNameField(String nameField) {
        this.nameField.setText(nameField);
    }


    public String getStreet1Field() {
        return street1Field.toString();
    }

    public void setStreet1Field(String street1Field) {
        this.street1Field.setText(street1Field);
    }

    public String getStreet2Field() {
        return street2Field.toString();
    }

    public void setStreet2Field(String street2Field) {
        this.street2Field.setText(street2Field);
    }

    public String getCityField() {
        return cityField.toString();
    }

    public void setCityField(String cityField) {
        this.cityField.setText(cityField);
    }

    public String getCountryField() {
        return countryField.toString();
    }

    public void setCountryField(String countryField) {
        this.countryField.setText(countryField);
    }

    public String getPostalCodeField() {
        return postalCodeField.toString();
    }

    public void setPostalCodeField(String postalCodeField) {
        this.postalCodeField.setText(postalCodeField);
    }

    public String getPhoneNumberField() {
        return phoneNumberField.toString();
    }

    public void setPhoneNumberField(String phoneNumberField) {
        this.phoneNumberField.setText(phoneNumberField);
    }
}


