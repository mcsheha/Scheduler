package scheduler.controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import scheduler.model.Appointment;
import scheduler.model.DbConnection;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;


public class ModifyAppointmentController {

    private boolean isNewAppointment = true;
    private static Connection dbConnect;
    private Stage modifyAppointmentScreenStage;
    private static String currentUserName;
    private static AppointmentTabController appointmentTabController;
    private Appointment selectedAppointment;


    @FXML
    private Label titleLabel = new Label("Modify Appointment");
    @FXML
    private TextField appointmentIdTextField;
    @FXML
    private ChoiceBox<String> customerChoiceBox;
    @FXML
    private TextField titleTextField;
    @FXML
    private TextField descriptionTextField;
    @FXML
    private TextField locationTextField;
    @FXML
    private TextField contactTextField;
    @FXML
    private TextField urlTextField;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private ChoiceBox<String> startTimeChoiceBox;
    @FXML
    private ChoiceBox<String> endTimeChoiceBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button deleteButton;





    public void initialize(){
        dbConnect = DbConnection.getInstance().getConnection();
        appointmentIdTextField.setText(Integer.toString(getLowestAppointmentId()));
        populateCustomerChoiceBox();
        populateTimeChoiceBoxes();
        appointmentTabController = AppointmentTabController.getInstance();
    }



    @FXML
    public void handleCancel () {
        modifyAppointmentScreenStage.close();
    }



    public void setModifyAppointmentScreenStage(Stage modifyAppointmentScreenStage) {
        this.modifyAppointmentScreenStage = modifyAppointmentScreenStage;
    }



    public void setTitleLabel (String titleLabelString) {
        this.titleLabel.setText(titleLabelString);
    }



    public int getLowestAppointmentId () {
        String queryString = "SELECT appointmentId FROM appointment;";
        int rtnInt = 1;
        ArrayList<Integer> arrayList = new ArrayList<>();

        try {
            Statement statement = dbConnect.prepareStatement(queryString);
            statement.executeQuery(queryString);
            ResultSet rs = statement.getResultSet();

            while(rs.next()){
                arrayList.add(rs.getInt("appointmentId"));
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


    public void populateCustomerChoiceBox () {
        ObservableList<String> list = FXCollections.observableArrayList();
        String queryString = "SELECT customerName FROM customer;";
        try{
            Statement statement = dbConnect.prepareStatement(queryString);
            statement.executeQuery(queryString);
            ResultSet rs = statement.getResultSet();

            while(rs.next()) {
                list.add(rs.getString("customerName"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        customerChoiceBox.setItems(list);

    }



    public void populateTimeChoiceBoxes() {
        ObservableList<String> list = FXCollections.observableArrayList();
        list.addAll("00:00", "00:30", "01:00", "01:30", "02:00", "02:30", "03:00", "03:30", "04:00", "04:30",
                "05:00", "05:30", "06:00", "06:30", "07:00", "07:30", "08:00", "08:30", "09:00", "09:30", "10:00",
                "10:30", "11:00", "11:30", "12:00", "12:30", "13:00", "13:30", "14:00", "14:30", "15:00", "15:30",
                "16:00", "16:30", "17:00", "17:30", "18:00", "18:30", "19:00", "19:30", "20:00", "20:30", "21:00",
                "21:30", "22:00", "22:30", "23:00", "23:30");
        startTimeChoiceBox.setItems(list);
        endTimeChoiceBox.setItems(list);

    }

    public void handleSaveButtonClicked () {
        if(isInputValid() && duringWorkingHours() && notConflicting()) {
            int appointmentId = Integer.valueOf(appointmentIdTextField.getText());
            String customerName = customerChoiceBox.getSelectionModel().getSelectedItem();
            String title = titleTextField.getText();
            String description = descriptionTextField.getText();
            String location = locationTextField.getText();
            String contact = contactTextField.getText();
            String url = urlTextField.getText();
            String startDateString = startDatePicker.getValue().toString();
            String startTimeString = startTimeChoiceBox.getSelectionModel().getSelectedItem();
            LocalDateTime startDateTime = localDateTimeStringToObject(startDateString, startTimeString);
            String endDateString = endDatePicker.getValue().toString();
            String endTimeString = endTimeChoiceBox.getSelectionModel().getSelectedItem();
            LocalDateTime endDateTime = localDateTimeStringToObject(endDateString, endTimeString);
            int customerId = getCustomerIdFromName(customerName);

            // adding new appointment
            if (isNewAppointment()) {
                String sql = "INSERT INTO appointment (appointmentId, customerId, title, description, location, contact, " +
                        "url, start, end, createDate, createdBy, lastUpdate, lastUpdateBy) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
                PreparedStatement psmt = null;

                try {
                    psmt = dbConnect.prepareStatement(sql);
                    psmt.setInt(1, appointmentId);
                    psmt.setInt(2, customerId);
                    psmt.setString(3, title);
                    psmt.setString(4, description);
                    psmt.setString(5, location);
                    psmt.setString(6, contact);
                    psmt.setString(7, url);
                    psmt.setTimestamp(8, Timestamp.valueOf(HomeScreenController.convertLocaltoZulu(startDateTime)));
                    psmt.setTimestamp(9, Timestamp.valueOf(HomeScreenController.convertLocaltoZulu(endDateTime)));
                    psmt.setTimestamp(10, Timestamp.valueOf(HomeScreenController.convertLocaltoZulu(LocalDateTime.now())));
                    psmt.setString(11, getCurrentUserName());
                    psmt.setTimestamp(12, Timestamp.valueOf(HomeScreenController.convertLocaltoZulu(LocalDateTime.now())));
                    psmt.setString(13, getCurrentUserName());
                    psmt.executeUpdate();
                    modifyAppointmentScreenStage.close();
                    // add to local list
                    appointmentTabController.addToAppointmentList(new Appointment(appointmentId, customerId, title, description,
                            location, contact, url, startDateTime, endDateTime, LocalDateTime.now(),
                            getCurrentUserName(), LocalDateTime.now(), getCurrentUserName()));
                    //appointmentTabController.showWeeklyView();


                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            // modifying existing appointment
            else {
                String sql = "UPDATE appointment SET customerId = ?, title = ?, description = ?, location = ?, contact = ?," +
                        "url = ?, start = ?, end = ?, lastUpdate = ?, lastUpdateBy = ? WHERE appointmentId = ?";

                PreparedStatement psmt = null;

                try {
                    psmt = dbConnect.prepareStatement(sql);
                    psmt.setInt(1, customerId);
                    psmt.setString(2, title);
                    psmt.setString(3, description);
                    psmt.setString(4, location);
                    psmt.setString(5, contact);
                    psmt.setString(6, url);
                    psmt.setTimestamp(7, Timestamp.valueOf(HomeScreenController.convertLocaltoZulu(startDateTime)));
                    psmt.setTimestamp (8, Timestamp.valueOf(HomeScreenController.convertLocaltoZulu(endDateTime)));

                    psmt.setTimestamp(9, Timestamp.valueOf(HomeScreenController.convertLocaltoZulu(LocalDateTime.now())));
                    psmt.setString(10, getCurrentUserName());
                    psmt.setInt(11, appointmentId);
                    psmt.executeUpdate();
                    modifyAppointmentScreenStage.close();
                    appointmentTabController.removeFromAppointmentList(appointmentId);

                    appointmentTabController.addToAppointmentList(new Appointment(appointmentId, customerId, title, description,
                        location, contact, url, startDateTime, endDateTime,
                            appointmentTabController.getCreateDateLDT(appointmentId),
                        appointmentTabController.getCreatedBy(appointmentId),
                            LocalDateTime.now(), getCurrentUserName()));


                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }

        }
    }


    // Checks for scheduling errors before saving appointment
    //      Queries DB for all appointments the day before and day after and compares start/end dates to date/times on form
    private boolean notConflicting() {
        boolean rtnBool = true;

        String newAptStartTimeLocalString = startTimeChoiceBox.getSelectionModel().getSelectedItem();
        String newAptEndTimeLocalString = endTimeChoiceBox.getSelectionModel().getSelectedItem();
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalDateTime newAptStartDateTimeZulu = HomeScreenController.
                convertLocaltoZulu(startDate.atTime(LocalTime.parse(newAptStartTimeLocalString,formatter)));
        LocalDateTime newAptEndDateTimeZulu = HomeScreenController.
                convertLocaltoZulu(endDate.atTime(LocalTime.parse(newAptEndTimeLocalString,formatter)));

        LocalDate dayBefore = newAptStartDateTimeZulu.toLocalDate().minusDays(1);
        LocalDate dayAfter = newAptEndDateTimeZulu.toLocalDate().plusDays(1);

        String queryString = "SELECT * FROM appointment WHERE start >= '" + dayBefore +"' AND start <='" + dayAfter + "';";

        try {
            Statement statement = dbConnect.prepareStatement(queryString);
            statement.executeQuery(queryString);
            ResultSet rs = statement.getResultSet();

            while(rs.next()) {
                LocalDateTime oldAptStartDateTimeZulu = rs.getTimestamp("start").toLocalDateTime();
                LocalDateTime oldAptEndDateTimeZulu = rs.getTimestamp("end").toLocalDateTime();
                int oldAptId = rs.getInt("appointmentId");

                if (!(Integer.valueOf(appointmentIdTextField.getText()) == oldAptId) && newAptStartDateTimeZulu.isEqual(oldAptStartDateTimeZulu) || newAptEndDateTimeZulu.isEqual(oldAptEndDateTimeZulu) ||
                        (newAptStartDateTimeZulu.isBefore(oldAptStartDateTimeZulu) && newAptEndDateTimeZulu.isAfter(oldAptStartDateTimeZulu)) ||
                        (newAptStartDateTimeZulu.isAfter(oldAptStartDateTimeZulu) &&  newAptEndDateTimeZulu.isBefore(oldAptEndDateTimeZulu))) {

                    String oldTitle = rs.getString("title");
                    String oldCustomer = HomeScreenController.getCustomerNameFromId(rs.getInt("customerId"));
                    LocalTime oldStartTimeLocal = HomeScreenController.convertZuluToLocal(oldAptStartDateTimeZulu).toLocalTime();
                    LocalTime oldEndTimeLocal = HomeScreenController.convertZuluToLocal(oldAptEndDateTimeZulu).toLocalTime();

                    String schedulingConflictError = oldStartTimeLocal + " - " + oldEndTimeLocal + "     " + oldCustomer + "     " + oldTitle;

                    //Alert
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Scheduling Conflict");
                    alert.setHeaderText("There is a scheduling conflict with: ");
                    alert.setContentText(schedulingConflictError);
                    alert.showAndWait();

                    return false;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rtnBool;
    }



    private boolean duringWorkingHours() {
        LocalTime workStartTime = LocalTime.of(9,00);
        LocalTime workEndTime = LocalTime.of(17,00);
        String startTimeString = startTimeChoiceBox.getSelectionModel().getSelectedItem();
        String endTimeString = endTimeChoiceBox.getSelectionModel().getSelectedItem();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalTime startTime = LocalTime.parse(startTimeString, formatter);
        LocalTime endTime = LocalTime.parse(endTimeString, formatter);
        boolean rtnBool = false;

        if(!startTime.isBefore(workStartTime) && !endTime.isAfter(workEndTime)) {
            rtnBool = true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText("Appointment times outside of working hours!");
            alert.setContentText("Appointment must be between 9am and 5pm.");
            alert.showAndWait();
        }
        return rtnBool;
    }


    public LocalDateTime localDateTimeStringToObject (String dateString, String timeString) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String concatString = dateString + " " + timeString;
        return LocalDateTime.parse(concatString, dateTimeFormatter);
    }



    public int getCustomerIdFromName (String customerName) {
        String queryString = "SELECT customerId FROM customer WHERE customerName = '" + customerName + "';";
        int returnInt = -1;

        try {
            Statement statement = dbConnect.prepareStatement(queryString);
            statement.executeQuery(queryString);
            ResultSet rs = statement.getResultSet();
            while(rs.next()){
                returnInt = rs.getInt("customerId");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return returnInt;
    }





    public boolean isInputValid() {
        String errorMessage = "";
        boolean dataTypesCorrect = true;

        //Ensure no blank fields: ID, Name, Street address 1, City, Country, Postal Code, Phone)
        if (appointmentIdTextField.getText() == null || appointmentIdTextField.getText().length() == 0) {
            errorMessage += "No valid appointment ID!\n";
        }
        if (customerChoiceBox.getSelectionModel().getSelectedItem() == null) {
            errorMessage += "No customer selected!\n";
        }
        if (titleTextField.getText() == null || titleTextField.getText().length() == 0) {
            errorMessage += "No valid title!\n";
        }
        if (locationTextField.getText() == null || locationTextField.getText().length() == 0) {
            errorMessage += "No valid location!\n";
        }
        if (startDatePicker.getValue() == null ) {
            errorMessage += "No valid start date!\n";
        }
        if (startTimeChoiceBox.getSelectionModel().getSelectedItem() == null ) {
            errorMessage += "No valid start time!\n";
        }
        if (endDatePicker.getValue() == null ) {
            errorMessage += "No valid end date!\n";
        }
        if (endTimeChoiceBox.getSelectionModel().getSelectedItem() == null ) {
            errorMessage += "No valid end time!\n";
        }

        // Ensure start date/time is not after end date/time
        //DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        //DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


        System.out.println("This is the raw output of the datepicker: " + startDatePicker.getValue());

        String startDateString = startDatePicker.getValue().toString();
        String endDateString = endDatePicker.getValue().toString();

        String startTimeString = startTimeChoiceBox.getSelectionModel().getSelectedItem();
        String endTimeString = endTimeChoiceBox.getSelectionModel().getSelectedItem();

        String startDateTimeString = startDateString + " " + startTimeString;
        String endDateTimeString = endDateString + " " + endTimeString;

        LocalDateTime startDateTime = LocalDateTime.parse(startDateTimeString, dateTimeFormatter);
        LocalDateTime endDateTime = LocalDateTime.parse(endDateTimeString, dateTimeFormatter);

        if (endDateTime.isBefore(startDateTime)) {
            errorMessage += "End date/time cannot be earlier than start date/time!";
        }
        if (startDateTime.isEqual(endDateTime)) {
            errorMessage += "Appointment must be longer than zero minutes!";
        }
        //



        if (errorMessage.length() == 0) {
            System.out.println("All inputs are valid!~~~~~~~~~~~~~");
            return true;
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(modifyAppointmentScreenStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);
            alert.showAndWait();
            return false;

        }

    }

    public void setTextFields () {
        appointmentIdTextField.setText(Integer.toString(selectedAppointment.getAppointmentId()));
        titleTextField.setText(Integer.toString(selectedAppointment.getAppointmentId()));
        customerChoiceBox.setValue(selectedAppointment.getCustomerNameColumnString());
        titleTextField.setText(selectedAppointment.getTitle());
        descriptionTextField.setText(selectedAppointment.getDescription());
        locationTextField.setText(selectedAppointment.getLocation());
        contactTextField.setText(selectedAppointment.getContact());
        urlTextField.setText(selectedAppointment.getUrl());
        startDatePicker.setValue(selectedAppointment.getStartLDT().toLocalDate());
        startTimeChoiceBox.setValue(selectedAppointment.getStartTimeAsString());
        endDatePicker.setValue(selectedAppointment.getEndLDT().toLocalDate());
        endTimeChoiceBox.setValue(selectedAppointment.getEndTimeAsString());


    }




    public static String getCurrentUserName() {
        return currentUserName;
    }

    public static void setCurrentUserName(String currentUserName) {
        ModifyAppointmentController.currentUserName = currentUserName;
    }


    public boolean isNewAppointment() {
        return isNewAppointment;
    }

    public void setNewAppointment(boolean newAppointment) {
        isNewAppointment = newAppointment;
    }


    public Appointment getSelectedAppointment() {
        return selectedAppointment;
    }

    public void setSelectedAppointment(Appointment selectedAppointment) {
        this.selectedAppointment = selectedAppointment;
    }
}
