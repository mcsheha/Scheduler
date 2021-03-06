package scheduler.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import scheduler.model.DbConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import static java.time.ZoneOffset.UTC;



public class HomeScreenController {

    private static HomeScreenController firstInstance = null;



    public void initialize(){
    }



    public static HomeScreenController getInstance() {
        if (firstInstance == null) {
            firstInstance = new HomeScreenController();
        }
        return firstInstance;
    }



    //returns current date and time in UTC as a string.
    public static String nowUtcAsString() {
        LocalDateTime currentDateTime = LocalDateTime.now(UTC);
        return currentDateTime.toString().replace("T", " ").substring(0,21);
    }



    public static String formatDateTimeString (String str) {
        return str.replace("T", " ").substring(0,16);
    }



    public static LocalDateTime convertLocaltoZulu (LocalDateTime local) {
        ZonedDateTime localZoned = local.atZone(ZoneId.systemDefault());
        ZonedDateTime utcZoned = localZoned.withZoneSameInstant(ZoneId.of("UTC"));
        return utcZoned.toLocalDateTime();
    }



    public static LocalDateTime convertZuluToLocal (LocalDateTime zulu) {
        ZonedDateTime utcZoned = zulu.atZone(ZoneId.of("UTC"));
        ZonedDateTime localZoned = utcZoned.withZoneSameInstant(ZoneId.systemDefault());
        return localZoned.toLocalDateTime();
    }



    // Takes customer Id and returns customer name
    public static String getCustomerNameFromId(int customerId) {
        Connection dbConnect =  DbConnection.getInstance().getConnection();

        String queryString = "SELECT customerName FROM customer WHERE customerId = " + customerId + ";";
        String customerName = "";

        try {
            Statement statement = dbConnect.prepareStatement(queryString);
            statement.executeQuery(queryString);
            ResultSet rs = statement.getResultSet();

            while(rs.next()) {
                customerName = rs.getString("customerName");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customerName;
    }



    // For 'closed' menu item clicked.
    @FXML
    private void closeClicked () {
        Platform.exit();
    }



    // For 'about' menu item clicked.
    @FXML
    public void showAbout () {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText(null);
        alert.setContentText("Scheduler Application version 1.0\n" +
                "C195 - Advanced Java Concepts\nBy: Michael Shehadeh");

        alert.showAndWait();
    }


}



