package scheduler.controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import scheduler.model.Appointment;
import scheduler.model.DbConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;


public class ModifyAppointmentController {

    private boolean isNewAppointment = true;
    private static Connection dbConnect;
    private Stage modifyAppointmentScreenStage;
    private static String currentUserName;
    private static AppointmentTabController appointmentTabController;
    private Appointment selectedAppoinment;




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
        if(isInputValid()) {
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
        appointmentIdTextField.setText(Integer.toString(selectedAppoinment.getAppointmentId()));
        titleTextField.setText(Integer.toString(selectedAppoinment.getAppointmentId()));
        customerChoiceBox.setValue(selectedAppoinment.getCustomerNameColumnString());
        titleTextField.setText(selectedAppoinment.getTitle());
        descriptionTextField.setText(selectedAppoinment.getDescription());
        locationTextField.setText(selectedAppoinment.getLocation());
        contactTextField.setText(selectedAppoinment.getContact());
        urlTextField.setText(selectedAppoinment.getUrl());
        startDatePicker.setValue(selectedAppoinment.getStartLDT().toLocalDate());
        startTimeChoiceBox.setValue(selectedAppoinment.getStartTimeAsString());
        endDatePicker.setValue(selectedAppoinment.getEndLDT().toLocalDate());
        endTimeChoiceBox.setValue(selectedAppoinment.getEndTimeAsString());


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


    public Appointment getSelectedAppoinment() {
        return selectedAppoinment;
    }

    public void setSelectedAppoinment(Appointment selectedAppoinment) {
        this.selectedAppoinment = selectedAppoinment;
    }
}
