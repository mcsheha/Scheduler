package scheduler.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import scheduler.MainApp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;



public class Customer implements Comparable <Customer> {

    private int customerId;
    private String customerName;
    private int addressId;
    private int active;
    private LocalDateTime createDate;  //LocalDateTime is GMT, OffsetDateTime converts to timezone;
    private String createdBy;
    private LocalDateTime lastUpdate;
    private String lastUpdatedBy;


    private String street1;
    private String street2;
    private int cityId;
    private String city;
    private int countryId;
    private String country;
    private String postalCode;
    private String phone;


    public Customer (int customerId, String customerName, int addressId) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.addressId = addressId;
        setClassAddressInfo(addressId);

    }

    private void setClassAddressInfo (int addressId) {
        //get db connection and query for details of address id to set properties in this class
        Connection dBase = MainApp.getDb().getConnection();
        String queryString = "SELECT * FROM address WHERE addressId = " + addressId + ";";
        int cityIdInt = -1;
        int countryIdInt = -1;


        try {
            PreparedStatement psmt = dBase.prepareStatement(queryString);
            psmt.executeQuery();
            ResultSet rs = psmt.getResultSet();

            while (rs.next()) {
                this.street1 = rs.getString ("address");
                this.street2 = rs.getString("address2");
                cityIdInt = rs.getInt("cityId");
                this.cityId = cityIdInt;
                this.postalCode = rs.getString("postalCode");
                this.phone = rs.getString ("phone");

            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        queryString = "SELECT * FROM city WHERE cityId = " + cityIdInt + ";";

        try {
            PreparedStatement psmt = dBase.prepareStatement(queryString);
            psmt.executeQuery();
            ResultSet rs = psmt.getResultSet();

            while (rs.next()) {
                this.city = rs.getString ("city");
                countryIdInt = rs.getInt("countryId");
                this.countryId = countryIdInt;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        queryString = "SELECT * FROM country WHERE countryId = " + countryIdInt + ";";

        try {
            PreparedStatement psmt = dBase.prepareStatement(queryString);
            psmt.executeQuery();
            ResultSet rs = psmt.getResultSet();

            while (rs.next()) {
                this.country = rs.getString ("country");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

    }



    public StringProperty customerNameProperty () {
       return new SimpleStringProperty(customerName);
    }

    public IntegerProperty customerIdProperty () {
        return new SimpleIntegerProperty(customerId);
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }



    public boolean isCustomerActive () {
        Connection dBase = DbConnection.getInstance().getConnection();
        int activeInt = -1;
        boolean bool = false;

        String queryString = "SELECT active FROM customer WHERE customerId = " + customerId + ";";

        try {
            PreparedStatement psmt = dBase.prepareStatement(queryString);
            psmt.executeQuery();
            ResultSet rs = psmt.getResultSet();

            while (rs.next()) {
                activeInt = rs.getInt ("active");
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        if (activeInt == 1) {
            bool = true;
        }
            return bool;

    }



    public String getStreet1() {
        return street1;
    }

    public String getStreet2() {
        return street2;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getPhone() {
        return phone;
    }




    @Override
    public int compareTo(Customer o) {
        int compareIds = ((Customer) o).getCustomerId();

        return this.customerId - compareIds;
    }
}
