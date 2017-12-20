package scheduler.controller;

import java.util.stream.*;
import static java.util.Comparator.*;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import scheduler.MainApp;
import scheduler.model.Appointment;
import scheduler.model.DbConnection;

import java.io.IOException;
import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;


public class AppointmentTabController {

    private static AppointmentTabController firstInstance = null;
    //private static AppointmentList appointmentList;
    public static ObservableList<Appointment> localAppointmentList = FXCollections.observableArrayList();

    private static Connection dbConnect;
    private MainApp mainApp;

    @FXML
    private TextArea textArea;
    @FXML
    private ChoiceBox<String> choiceBox;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Label label;
    @FXML
    private Button addAppointmentButton;
    @FXML
    private AnchorPane appointmentsTab;
    @FXML
    private TreeTableView<Appointment>  treeTableView = new TreeTableView<>();
    @FXML
    private TreeTableColumn<Appointment, String> timeColumn = new TreeTableColumn<>();
    @FXML
    private TreeTableColumn<Appointment, String> customerNameColumn = new TreeTableColumn<>();
    @FXML
    private TreeTableColumn<Appointment, String> appointmentTypeColumn = new TreeTableColumn<>();

    private String weeklyOrMonthly = "Weekly";

    private LocalDateTime beginningDateTime;

    private LocalDateTime endingDateTime;




    public void initialize(){
        choiceBox.getItems().addAll("Weekly", "Monthly");
        choiceBox.setValue("Weekly");
        dbConnect = DbConnection.getInstance().getConnection();
        this.mainApp = MainApp.getInstance();
        datePicker.setValue(LocalDate.now());


        pullAppointmentsFromDb();
        showMonthlyView();
        showWeeklyView();

        // Listen for changes to localAppointmentList and re-call showView when list changes
        localAppointmentList.addListener((ListChangeListener<Appointment>) c -> {
            showView();
            showAppointmentDetails();

        });

        // toggle view between monthly and weekly based on user's selection
        choiceBox.setOnAction((event -> {
            showView();
        }));


        //localAppointmentList.sort(Comparator.comparing(Appointment::getStartLDT));


        // Set the Appointment pane based on date selected in datePicker
        datePicker.setOnAction((event -> {
            setLabelText();
            showView();
        }));

        // add listener to TreeTable for when an Item is selected to populate the appointment details pane
        treeTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
           if (newSelection != null  && !newSelection.getValue().isDummyAppointment()) {
               showAppointmentDetails();
           }
           else {textArea.setText("");}
        });


        showAppointmentDetails();
    }


    @FXML
    public void showView() {
        weeklyOrMonthly = choiceBox.getValue();
        if ((weeklyOrMonthly.equals("Monthly"))) {
            showMonthlyView();
        } else {
            showWeeklyView();
        }

    }

    @FXML
    public void handleDelete () {
        Appointment appointment = treeTableView.getSelectionModel().getSelectedItem().getValue();
        int appointmentId = appointment.getAppointmentId();

        String sql = "DELETE FROM appointment WHERE appointmentId = " + appointmentId + ";";

        try {
            PreparedStatement psmt = dbConnect.prepareStatement(sql);
            psmt.execute();
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

        // also delete from local customer list
        localAppointmentList.remove(appointment);
    }


    public void handleModifyButton () throws IOException {
        if (treeTableView.getSelectionModel().getSelectedItem() == null ||
                treeTableView.getSelectionModel().getSelectedItem().getValue().isDummyAppointment()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No Appointment Selected");
            alert.setHeaderText("No Appointment Selected");
            alert.setContentText("You must select an Appointment from the table.");

            alert.showAndWait();
        }

        else {
            Appointment appointment = treeTableView.getSelectionModel().getSelectedItem().getValue();
            ModifyAppointmentController controller = mainApp.showModifyAppointmentScreen(appointment);

        }

    }



    public void showWeeklyView () {
        choiceBox.setValue("Weekly");
        weeklyOrMonthly = "Weekly";
        setBeginningAndEndDateTime();

        // Creating tree items
        final TreeItem<Appointment> sunday = new TreeItem<>(new Appointment("Sunday",true));
        final TreeItem<Appointment> monday = new TreeItem<>(new Appointment("Monday",true));
        final TreeItem<Appointment> tuesday = new TreeItem<>(new Appointment("Tuesday",true));
        final TreeItem<Appointment> wednesday = new TreeItem<>(new Appointment("Wednesday",true));
        final TreeItem<Appointment> thursday = new TreeItem<>(new Appointment("Thursday",true));
        final TreeItem<Appointment> friday = new TreeItem<>(new Appointment("Friday",true));
        final TreeItem<Appointment> saturday = new TreeItem<>(new Appointment("Saturday",true));

        // Creating the root element
        final TreeItem<Appointment> root = new TreeItem<>(new Appointment("Root node", true));
        root.setExpanded(true);
        sunday.setExpanded(true);
        monday.setExpanded(true);
        tuesday.setExpanded(true);
        wednesday.setExpanded(true);
        thursday.setExpanded(true);
        friday.setExpanded(true);
        saturday.setExpanded(true);




        for (Appointment a : localAppointmentList) {
            if(a.getStartLDT().isAfter(beginningDateTime) && a.getStartLDT().isBefore(endingDateTime)) {
                DayOfWeek d = a.getStartLDT().getDayOfWeek();
                if (d.getValue() == 7) {
                    sunday.getChildren().add(new TreeItem<>(a));
                }
                if (d.getValue() == 1) {
                    monday.getChildren().add(new TreeItem<>(a));
                }
                if (d.getValue() == 2) {
                    tuesday.getChildren().add(new TreeItem<>(a));
                }
                if (d.getValue() == 3) {
                    wednesday.getChildren().add(new TreeItem<>(a));
                }
                if (d.getValue() == 4) {
                    thursday.getChildren().add(new TreeItem<>(a));
                }
                if (d.getValue() == 5) {
                    friday.getChildren().add(new TreeItem<>(a));
                }
                if (d.getValue() == 6) {
                    saturday.getChildren().add(new TreeItem<>(a));
                }
            }

        }







        root.getChildren().setAll(sunday, monday, tuesday, wednesday, thursday, friday, saturday);

        // Creating a column
        //TreeTableColumn<String,String> column1 = new TreeTableColumn<>("Column1");

        localAppointmentList.sorted(comparing(Appointment::getStartLDT, nullsLast(naturalOrder())));



        // Defining cell content
        timeColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Appointment, String> p) ->
                new ReadOnlyStringWrapper(p.getValue().getValue().getTimeColumnString()));

        customerNameColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Appointment, String> p) ->
                new ReadOnlyStringWrapper(p.getValue().getValue().getCustomerNameColumnString()));

        appointmentTypeColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Appointment, String> p) ->
                new ReadOnlyStringWrapper(p.getValue().getValue().getTitle()));


        // Creating a tree table view
        treeTableView.setRoot(root);
        treeTableView.getColumns().clear();
        treeTableView.getColumns().add(timeColumn);
        treeTableView.getColumns().add(customerNameColumn);
        treeTableView.getColumns().add(appointmentTypeColumn);
        treeTableView.setShowRoot(false);
        weeklyOrMonthly = "Weekly";
        setLabelText();
    }



    @FXML
    public void showMonthlyView() {
        choiceBox.setValue("Monthly");
        weeklyOrMonthly = "Monthly";
        setBeginningAndEndDateTime();

        final TreeItem<Appointment> day1 = new TreeItem<>(new Appointment("1",true));
        final TreeItem<Appointment> day2 = new TreeItem<>(new Appointment("2",true));
        final TreeItem<Appointment> day3 = new TreeItem<>(new Appointment("3",true));
        final TreeItem<Appointment> day4 = new TreeItem<>(new Appointment("4",true));
        final TreeItem<Appointment> day5 = new TreeItem<>(new Appointment("5",true));
        final TreeItem<Appointment> day6 = new TreeItem<>(new Appointment("6",true));
        final TreeItem<Appointment> day7 = new TreeItem<>(new Appointment("7",true));
        final TreeItem<Appointment> day8 = new TreeItem<>(new Appointment("8",true));
        final TreeItem<Appointment> day9 = new TreeItem<>(new Appointment("9",true));
        final TreeItem<Appointment> day10 = new TreeItem<>(new Appointment("10",true));
        final TreeItem<Appointment> day11 = new TreeItem<>(new Appointment("11",true));
        final TreeItem<Appointment> day12 = new TreeItem<>(new Appointment("12",true));
        final TreeItem<Appointment> day13 = new TreeItem<>(new Appointment("13",true));
        final TreeItem<Appointment> day14 = new TreeItem<>(new Appointment("14",true));
        final TreeItem<Appointment> day15 = new TreeItem<>(new Appointment("15",true));
        final TreeItem<Appointment> day16 = new TreeItem<>(new Appointment("16",true));
        final TreeItem<Appointment> day17 = new TreeItem<>(new Appointment("17",true));
        final TreeItem<Appointment> day18 = new TreeItem<>(new Appointment("18",true));
        final TreeItem<Appointment> day19 = new TreeItem<>(new Appointment("19",true));
        final TreeItem<Appointment> day20 = new TreeItem<>(new Appointment("20",true));
        final TreeItem<Appointment> day21 = new TreeItem<>(new Appointment("21",true));
        final TreeItem<Appointment> day22 = new TreeItem<>(new Appointment("22",true));
        final TreeItem<Appointment> day23 = new TreeItem<>(new Appointment("23",true));
        final TreeItem<Appointment> day24 = new TreeItem<>(new Appointment("24",true));
        final TreeItem<Appointment> day25 = new TreeItem<>(new Appointment("25",true));
        final TreeItem<Appointment> day26 = new TreeItem<>(new Appointment("26",true));
        final TreeItem<Appointment> day27 = new TreeItem<>(new Appointment("27",true));
        final TreeItem<Appointment> day28 = new TreeItem<>(new Appointment("28",true));
        final TreeItem<Appointment> day29 = new TreeItem<>(new Appointment("29",true));
        final TreeItem<Appointment> day30 = new TreeItem<>(new Appointment("30", true));
        final TreeItem<Appointment> day31 = new TreeItem<>(new Appointment("31", true));


        final TreeItem<Appointment> root = new TreeItem<>(new Appointment("Root node", true));
        root.setExpanded(true);
        //day12.getChildren().setAll(day55);
        day1.setExpanded(true);
        day2.setExpanded(true);
        day3.setExpanded(true);
        day4.setExpanded(true);
        day5.setExpanded(true);
        day6.setExpanded(true);
        day7.setExpanded(true);
        day8.setExpanded(true);
        day9.setExpanded(true);
        day10.setExpanded(true);
        day11.setExpanded(true);
        day12.setExpanded(true);
        day13.setExpanded(true);
        day14.setExpanded(true);
        day15.setExpanded(true);
        day16.setExpanded(true);
        day17.setExpanded(true);
        day18.setExpanded(true);
        day19.setExpanded(true);
        day20.setExpanded(true);
        day21.setExpanded(true);
        day22.setExpanded(true);
        day23.setExpanded(true);
        day24.setExpanded(true);
        day25.setExpanded(true);
        day26.setExpanded(true);
        day27.setExpanded(true);
        day28.setExpanded(true);
        day29.setExpanded(true);
        day30.setExpanded(true);
        day31.setExpanded(true);

        for (Appointment a : localAppointmentList) {
            if(a.getStartLDT().isAfter(beginningDateTime) && a.getStartLDT().isBefore(endingDateTime)) {
                int i = a.getStartLDT().getDayOfMonth();
                if (i == 1) { day1.getChildren().add(new TreeItem<>(a)); }
                if (i == 2) { day2.getChildren().add(new TreeItem<>(a)); }
                if (i == 3) { day3.getChildren().add(new TreeItem<>(a)); }
                if (i == 4) { day4.getChildren().add(new TreeItem<>(a)); }
                if (i == 5) { day5.getChildren().add(new TreeItem<>(a)); }
                if (i == 6) { day6.getChildren().add(new TreeItem<>(a)); }
                if (i == 7) { day7.getChildren().add(new TreeItem<>(a)); }
                if (i == 8) { day8.getChildren().add(new TreeItem<>(a)); }
                if (i == 9) { day9.getChildren().add(new TreeItem<>(a)); }
                if (i == 10) { day10.getChildren().add(new TreeItem<>(a)); }
                if (i == 11) { day11.getChildren().add(new TreeItem<>(a)); }
                if (i == 12) { day12.getChildren().add(new TreeItem<>(a)); }
                if (i == 13) { day13.getChildren().add(new TreeItem<>(a)); }
                if (i == 14) { day14.getChildren().add(new TreeItem<>(a)); }
                if (i == 15) { day15.getChildren().add(new TreeItem<>(a)); }
                if (i == 16) { day16.getChildren().add(new TreeItem<>(a)); }
                if (i == 17) { day17.getChildren().add(new TreeItem<>(a)); }
                if (i == 18) { day18.getChildren().add(new TreeItem<>(a)); }
                if (i == 19) { day19.getChildren().add(new TreeItem<>(a)); }
                if (i == 20) { day20.getChildren().add(new TreeItem<>(a)); }
                if (i == 21) { day21.getChildren().add(new TreeItem<>(a)); }
                if (i == 22) { day22.getChildren().add(new TreeItem<>(a)); }
                if (i == 23) { day23.getChildren().add(new TreeItem<>(a)); }
                if (i == 24) { day24.getChildren().add(new TreeItem<>(a)); }
                if (i == 25) { day25.getChildren().add(new TreeItem<>(a)); }
                if (i == 26) { day26.getChildren().add(new TreeItem<>(a)); }
                if (i == 27) { day27.getChildren().add(new TreeItem<>(a)); }
                if (i == 28) { day28.getChildren().add(new TreeItem<>(a)); }
                if (i == 29) { day29.getChildren().add(new TreeItem<>(a)); }
                if (i == 30) { day30.getChildren().add(new TreeItem<>(a)); }
                if (i == 31) { day31.getChildren().add(new TreeItem<>(a)); }


            }
        }


        root.getChildren().setAll(day1, day2, day3, day4, day5, day6, day7, day8, day9, day10, day11, day12, day13, day14,
            day15, day16, day17, day18, day19, day20, day21, day22, day23, day24, day25, day26, day27, day28);

        if (datePicker.getValue().lengthOfMonth() > 28) {
            root.getChildren().addAll(day29);
        }
        if (datePicker.getValue().lengthOfMonth() > 29) {
            root.getChildren().addAll(day30);
        }
        if (datePicker.getValue().lengthOfMonth() > 30) {
            root.getChildren().addAll(day31);
        }

        //localAppointmentList.sorted(comparing(Appointment::getStartLDT, nullsLast(naturalOrder())));


        // Defining cell content
        timeColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Appointment, String> p) ->
                new ReadOnlyStringWrapper(p.getValue().getValue().getTimeColumnString()));

        customerNameColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Appointment, String> p) ->
                new ReadOnlyStringWrapper(p.getValue().getValue().getCustomerNameColumnString()));

        appointmentTypeColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Appointment, String> p) ->
                new ReadOnlyStringWrapper(p.getValue().getValue().getTitle()));

        // Creating a tree table view
        treeTableView.setRoot(root);
        treeTableView.getColumns().clear();
        treeTableView.getColumns().add(timeColumn);
        treeTableView.getColumns().add(customerNameColumn);
        treeTableView.getColumns().add(appointmentTypeColumn);
        treeTableView.sort();
        treeTableView.setShowRoot(false);
        weeklyOrMonthly = "Monthly";
        setLabelText();
    }


    public static AppointmentTabController getInstance() {
        if (firstInstance == null) {
            firstInstance = new AppointmentTabController();
        }
        return firstInstance;
    }


    public AppointmentTabController () {
        //this.appointmentList = AppointmentList.getInstance();
        this.dbConnect = DbConnection.getInstance().getConnection();

    }

    @FXML
    public void setLabelText () {
        String labelText;
        if(weeklyOrMonthly.equals("Monthly")) {
            String month = datePicker.getValue().getMonth().getDisplayName(TextStyle.FULL, Locale.US);
            String year = Integer.toString(datePicker.getValue().getYear());
            labelText = month + " " + year;
        }
        else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMMM yyyy");
            labelText = "Weekly View";
            LocalDate dateSelected = datePicker.getValue();
            DayOfWeek dayOfWeek = dateSelected.getDayOfWeek();
            int offset = dayOfWeek.getValue();
            if (offset == 7) {
                offset = 0;
            }
            LocalDate startOfWeek = dateSelected.minusDays(offset);
            int daysToAdd = 6 - offset;
            LocalDate endOfWeek = dateSelected.plusDays(daysToAdd);
            labelText = startOfWeek.format(formatter) + " - " + endOfWeek.format(formatter);
        }
        label.setText(labelText);
    }



    public void setBeginningAndEndDateTime () {
        if(weeklyOrMonthly.equals("Monthly")) {
            beginningDateTime = datePicker.getValue().withDayOfMonth(1).atStartOfDay();
            int lengthOfMonth = datePicker.getValue().lengthOfMonth();
            endingDateTime = datePicker.getValue().withDayOfMonth(lengthOfMonth).atTime(23,59,59);
        }
        else {
            int dayOfWeekValue = datePicker.getValue().getDayOfWeek().getValue();
            if (dayOfWeekValue == 7) {
                dayOfWeekValue = 0;
            }
            int offset = 6 - dayOfWeekValue;
            beginningDateTime = datePicker.getValue().minusDays(dayOfWeekValue).atStartOfDay();
            endingDateTime = datePicker.getValue().plusDays(offset).atTime(23,59,59);

        }
    }

    @FXML
    public void showAddAppointment() throws IOException {
        mainApp.showAddAppointmentScreen();

    }

    public void pullAppointmentsFromDb() {
        //ObservableList<Appointment> tempAppointmentList = FXCollections.observableArrayList();
        String sql = "SELECT * FROM appointment;";
        PreparedStatement psmt = null;


        try {
            psmt = dbConnect.prepareStatement(sql);
            psmt.executeQuery();
            ResultSet rs = psmt.getResultSet();

            while (rs.next()) {
                int appointmentId = rs.getInt ("appointmentId");
                int customerId = rs.getInt("customerId");
                String title = rs.getString("title");
                String description = rs.getString("description");
                String location = rs.getString("location");
                String contact = rs.getString("contact");
                String url = rs.getString("url");
                String start = rs.getString("start");
                String end = rs.getString("end");
                String createDate = rs.getString("createDate");
                String createdBy = rs.getString("createdBy");
                String lastUpdate = rs.getString("lastUpdate");
                String lastUpdateBy = rs.getString("lastUpdateBy");
                LocalDateTime startLDT = HomeScreenController.convertZuluToLocal(rs.getTimestamp("start").toLocalDateTime());
                LocalDateTime endLDT = HomeScreenController.convertZuluToLocal(rs.getTimestamp("end").toLocalDateTime());
                LocalDateTime lastUpdateLDT = HomeScreenController.convertZuluToLocal(rs.getTimestamp("lastUpdate").toLocalDateTime());
                LocalDateTime createdDateLDT = HomeScreenController.convertZuluToLocal(rs.getTimestamp("createDate").toLocalDateTime());


                localAppointmentList.add(new Appointment(appointmentId, customerId, title, description, location,
                        contact, url, startLDT, endLDT, createdDateLDT, createdBy, lastUpdateLDT, lastUpdateBy));

            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }



    }


    public void addToAppointmentList(Appointment appointment) {
        localAppointmentList.add(appointment);
    }

    public void removeFromAppointmentList(int appointmentId) {
        for(Appointment a : localAppointmentList) {
            if(a.getAppointmentId() == appointmentId) {
                localAppointmentList.remove(a);
                //System.out.println("Found and removed the appointment!");
                return;
            }
        }

    }


    public LocalDateTime getCreateDateLDT(int appointmentId) {
        String queryString = "SELECT createDate FROM appointment WHERE appointmentId = " + appointmentId + ";";
        LocalDateTime createdDateLDT = LocalDateTime.now();

        try {
            Statement statement = dbConnect.prepareStatement(queryString);
            statement.executeQuery(queryString);
            ResultSet rs = statement.getResultSet();

            while (rs.next()) {
                createdDateLDT = rs.getTimestamp("createDate").toLocalDateTime();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return createdDateLDT;
    }


    public String getCreatedBy(int appointmentId) {
        String queryString = "SELECT createdBy FROM appointment WHERE appointmentId = " + appointmentId + ";";
        String resultString = "";

        try {
            Statement statement = dbConnect.prepareStatement(queryString);
            statement.executeQuery(queryString);
            ResultSet rs = statement.getResultSet();

            while (rs.next()) {
                resultString = rs.getString("createdBy");

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return resultString;
    }

    public void showAppointmentDetails () {
        String textAreaString = "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");

        if (treeTableView.getSelectionModel().getSelectedItem() != null) {
            Appointment selectedApt = treeTableView.getSelectionModel().getSelectedItem().getValue();

            textAreaString += ("Appointment ID:  " + Integer.toString(selectedApt.getAppointmentId()) + "\n");
            textAreaString += ("\n");

            textAreaString += ("Title:  " + selectedApt.getTitle() + "\n");
            textAreaString += ("\n");

            textAreaString += ("Description:  " + selectedApt.getDescription() + "\n");
            textAreaString += ("Location:  " + selectedApt.getLocation() + "\n");
            textAreaString += ("Contact:  " + selectedApt.getContact() + "\n");
            textAreaString += ("URL:  " + selectedApt.getUrl() + "\n");
            textAreaString += ("\n");

            textAreaString += ("Date:  " + selectedApt.getStartLDT().format(formatter) + "\n");
            textAreaString += ("Time:  " + selectedApt.getStartTimeAsString() + " - " + selectedApt.getEndTimeAsString() + "\n");
            textAreaString += ("\n");

            textAreaString += ("Created Date:  " + selectedApt.getCreatedDateAsLocalString() + "\n");
            textAreaString += ("Created By:  " + selectedApt.getCreatedBy() + "\n");
            textAreaString += ("\n");

            textAreaString += ("Last Update:  " + selectedApt.getLastUpdateDateAsLocalString() + "\n");
            textAreaString += ("Last Updated By:  " + selectedApt.getLastUpdateBy() + "\n");


            textArea.setText(textAreaString);
        }

    }


}























