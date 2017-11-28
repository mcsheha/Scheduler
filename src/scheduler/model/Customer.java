package scheduler.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.*;



public class Customer {

    private int customerId;
    private String customerName;
    private int addressId;
    private int active;
    private LocalDateTime createDate;  //LocalDateTime is GMT, OffsetDateTime converts to timezone;
    private String createdBy;
    private LocalDateTime lastUpdate;
    private String lastUpdatedBy;
    private IntegerProperty id;
    private StringProperty name;

    public Customer (int customerId, String customerName) {
        //this.customerId = customerId;
        //this.customerName = customerName;
        this.id = new SimpleIntegerProperty(customerId);
        this.name = new SimpleStringProperty(customerName);
    }


    public StringProperty customerNameProperty () {
       return name;
    }

    public IntegerProperty customerIdProperty () {
        return id;
    }

    public Integer getId() {
        return id.get();
    }

    public String getName () {
        return name.get();
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public LocalDateTime getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }


}
