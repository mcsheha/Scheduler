package scheduler.controller;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import scheduler.model.Appointment;
import scheduler.model.AppointmentList;
import scheduler.model.DbConnection;
import java.sql.Connection;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;


public class AppointmentTabController {

    private static AppointmentTabController firstInstance = null;
    private AppointmentList appointmentList;
    private static Connection dbConnect;

    @FXML
    private ChoiceBox<String> choiceBox;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Label label;
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
        datePicker.setValue(LocalDate.now());
        showMonthlyView();
        showWeeklyView();

        this.appointmentList = AppointmentList.getInstance();



        // toggle view between monthly and weekly based on user's selection
        choiceBox.setOnAction((event -> {
            showView();
        }));

        datePicker.setOnAction((event -> {
            setLabelText();
            showView();
        }));

    }



    public void showView() {
        String selectedView = choiceBox.getSelectionModel().getSelectedItem();
        if ((selectedView.equals("Monthly"))) {
            showMonthlyView();
        } else {
            showWeeklyView();
        }
    }



    public void showWeeklyView () {
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


        // Adding tree items to root
        if(Objects.equals(choiceBox.getValue(), "Weekly")) {
            System.out.println("They are equals!");
        }
        else {
            System.out.println("They are not equals!");
        }


        for (Appointment a : appointmentList.getAppointmentArrayList()) {
            if(a.getStartDateTime().isAfter(beginningDateTime) && a.getStartDateTime().isBefore(endingDateTime)) {
                DayOfWeek d = a.getStartDateTime().getDayOfWeek();
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

        // Defining cell content
        timeColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Appointment, String> p) ->
                new ReadOnlyStringWrapper(p.getValue().getValue().getTimeColumnString()));

        customerNameColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Appointment, String> p) ->
                new ReadOnlyStringWrapper(p.getValue().getValue().getCustomerNameColumnString()));

        appointmentTypeColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Appointment, String> p) ->
                new ReadOnlyStringWrapper(p.getValue().getValue().getAppointmentTypeString()));

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

        for (Appointment a : appointmentList.getAppointmentArrayList()) {
            if(a.getStartDateTime().isAfter(beginningDateTime) && a.getStartDateTime().isBefore(endingDateTime)) {
                int i = a.getStartDateTime().getDayOfMonth();
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

        // Defining cell content
        timeColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Appointment, String> p) ->
                new ReadOnlyStringWrapper(p.getValue().getValue().getTimeColumnString()));

        customerNameColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Appointment, String> p) ->
                new ReadOnlyStringWrapper(p.getValue().getValue().getCustomerNameColumnString()));

        appointmentTypeColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<Appointment, String> p) ->
                new ReadOnlyStringWrapper(p.getValue().getValue().getAppointmentTypeString()));

        // Creating a tree table view
        treeTableView.setRoot(root);
        treeTableView.getColumns().clear();
        treeTableView.getColumns().add(timeColumn);
        treeTableView.getColumns().add(customerNameColumn);
        treeTableView.getColumns().add(appointmentTypeColumn);
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
        this.appointmentList = AppointmentList.getInstance();
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








}
