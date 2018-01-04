package scheduler.model;


import scheduler.controller.HomeScreenController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Appointment {

    private boolean isDummyAppointment = false;

    // Displayed in appointment details pane
    private int appointmentId;
    private String title;
    private String description;
    private String location;
    private String contact;
    private String url;
    private LocalDateTime startLDT;
    private LocalDateTime endLDT;
    private LocalDateTime lastUpdateLDT;
    private LocalDateTime createdDateLDT;
    private String createdBy;
    private String lastUpdateBy;



    // Displayed in Monthly and Weekly views pane
    private String timeColumnString;
    private String customerNameColumnString = "";



    // Constructor for dummy appointments used as treeTableView headers... Monday, Tuesday, 1, 2, 3, etc.
    public Appointment (String timeColumnString, boolean isDummyAppointment) {
        this.timeColumnString = timeColumnString;
        this.isDummyAppointment = true;
    }



    // Constructor used when pulling appointments from DB and also when completing add/modify form
    public Appointment (int appointmentId, int customerId, String title, String description, String location, String contact,
                        String url, LocalDateTime startLDT, LocalDateTime endLDT, LocalDateTime createDateLDT, String createdBy,
                        LocalDateTime lastUpdateLDT, String lastUpdateBy) {
        this.appointmentId = appointmentId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contact = contact;
        this.url = url;
        this.startLDT = startLDT;
        this.endLDT = endLDT;
        this.createdBy = createdBy;
        this.lastUpdateBy = lastUpdateBy;
        this.createdDateLDT = createDateLDT;
        this.lastUpdateLDT = lastUpdateLDT;
        this.customerNameColumnString = getCustomerNameFromDb(customerId);
        this.timeColumnString = startLDT.toString().substring(11,16) + " - " + endLDT.toString().substring(11, 16);

    }



    public String getCreatedDateAsLocalString() {
        String str = createdDateLDT.toString();
        return HomeScreenController.formatDateTimeString(str);

    }



    public String getLastUpdateDateAsLocalString() {
        String str = lastUpdateLDT.toString();
        return HomeScreenController.formatDateTimeString(str);

    }



    private String getCustomerNameFromDb (int customerId) {
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



    public int getAppointmentId() {
        return appointmentId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public String getContact() {
        return contact;
    }

    public String getUrl() {
        return url;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public String getTimeColumnString() {
        return timeColumnString;
    }

    public String getCustomerNameColumnString() {
        return customerNameColumnString;
    }

    public boolean isDummyAppointment() {
        return isDummyAppointment;
    }

    public String getStartTimeAsString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return startLDT.format(formatter);
    }

    public String getEndTimeAsString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return endLDT.format(formatter);
    }

    public LocalDateTime getStartLDT() {
        return startLDT;
    }

    public LocalDateTime getEndLDT() {
        return endLDT;
    }


}
