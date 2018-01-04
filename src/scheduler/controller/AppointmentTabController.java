package scheduler.controller;


import javafx.beans.property.ReadOnlyStringWrapper;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

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
    private static ObservableList<Appointment> localAppointmentList = FXCollections.observableArrayList();

    private static Connection dbConnect = DbConnection.getInstance().getConnection();
    private ArrayList<String> consultantList = new ArrayList<>();


    @FXML
    private TextArea textArea;
    @FXML
    private ChoiceBox<String> weeklyMonthlyChoiceBox;
    @FXML
    private ChoiceBox<String> consultantChoiceBox;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Label label;
    @FXML
    private TreeTableView<Appointment>  treeTableView = new TreeTableView<>();
    @FXML
    private TreeTableColumn<Appointment, String> timeColumn = new TreeTableColumn<>();
    @FXML
    private TreeTableColumn<Appointment, String> customerNameColumn = new TreeTableColumn<>();
    @FXML
    private TreeTableColumn<Appointment, String> appointmentTypeColumn = new TreeTableColumn<>();
    @FXML
    private TreeTableColumn<Appointment, String> consultantNameColumn = new TreeTableColumn<>();
    private String weeklyOrMonthly = "Weekly";
    private String userOrEveryone = "Everyone";
    private LocalDateTime beginningDateTime;
    private LocalDateTime endingDateTime;



    public void initialize(){
        populateConsultantList();
        weeklyMonthlyChoiceBox.getItems().addAll("Weekly", "Monthly");
        weeklyMonthlyChoiceBox.setValue("Weekly");
        consultantChoiceBox.getItems().addAll(MainApp.getCurrentUserName(),"Everyone");
        consultantChoiceBox.setValue(MainApp.getCurrentUserName());
        datePicker.setValue(LocalDate.now());


        pullAppointmentsFromDb();
        showMonthlyView();
        showWeeklyView();
        checkForUpcomingAppointments();


        // Listen for changes to localAppointmentList and re-call showView when list changes
        localAppointmentList.addListener((ListChangeListener<Appointment>) c -> {
            showView();
            showAppointmentDetails();

        });

        // toggle view between monthly and weekly based on user's selection
        weeklyMonthlyChoiceBox.setOnAction((event -> {
            showView();
        }));

        // toggle view between monthly and weekly based on user's selection
        consultantChoiceBox.setOnAction((event -> {
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



    private void checkForUpcomingAppointments() {
        LocalDateTime loginDateTime = LocalDateTime.now();
        LocalDateTime loginDateTimePlus15 = loginDateTime.plusMinutes(15);
        String reminderString = "";

        for (Appointment i : localAppointmentList) {
            if(i.getStartLDT().isAfter(loginDateTime) && i.getStartLDT().isBefore(loginDateTimePlus15)) {
                String customer = i.getCustomerNameColumnString();
                String startTime = i.getStartTimeAsString();
                String endTime = i.getEndTimeAsString();
                String title = i.getTitle();

                reminderString += startTime + " - " + endTime + "\t" + customer + "\t" + title + "\n";
                System.out.println("You have an upcoming appointment!");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Upcoming Activity!");
                alert.setHeaderText("Reminder:");
                alert.setContentText(reminderString);

                alert.showAndWait();


            }

        }
    }


    // Method used to initially show the schedule or to refresh the schedule view
    @FXML
    private void showView() {
        weeklyOrMonthly = weeklyMonthlyChoiceBox.getValue();
        userOrEveryone = consultantChoiceBox.getValue();
        if ((weeklyOrMonthly.equals("Monthly"))) {
            showMonthlyView();
        } else {
            showWeeklyView();
        }

    }


    // Deletes an appointment from the DB and local appointment list
    @FXML
    public void handleDelete () {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm");
        alert.setHeaderText("Are you sure you want to delete this appointment?");


        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK){
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

            // also delete from local appointment list
            localAppointmentList.remove(appointment);
        } else {
            alert.close();
        }

    }


    // Modify button clicked
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
            ModifyAppointmentController controller = MainApp.showModifyAppointmentScreen(appointment);

        }

    }



    private void showWeeklyView () {
        weeklyMonthlyChoiceBox.setValue("Weekly");
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


        List<TreeItem<Appointment>> sundayList = new ArrayList<>();
        List<TreeItem<Appointment>> mondayList = new ArrayList<>();
        List<TreeItem<Appointment>> tuesdayList = new ArrayList<>();
        List<TreeItem<Appointment>> wednesdayList = new ArrayList<>();
        List<TreeItem<Appointment>> thursdayList = new ArrayList<>();
        List<TreeItem<Appointment>> fridayList = new ArrayList<>();
        List<TreeItem<Appointment>> saturdayList = new ArrayList<>();


        for (Appointment a : localAppointmentList) {
            if(consultantChoiceBox.getValue().equals("Everyone") && a.getStartLDT().isAfter(beginningDateTime) && a.getStartLDT().isBefore(endingDateTime)) {
                DayOfWeek d = a.getStartLDT().getDayOfWeek();
                if (d.getValue() == 7) {
                    sundayList.add(new TreeItem<>(a));
                }
                if (d.getValue() == 1) {
                    mondayList.add(new TreeItem<>(a));
                }
                if (d.getValue() == 2) {
                    tuesdayList.add(new TreeItem<>(a));
                }
                if (d.getValue() == 3) {
                    wednesdayList.add(new TreeItem<>(a));
                }
                if (d.getValue() == 4) {
                    thursdayList.add(new TreeItem<>(a));
                }
                if (d.getValue() == 5) {
                    fridayList.add(new TreeItem<>(a));
                }
                if (d.getValue() == 6) {
                    saturdayList.add(new TreeItem<>(a));
                }
            } else if (a.getCreatedBy().equals(MainApp.getCurrentUserName()) && a.getStartLDT().isAfter(beginningDateTime) &&
                    a.getStartLDT().isBefore(endingDateTime)) {
                DayOfWeek d = a.getStartLDT().getDayOfWeek();
                if (d.getValue() == 7) {
                    sundayList.add(new TreeItem<>(a));
                }
                if (d.getValue() == 1) {
                    mondayList.add(new TreeItem<>(a));
                }
                if (d.getValue() == 2) {
                    tuesdayList.add(new TreeItem<>(a));
                }
                if (d.getValue() == 3) {
                    wednesdayList.add(new TreeItem<>(a));
                }
                if (d.getValue() == 4) {
                    thursdayList.add(new TreeItem<>(a));
                }
                if (d.getValue() == 5) {
                    fridayList.add(new TreeItem<>(a));
                }
                if (d.getValue() == 6) {
                    saturdayList.add(new TreeItem<>(a));
                }
            }
        }


        // Set sort order to appointment start time
        sundayList.sort(Comparator.comparing(t->t.getValue().getStartLDT()));
        mondayList.sort(Comparator.comparing(t->t.getValue().getStartLDT()));
        tuesdayList.sort(Comparator.comparing(t->t.getValue().getStartLDT()));
        wednesdayList.sort(Comparator.comparing(t->t.getValue().getStartLDT()));
        thursdayList.sort(Comparator.comparing(t->t.getValue().getStartLDT()));
        fridayList.sort(Comparator.comparing(t->t.getValue().getStartLDT()));
        saturdayList.sort(Comparator.comparing(t->t.getValue().getStartLDT()));

        // Add sorted Tree items as children to days of the week
        sunday.getChildren().addAll(sundayList);
        monday.getChildren().addAll(mondayList);
        tuesday.getChildren().addAll(tuesdayList);
        wednesday.getChildren().addAll(wednesdayList);
        thursday.getChildren().addAll(thursdayList);
        friday.getChildren().addAll(fridayList);
        saturday.getChildren().addAll(saturdayList);


        root.getChildren().setAll(sunday, monday, tuesday, wednesday, thursday, friday, saturday);


        // Defining cell content
        timeColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Appointment, String> p) ->
                new ReadOnlyStringWrapper(p.getValue().getValue().getTimeColumnString()));

        customerNameColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Appointment, String> p) ->
                new ReadOnlyStringWrapper(p.getValue().getValue().getCustomerNameColumnString()));

        appointmentTypeColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Appointment, String> p) ->
                new ReadOnlyStringWrapper(p.getValue().getValue().getTitle()));

        consultantNameColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Appointment, String> p) ->
                new ReadOnlyStringWrapper(p.getValue().getValue().getCreatedBy()));


        // Creating a tree table view
        treeTableView.setRoot(root);
        treeTableView.getColumns().clear();
        treeTableView.getColumns().add(timeColumn);
        treeTableView.getColumns().add(customerNameColumn);
        treeTableView.getColumns().add(appointmentTypeColumn);
        treeTableView.getColumns().add(consultantNameColumn);
        treeTableView.setShowRoot(false);
        weeklyOrMonthly = "Weekly";
        setLabelText();
    }



    @FXML
    private void showMonthlyView() {
        weeklyMonthlyChoiceBox.setValue("Monthly");
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

        List<TreeItem<Appointment>> day1List = new ArrayList<>();
        List<TreeItem<Appointment>> day2List = new ArrayList<>();
        List<TreeItem<Appointment>> day3List = new ArrayList<>();
        List<TreeItem<Appointment>> day4List = new ArrayList<>();
        List<TreeItem<Appointment>> day5List = new ArrayList<>();
        List<TreeItem<Appointment>> day6List = new ArrayList<>();
        List<TreeItem<Appointment>> day7List = new ArrayList<>();
        List<TreeItem<Appointment>> day8List = new ArrayList<>();
        List<TreeItem<Appointment>> day9List = new ArrayList<>();
        List<TreeItem<Appointment>> day10List = new ArrayList<>();
        List<TreeItem<Appointment>> day11List = new ArrayList<>();
        List<TreeItem<Appointment>> day12List = new ArrayList<>();
        List<TreeItem<Appointment>> day13List = new ArrayList<>();
        List<TreeItem<Appointment>> day14List = new ArrayList<>();
        List<TreeItem<Appointment>> day15List = new ArrayList<>();
        List<TreeItem<Appointment>> day16List = new ArrayList<>();
        List<TreeItem<Appointment>> day17List = new ArrayList<>();
        List<TreeItem<Appointment>> day18List = new ArrayList<>();
        List<TreeItem<Appointment>> day19List = new ArrayList<>();
        List<TreeItem<Appointment>> day20List = new ArrayList<>();
        List<TreeItem<Appointment>> day21List = new ArrayList<>();
        List<TreeItem<Appointment>> day22List = new ArrayList<>();
        List<TreeItem<Appointment>> day23List = new ArrayList<>();
        List<TreeItem<Appointment>> day24List = new ArrayList<>();
        List<TreeItem<Appointment>> day25List = new ArrayList<>();
        List<TreeItem<Appointment>> day26List = new ArrayList<>();
        List<TreeItem<Appointment>> day27List = new ArrayList<>();
        List<TreeItem<Appointment>> day28List = new ArrayList<>();
        List<TreeItem<Appointment>> day29List = new ArrayList<>();
        List<TreeItem<Appointment>> day30List = new ArrayList<>();
        List<TreeItem<Appointment>> day31List = new ArrayList<>();


        for (Appointment a : localAppointmentList) {
            if(consultantChoiceBox.getValue().equals("Everyone") && a.getStartLDT().isAfter(beginningDateTime) &&
                    a.getStartLDT().isBefore(endingDateTime)) {
                int i = a.getStartLDT().getDayOfMonth();
                if (i == 1) { day1List.add(new TreeItem<>(a)); }
                if (i == 2) { day2List.add(new TreeItem<>(a)); }
                if (i == 3) { day3List.add(new TreeItem<>(a)); }
                if (i == 4) { day4List.add(new TreeItem<>(a)); }
                if (i == 5) { day5List.add(new TreeItem<>(a)); }
                if (i == 6) { day6List.add(new TreeItem<>(a)); }
                if (i == 7) { day7List.add(new TreeItem<>(a)); }
                if (i == 8) { day8List.add(new TreeItem<>(a)); }
                if (i == 9) { day9List.add(new TreeItem<>(a)); }
                if (i == 10) { day10List.add(new TreeItem<>(a)); }
                if (i == 11) { day11List.add(new TreeItem<>(a)); }
                if (i == 12) { day12List.add(new TreeItem<>(a)); }
                if (i == 13) { day13List.add(new TreeItem<>(a)); }
                if (i == 14) { day14List.add(new TreeItem<>(a)); }
                if (i == 15) { day15List.add(new TreeItem<>(a)); }
                if (i == 16) { day16List.add(new TreeItem<>(a)); }
                if (i == 17) { day17List.add(new TreeItem<>(a)); }
                if (i == 18) { day18List.add(new TreeItem<>(a)); }
                if (i == 19) { day19List.add(new TreeItem<>(a)); }
                if (i == 20) { day20List.add(new TreeItem<>(a)); }
                if (i == 21) { day21List.add(new TreeItem<>(a)); }
                if (i == 22) { day22List.add(new TreeItem<>(a)); }
                if (i == 23) { day23List.add(new TreeItem<>(a)); }
                if (i == 24) { day24List.add(new TreeItem<>(a)); }
                if (i == 25) { day25List.add(new TreeItem<>(a)); }
                if (i == 26) { day26List.add(new TreeItem<>(a)); }
                if (i == 27) { day27List.add(new TreeItem<>(a)); }
                if (i == 28) { day28List.add(new TreeItem<>(a)); }
                if (i == 29) { day29List.add(new TreeItem<>(a)); }
                if (i == 30) { day30List.add(new TreeItem<>(a)); }
                if (i == 31) { day31List.add(new TreeItem<>(a)); }
            } else if (a.getCreatedBy().equals(MainApp.getCurrentUserName()) && a.getStartLDT().isAfter(beginningDateTime) &&
                    a.getStartLDT().isBefore(endingDateTime)) {
                int i = a.getStartLDT().getDayOfMonth();
                if (i == 1) { day1List.add(new TreeItem<>(a)); }
                if (i == 2) { day2List.add(new TreeItem<>(a)); }
                if (i == 3) { day3List.add(new TreeItem<>(a)); }
                if (i == 4) { day4List.add(new TreeItem<>(a)); }
                if (i == 5) { day5List.add(new TreeItem<>(a)); }
                if (i == 6) { day6List.add(new TreeItem<>(a)); }
                if (i == 7) { day7List.add(new TreeItem<>(a)); }
                if (i == 8) { day8List.add(new TreeItem<>(a)); }
                if (i == 9) { day9List.add(new TreeItem<>(a)); }
                if (i == 10) { day10List.add(new TreeItem<>(a)); }
                if (i == 11) { day11List.add(new TreeItem<>(a)); }
                if (i == 12) { day12List.add(new TreeItem<>(a)); }
                if (i == 13) { day13List.add(new TreeItem<>(a)); }
                if (i == 14) { day14List.add(new TreeItem<>(a)); }
                if (i == 15) { day15List.add(new TreeItem<>(a)); }
                if (i == 16) { day16List.add(new TreeItem<>(a)); }
                if (i == 17) { day17List.add(new TreeItem<>(a)); }
                if (i == 18) { day18List.add(new TreeItem<>(a)); }
                if (i == 19) { day19List.add(new TreeItem<>(a)); }
                if (i == 20) { day20List.add(new TreeItem<>(a)); }
                if (i == 21) { day21List.add(new TreeItem<>(a)); }
                if (i == 22) { day22List.add(new TreeItem<>(a)); }
                if (i == 23) { day23List.add(new TreeItem<>(a)); }
                if (i == 24) { day24List.add(new TreeItem<>(a)); }
                if (i == 25) { day25List.add(new TreeItem<>(a)); }
                if (i == 26) { day26List.add(new TreeItem<>(a)); }
                if (i == 27) { day27List.add(new TreeItem<>(a)); }
                if (i == 28) { day28List.add(new TreeItem<>(a)); }
                if (i == 29) { day29List.add(new TreeItem<>(a)); }
                if (i == 30) { day30List.add(new TreeItem<>(a)); }
                if (i == 31) { day31List.add(new TreeItem<>(a)); }
            }
        }

        // Set sort order to appointment start time
        day1List.sort(Comparator.comparing(t->t.getValue().getStartLDT()));
        day2List.sort(Comparator.comparing(t->t.getValue().getStartLDT()));
        day3List.sort(Comparator.comparing(t->t.getValue().getStartLDT()));
        day4List.sort(Comparator.comparing(t->t.getValue().getStartLDT()));
        day5List.sort(Comparator.comparing(t->t.getValue().getStartLDT()));
        day6List.sort(Comparator.comparing(t->t.getValue().getStartLDT()));
        day7List.sort(Comparator.comparing(t->t.getValue().getStartLDT()));
        day8List.sort(Comparator.comparing(t->t.getValue().getStartLDT()));
        day9List.sort(Comparator.comparing(t->t.getValue().getStartLDT()));
        day10List.sort(Comparator.comparing(t->t.getValue().getStartLDT()));
        day11List.sort(Comparator.comparing(t->t.getValue().getStartLDT()));
        day12List.sort(Comparator.comparing(t->t.getValue().getStartLDT()));
        day13List.sort(Comparator.comparing(t->t.getValue().getStartLDT()));
        day14List.sort(Comparator.comparing(t->t.getValue().getStartLDT()));
        day15List.sort(Comparator.comparing(t->t.getValue().getStartLDT()));
        day16List.sort(Comparator.comparing(t->t.getValue().getStartLDT()));
        day17List.sort(Comparator.comparing(t->t.getValue().getStartLDT()));
        day18List.sort(Comparator.comparing(t->t.getValue().getStartLDT()));
        day19List.sort(Comparator.comparing(t->t.getValue().getStartLDT()));
        day20List.sort(Comparator.comparing(t->t.getValue().getStartLDT()));
        day21List.sort(Comparator.comparing(t->t.getValue().getStartLDT()));
        day22List.sort(Comparator.comparing(t->t.getValue().getStartLDT()));
        day23List.sort(Comparator.comparing(t->t.getValue().getStartLDT()));
        day24List.sort(Comparator.comparing(t->t.getValue().getStartLDT()));
        day25List.sort(Comparator.comparing(t->t.getValue().getStartLDT()));
        day26List.sort(Comparator.comparing(t->t.getValue().getStartLDT()));
        day27List.sort(Comparator.comparing(t->t.getValue().getStartLDT()));
        day28List.sort(Comparator.comparing(t->t.getValue().getStartLDT()));
        day29List.sort(Comparator.comparing(t->t.getValue().getStartLDT()));
        day30List.sort(Comparator.comparing(t->t.getValue().getStartLDT()));
        day31List.sort(Comparator.comparing(t->t.getValue().getStartLDT()));

        // Add sorted Tree items as children to days of the month
        day1.getChildren().addAll(day1List);
        day2.getChildren().addAll(day2List);
        day3.getChildren().addAll(day3List);
        day4.getChildren().addAll(day4List);
        day5.getChildren().addAll(day5List);
        day6.getChildren().addAll(day6List);
        day7.getChildren().addAll(day7List);
        day8.getChildren().addAll(day8List);
        day9.getChildren().addAll(day9List);
        day10.getChildren().addAll(day10List);
        day11.getChildren().addAll(day11List);
        day12.getChildren().addAll(day12List);
        day13.getChildren().addAll(day13List);
        day14.getChildren().addAll(day14List);
        day15.getChildren().addAll(day15List);
        day16.getChildren().addAll(day16List);
        day17.getChildren().addAll(day17List);
        day18.getChildren().addAll(day18List);
        day19.getChildren().addAll(day19List);
        day20.getChildren().addAll(day20List);
        day21.getChildren().addAll(day21List);
        day22.getChildren().addAll(day22List);
        day23.getChildren().addAll(day23List);
        day24.getChildren().addAll(day24List);
        day25.getChildren().addAll(day25List);
        day26.getChildren().addAll(day26List);
        day27.getChildren().addAll(day27List);
        day28.getChildren().addAll(day28List);
        day29.getChildren().addAll(day29List);
        day30.getChildren().addAll(day30List);
        day31.getChildren().addAll(day31List);


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



        // Defining cell content
        timeColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Appointment, String> p) ->
                new ReadOnlyStringWrapper(p.getValue().getValue().getTimeColumnString()));

        customerNameColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Appointment, String> p) ->
                new ReadOnlyStringWrapper(p.getValue().getValue().getCustomerNameColumnString()));

        appointmentTypeColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Appointment, String> p) ->
                new ReadOnlyStringWrapper(p.getValue().getValue().getTitle()));

        consultantNameColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Appointment, String> p) ->
                new ReadOnlyStringWrapper(p.getValue().getValue().getCreatedBy()));

        // Creating a tree table view
        treeTableView.setRoot(root);
        treeTableView.getColumns().clear();
        treeTableView.getColumns().add(timeColumn);
        treeTableView.getColumns().add(customerNameColumn);
        treeTableView.getColumns().add(appointmentTypeColumn);
        treeTableView.getColumns().add(consultantNameColumn);
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
        MainApp.showAddAppointmentScreen();

    }


    private void pullAppointmentsFromDb() {
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

            textAreaString += ("Customer:  " + selectedApt.getCustomerNameColumnString() + "\n");
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
            textAreaString += ("Consultant:  " + selectedApt.getCreatedBy() + "\n");
            textAreaString += ("\n");

            textAreaString += ("Last Update:  " + selectedApt.getLastUpdateDateAsLocalString() + "\n");
            textAreaString += ("Last Updated By:  " + selectedApt.getLastUpdateBy() + "\n");


            textArea.setText(textAreaString);
        }

    }


    private void populateConsultantList() {
        String queryString = "SELECT userName FROM user;";
        try {
            Statement statement = dbConnect.prepareStatement(queryString);
            statement.executeQuery(queryString);
            ResultSet rs = statement.getResultSet();

            while(rs.next()) {
                consultantList.add(rs.getString("userName"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}























