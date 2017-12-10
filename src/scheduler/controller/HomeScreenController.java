package scheduler.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import scheduler.MainApp;
import scheduler.model.*;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static java.time.ZoneOffset.UTC;


public class HomeScreenController {

    private static HomeScreenController firstInstance = null;
    private static String currentUserName;


    public void initialize(){
    }


    public static HomeScreenController getInstance() {
        if (firstInstance == null) {
            firstInstance = new HomeScreenController();
        }
        return firstInstance;
    }


    public void setCurrentUserName(String currentUserName) {
    }

    public static String getCurrentUserName() {
        return currentUserName;
    }
}



