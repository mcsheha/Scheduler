package scheduler.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import scheduler.controller.AppointmentTabController;
import scheduler.controller.CustomerTabController;
import scheduler.controller.HomeScreenController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Appointment {

    // Displayed
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

    // Displayed in Monthly and Weekly Views
    private String timeColumnString = "";
    private String customerNameColumnString = "";
    private String appointmentTypeString = "";

    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;

    private boolean isDummyAppointment = false;

    // not currently used
    public Appointment (int appointmentId, int customerId, String title, String description, String location, String contact,
                        String url, String start, String end) {

        this.appointmentId = appointmentId;
        this.customerId = customerId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contact = contact;
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

    public Appointment (String timeColumnString, String customerNameColumnString, String appointmentTypeString) {
        this.timeColumnString = timeColumnString;
        this.customerNameColumnString = customerNameColumnString;
        this.appointmentTypeString = appointmentTypeString;
    }

    public Appointment (String timeColumnString, boolean isDummyAppointment) {
        this.timeColumnString = timeColumnString;
        this.isDummyAppointment = true;
    }

    // used by AppointmentList DB query results
    public Appointment(int appointmentId, int customerId, String title, String description, String location, String contact,
                String url, String start, String end, String createDate, String createdBy,
                String lastUpdate, String lastUpdateBy) {
            this.appointmentId = appointmentId;
            this.customerId = customerId;
            this.title = title;
            this.description = description;
            this.location = location;
            this.contact = contact;
            this.url = url;
            this.start = start;
            this.end = end;
            String startSub = start.substring(11,16);
            //System.out.println("The startSub format is: " + startSub);

            this.timeColumnString = startSub;
            this.customerNameColumnString = getCustomerNameFromDb(customerId);
            this.appointmentTypeString = title;
            setStartAndEndDateTime(start, end);
    }



    public String getCustomerNameFromDb (int customerId) {
        String customerName = null;
        String sql = "SELECT customerName FROM customer WHERE customerId = " + customerId + ";";
        Connection dbConnect = DbConnection.getInstance().getConnection();

        try {
            PreparedStatement psmt = dbConnect.prepareStatement(sql);
            psmt.executeQuery();
            ResultSet rs = psmt.getResultSet();

            while(rs.next()) {
                customerName = rs.getString("customerName");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customerName;
    }


    public void setStartAndEndDateTime (String start, String end) {
        start = start.substring(0,19);
        end = end.substring(0,19);
        System.out.println("Start is: " + start);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.startDateTime = LocalDateTime.parse(start, formatter);
        this.endDateTime = LocalDateTime.parse(end,formatter);
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

    public String getTimeColumnString() {
        return timeColumnString;
    }

    public void setTimeColumnString(String timeColumnString) {
        this.timeColumnString = timeColumnString;
    }

    public String getCustomerNameColumnString() {
        return customerNameColumnString;
    }

    public void setCustomerNameColumnString(String customerNameColumnString) {
        this.customerNameColumnString = customerNameColumnString;
    }

    public String getAppointmentTypeString() {
        return appointmentTypeString;
    }

    public void setAppointmentTypeString(String appointmentTypeString) {
        this.appointmentTypeString = appointmentTypeString;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }
}
