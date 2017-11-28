package scheduler.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import scheduler.MainApp;
import scheduler.model.Customer;

import java.io.IOException;
import java.sql.*;


public class HomeScreenController {

    @FXML
    private ChoiceBox<String> choiceBox;

    @FXML
    private Button addCustomer;

    @FXML
    private TableView<Customer> customerTable;

    @FXML
    private TableColumn<Customer, Integer> customerIdColumn;

    @FXML
    private TextField customerIdField;

    @FXML
    private TextArea customerInfoField;

    @FXML
    private TextField customerActiveField;

    @FXML
    private TableColumn<Customer, String> customerNameColumn;

    public static ObservableList<Customer> customerList;



    private static MainApp mainApp;
    private static String currentUserName;


    public void initialize(){
        choiceBox.getItems().addAll("Weekly", "Monthly");
        choiceBox.setValue("Weekly");
        populateCustomerTable();

        System.out.println("The current user when HomeScreenController is first initialized is: " + currentUserName);

        //ID column
        customerIdColumn.setCellValueFactory(cellData -> cellData.getValue().customerIdProperty().asObject());

        //Name column
        customerNameColumn.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());

        customerTable.setItems(customerList);

        customerTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showCustomerDetails(newValue));

    }


    public void setMainApp (MainApp mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    public void showAddPerson() throws IOException{
        mainApp.showAddCustomerScreen(currentUserName);

    }



    public void populateCustomerTable () {

        ObservableList<Customer> customers = FXCollections.observableArrayList();
        Connection dBase = mainApp.getDb().getConnection();
        String queryString = "SELECT customerId, customerName FROM customer;";

        try {

            PreparedStatement psmt = dBase.prepareStatement(queryString);
            psmt.executeQuery();
            ResultSet rs = psmt.getResultSet();

            while (rs.next()) {
                int customerID = rs.getInt ("customerId");
                String customerName = rs.getString("customerName");
                //System.out.println(customerID + "\t" + customerName);
                customers.add(new Customer(customerID, customerName));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        this.customerList = customers;
    }

    public static void addToCustomerList (int id, String name) {
        customerList.add(new Customer (id, name));
    }

    private String isCustomerActive (Customer customer) {
        int active = -1;
        Connection dBase = mainApp.getDb().getConnection();
        String queryString = "SELECT active FROM customer WHERE customerId = "
                + customer.getId() + ";";
        try {
            PreparedStatement psmt = dBase.prepareStatement(queryString);
            psmt.executeQuery();
            ResultSet rs = psmt.getResultSet();

            while (rs.next()) {
                active = rs.getInt ("active");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        String isActive = null;
        if (active == 1) {
            isActive = "Yes";
        }
        else {
            isActive = "No";
        }
        return isActive;
    }


    public String getCurrentUserName() {
        return currentUserName;
    }

    public void setCurrentUserName(String currentUserName) {
        this.currentUserName = currentUserName;
    }

    private void showCustomerDetails (Customer customer) {
        if (customer != null) {
                customerIdField.setText(customer.getId().toString());
                customerInfoField.setText(getCustomerInfo(customer));
                customerActiveField.setText(isCustomerActive(customer));
        }
        else {
            customerIdField.setText("");
            customerInfoField.setText("");
            customerActiveField.setText("");
        }
    }

    private String getCustomerInfo (Customer customer) {
        String customerInfo = null;

        String name = null;
        String address1 = null;
        String address2 = null;
        String city = null;
        String country = null;
        String postalCode = null;
        String phone = null;
        int addressId = -1;
        int cityId = -1;
        int countryId = -1;

        Connection dBase = mainApp.getDb().getConnection();
        String queryString = "SELECT * FROM customer WHERE customerId = "
                + customer.getId() + ";";

        try {
            PreparedStatement psmt = dBase.prepareStatement(queryString);
            psmt.executeQuery();
            ResultSet rs = psmt.getResultSet();

            while (rs.next()) {
                name = rs.getString("customerName");
                addressId = rs.getInt("addressId");

            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }

        queryString = "SELECT * FROM address WHERE addressId = " + addressId + ";";
        try {
            PreparedStatement psmt = dBase.prepareStatement(queryString);
            psmt.executeQuery();
            ResultSet rs = psmt.getResultSet();

            while (rs.next()) {
                address1 = rs.getString("address");
                address2 = rs.getString("address2");
                postalCode = rs.getString("postalCode");
                phone = rs.getString("phone");
                cityId = rs.getInt("cityId");
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }

        queryString = "SELECT city, countryId FROM city WHERE cityId = " + cityId + ";";
        try {
            PreparedStatement psmt = dBase.prepareStatement(queryString);
            psmt.executeQuery();
            ResultSet rs = psmt.getResultSet();

            while (rs.next()) {
                city = rs.getString("city");
                countryId = rs.getInt("countryId");

            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }

        queryString = "SELECT country FROM country WHERE countryId = " + countryId + ";";
        try {
            PreparedStatement psmt = dBase.prepareStatement(queryString);
            psmt.executeQuery();
            ResultSet rs = psmt.getResultSet();

            while (rs.next()) {
                country = rs.getString("country");
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }

        if (address2 != null && !address2.isEmpty()) {
            address1 = address1 + "\n" + address2;
        }

        customerInfo = name + "\n" +
                        address1 + "\n" +
                        city + "\n" +
                        country + "\n" +
                        postalCode + "\n" +
                        phone;


        //System.out.println("This is the customer Info: \n" + customerInfo);
        return customerInfo;
    }



}



