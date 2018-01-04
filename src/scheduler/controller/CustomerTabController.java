package scheduler.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import scheduler.MainApp;
import scheduler.model.Customer;
import scheduler.model.DbConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;

public class CustomerTabController {

    @FXML
    private AnchorPane customerTab;
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
    private static CustomerTabController firstInstance = null;
    private static ObservableList<Customer> customerList;
    private static Connection dbConnect;
    private static MainApp mainApp;
    private static String currentUserName;


    public void initialize(){

        dbConnect = DbConnection.getInstance().getConnection();
        this.mainApp = MainApp.getInstance();
        this.currentUserName = mainApp.getCurrentUserName();
        setTheTable();

    }


    // Populates the list of customers
    public void setTheTable () {
        populateCustomerList();

        //ID column
        customerIdColumn.setCellValueFactory(cellData -> cellData.getValue().customerIdProperty().asObject());

        //Name column
        customerNameColumn.setCellValueFactory(cellData -> cellData.getValue().customerNameProperty());

        customerTable.setItems(customerList);
        Collections.sort(customerList);

        customerTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showCustomerDetails(newValue));

    }



    public static CustomerTabController getInstance() {
        if (firstInstance == null) {
            firstInstance = new CustomerTabController();
        }
        return firstInstance;
    }



    public void setMainApp () {
        this.mainApp = MainApp.getInstance();
    }


    @FXML
    public void showAddPerson() throws IOException {
        mainApp.showAddCustomerScreen(currentUserName);

    }


    // Queries customers from the DB and creates a local list of customers
    public void populateCustomerList() {

        ObservableList<Customer> customers = FXCollections.observableArrayList();
        String queryString = "SELECT customerId, customerName, addressId FROM customer;";

        try {

            PreparedStatement psmt = dbConnect.prepareStatement(queryString);
            psmt.executeQuery();
            ResultSet rs = psmt.getResultSet();

            while (rs.next()) {
                int customerID = rs.getInt ("customerId");
                String customerName = rs.getString("customerName");
                int addressId = rs.getInt("addressId");
                //System.out.println(customerID + "\t" + customerName);
                customers.add(new Customer(customerID, customerName, addressId));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        this.customerList = customers;
    }



    public void addToCustomerList (int id, String name, int addressId) {
        customerList.add(new Customer (id, name, addressId));
        Collections.sort(customerList);

    }



    private String isCustomerActive (Customer customer) {
        int active = -1;
        String queryString = "SELECT active FROM customer WHERE customerId = "
                + customer.getCustomerId() + ";";
        try {
            PreparedStatement psmt = dbConnect.prepareStatement(queryString);
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



    // Populates or clears the customer details pane depending on which customer is selected in left pane (customer list)
    private void showCustomerDetails (Customer customer) {
        if (customer != null) {
            customerIdField.setText(String.valueOf(customer.getCustomerId()));
            customerInfoField.setText(getCustomerInfo(customer));
            customerActiveField.setText(isCustomerActive(customer));
        }
        else {
            customerIdField.setText("");
            customerInfoField.setText("");
            customerActiveField.setText("");
        }
    }


    // Queries DB to get customer Info
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

        String queryString = "SELECT * FROM customer WHERE customerId = "
                + customer.getCustomerId() + ";";

        try {
            PreparedStatement psmt = dbConnect.prepareStatement(queryString);
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
            PreparedStatement psmt = dbConnect.prepareStatement(queryString);
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
            PreparedStatement psmt = dbConnect.prepareStatement(queryString);
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
            PreparedStatement psmt = dbConnect.prepareStatement(queryString);
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

        return customerInfo;
    }



    @FXML
    public void handleDeleteCustomer () {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Are You Sure?");
        alert.setHeaderText("It is recommended to set a customer to inactive instead of deleting.");
        alert.setContentText("Deleting a customer can not be undone.");

        ButtonType buttonMakeInactive = new ButtonType("Set Inactive");
        ButtonType buttonConfirmDelete = new ButtonType("Delete");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonMakeInactive, buttonConfirmDelete, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonMakeInactive){
            makeCustomerInactive();
        } else if (result.get() == buttonConfirmDelete) {
            deleteCustomer();
        } else {
            // ... user chose CANCEL or closed the dialog
        }
    }



    private void makeCustomerInactive() {
        int customerId = -1;
        Customer customer = customerTable.getSelectionModel().getSelectedItem();
        customerId = customer.getCustomerId();

        String sql = "UPDATE customer SET active = 0 WHERE customerId = " + customerId + ";";

        try {
            PreparedStatement psmt = dbConnect.prepareStatement(sql);
            psmt.execute();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public void deleteCustomer () {

        int customerId = -1;
        Customer customer = customerTable.getSelectionModel().getSelectedItem();
        customerId = customer.getCustomerId();

        String sql = "DELETE FROM customer WHERE customerId = " + customerId + ";";

        try {
            PreparedStatement psmt = dbConnect.prepareStatement(sql);
            psmt.execute();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        // also delete from local customer list
        customerList.remove(customer);
    }

    @FXML
    public void handleModifyCustomer() throws IOException  {
        Customer customer = customerTable.getSelectionModel().getSelectedItem();
        if (customer == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Customer Selected");
            alert.setHeaderText("No Customer Selected");
            alert.setContentText("You must select a customer from the table.");

            alert.showAndWait();
        }
        else {
            ModifyCustomerController controller = mainApp.showModifyCustomerScreen(currentUserName, customer);

        }

    }


    public void updateTable (int customerId, String customerName, int addressId) {
        //customerTable.setItems(customerList);
        //customerList.remove(1,3); <- this worked
        System.out.println("The customer list contains " + customerList.size() + " customers.");


        for (Customer c : customerList) {
            if (c.getCustomerId() == customerId) {
                customerList.remove(c);
                customerList.add(new Customer(customerId, customerName, addressId));
                // now sort the table on customerId.
                Collections.sort(customerList);
                return;
            }

        }



    }
/*

    //returns current date and time in UTC as a string.
    public String nowUtcAsString() {

        LocalDateTime currentDateTime = LocalDateTime.now(UTC);
        return currentDateTime.toString().replace("T", " ").substring(0,21);

    }
*/


    public String getCustomerName(int customerId) {
        String customerName = null;
        String queryString = "SELECT customerName FROM customer WHERE customerId = " + customerId + ";";

        try {
            PreparedStatement psmt = dbConnect.prepareStatement(queryString);
            psmt.executeQuery();
            ResultSet rs = psmt.getResultSet();

            while (rs.next()) {
                customerName = rs.getString("customerId");
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return customerName;
    }

}
