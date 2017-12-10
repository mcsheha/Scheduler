package scheduler.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.AnchorPane;
import scheduler.model.AppointmentList;
import scheduler.model.DbConnection;

import java.sql.Connection;

public class AppointmentTabController {

    private static AppointmentTabController firstInstance = null;
    private AppointmentList appointmentList;
    private static Connection dbConnect;

    @FXML
    private ChoiceBox<String> choiceBox;

    @FXML
    private AnchorPane appointmentsTab;



    public void initialize(){
        choiceBox.getItems().addAll("Weekly", "Monthly");
        choiceBox.setValue("Weekly");
        dbConnect = DbConnection.getInstance().getConnection();

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



}
