package scheduler.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import scheduler.controller.AppointmentTabController;
import scheduler.controller.CustomerTabController;
import scheduler.controller.HomeScreenController;


public class Appointment {

    private int appointmentId;
    private int customerId;
    private String title;
    private String description;
    private String location;
    private String contact;
    private String url;
    private String start;
    private String end;
    private String createDate;
    private String createdBy;
    private String lastUpdate;
    private String lastUpdateBy;
    private HomeScreenController homeScreenController;
    private AppointmentTabController appointmentTabController;
    private CustomerTabController customerTabController;

    private SimpleIntegerProperty startProperty;
    private SimpleStringProperty customerNameProperty;
    private SimpleStringProperty titleProperty;


    public Appointment (int appointmentId, int customerId, String title, String description, String location,
                        String url, String start, String end) {

        this.appointmentId = appointmentId;
        this.customerId = customerId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.url = url;
        this.start = start;
        this.end = end;
        this.customerTabController = CustomerTabController.getInstance();
        this.appointmentTabController = AppointmentTabController.getInstance();
        this.homeScreenController = HomeScreenController.getInstance();
        this.createdBy = customerTabController.getCurrentUserName();
        this.lastUpdateBy = customerTabController.getCurrentUserName();
        this.createDate = customerTabController.nowUtcAsString();
        this.lastUpdate = customerTabController.nowUtcAsString();

        this.startProperty = new SimpleIntegerProperty(Integer.valueOf(start));
        this.customerNameProperty = new SimpleStringProperty(customerTabController.getCustomerName(customerId));
        this.titleProperty = new SimpleStringProperty(title);

    }


    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
        this.customerNameProperty.setValue(customerTabController.getCustomerName(customerId));
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.titleProperty.setValue(title);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
        this.startProperty.setValue(Integer.valueOf(start));
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public int getStartProperty() {
        return startProperty.get();
    }

    public SimpleIntegerProperty startPropertyProperty() {
        return startProperty;
    }

    public void setStartProperty(int startProperty) {
        this.startProperty.set(startProperty);
    }

    public String getCustomerNameProperty() {
        return customerNameProperty.get();
    }

    public SimpleStringProperty customerNamePropertyProperty() {
        return customerNameProperty;
    }

    public void setCustomerNameProperty(String customerNameProperty) {
        this.customerNameProperty.set(customerNameProperty);
    }

    public String getTitleProperty() {
        return titleProperty.get();
    }

    public SimpleStringProperty titlePropertyProperty() {
        return titleProperty;
    }

    public void setTitleProperty(String titleProperty) {
        this.titleProperty.set(titleProperty);
    }
}
