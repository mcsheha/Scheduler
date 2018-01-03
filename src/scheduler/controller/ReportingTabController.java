package scheduler.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import scheduler.MainApp;
import scheduler.model.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ReportingTabController {

    private static Connection dbConnect = DbConnection.getInstance().getConnection();

    private ToggleGroup reportToggleGroup = new ToggleGroup();
    @FXML
    private RadioButton report1Button = new RadioButton();
    @FXML
    private RadioButton report3Button = new RadioButton();
    @FXML
    private RadioButton report2Button = new RadioButton();
    @FXML
    private Button generateButton;
    @FXML
    private Button printButton;
    @FXML
    private TextArea reportResults;
    @FXML
    private DatePicker report1DatePicker = new DatePicker();
    @FXML
    private ChoiceBox<String> consultantsChoiceBox = new ChoiceBox<>();


    public void initialize() {
        report1DatePicker.setValue(LocalDate.now());
        populateConsultantChoiceBox();
        reportResults.setStyle("-fx-font-family: monospace");

    }


    @FXML
    private void handleGenerateButtonClicked () {

        // Number of appointment types by month
        if (report1Button.isSelected()) {
            reportAppointmentTypesByMonth();

        // Schedule for consultant
        } else if (report2Button.isSelected()) {
            reportConsultantSchedule();
        } else {
            reportPhoneListing();
        }

    }



    private void handlePrintButtonClicked () {

    }


    private void populateConsultantChoiceBox () {
        String sql = "SELECT userName FROM user;";
        PreparedStatement psmt = null;

        try {
            psmt = dbConnect.prepareStatement(sql);
            psmt.executeQuery();
            ResultSet rs = psmt.getResultSet();

            while (rs.next()) {
                consultantsChoiceBox.getItems().add(rs.getString("userName"));
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        consultantsChoiceBox.setValue(MainApp.getCurrentUserName());
    }


    // Number of appointment types by month
    private void reportAppointmentTypesByMonth() {
        LocalDate datePicked = report1DatePicker.getValue();
        LocalDate firstOfMonth = datePicked.withDayOfMonth(1);
        LocalDate lastOfMonth = datePicked.withDayOfMonth(datePicked.lengthOfMonth());

        String displayString = "Qty:      Type:\n----------------------\n";
        String sql = "SELECT description, COUNT(appointmentId)\n" +
                "FROM appointment\n" +
                "WHERE start BETWEEN '" + firstOfMonth + "' AND '" + lastOfMonth + "'\n" +
                "GROUP BY description\n" +
                "ORDER BY COUNT(appointmentId) DESC;";

        PreparedStatement psmt = null;

        try {
            psmt = dbConnect.prepareStatement(sql);
            psmt.executeQuery();
            ResultSet rs = psmt.getResultSet();

            while (rs.next()) {
                String tempString = String.format("%-10d%-30s",
                        rs.getInt("COUNT(AppointmentID)"), rs.getString("description"));
                displayString += tempString + "\n";
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        reportResults.setText(displayString);
    }


    private void reportConsultantSchedule() {
        String displayString = String.format("%-35s%-25s%-35s%-30s%n","Date/Time:", "Customer:", "Title:", "Description:") +
                "-------------------------------------------------------------------------------------------------------------\n";

        String sql = "SELECT start, customerId, title, description FROM appointment WHERE createdBy = '" +
                consultantsChoiceBox.getSelectionModel().getSelectedItem() + "' ORDER BY start;";

        PreparedStatement psmt = null;

        try {
            psmt = dbConnect.prepareStatement(sql);
            psmt.executeQuery();
            ResultSet rs = psmt.getResultSet();

            while (rs.next()) {
                displayString += String.format("%-35tc%-25.25s%-35.35s%-30.30s%n",
                        rs.getTimestamp("start"),
                        HomeScreenController.getCustomerNameFromId(rs.getInt("customerId")),
                        rs.getString("title"),
                        rs.getString("description"));
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        reportResults.setText(displayString);
    }


    private void reportPhoneListing() {
        String displayString = String.format("%-25s%-25s%n","Name:","Phone Number:") + "----------------------------------------\n";
        String sql = "SELECT customer.customerName, address.phone\n" +
                "FROM address\n" +
                "INNER JOIN customer ON address.addressId=customer.addressId\n" +
                "ORDER BY customerName;";

        PreparedStatement psmt = null;

        try {
            psmt = dbConnect.prepareStatement(sql);
            psmt.executeQuery();
            ResultSet rs = psmt.getResultSet();

            while (rs.next()) {
                displayString += String.format("%-25.20s%-25.20s%n", rs.getString("customerName"), rs.getString("phone"));
            }
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        reportResults.setText(displayString);

    }


}
