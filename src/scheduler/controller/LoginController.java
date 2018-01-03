package scheduler.controller;

import javafx.fxml.FXML;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Objects;
import java.util.ResourceBundle;



import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import scheduler.MainApp;
import sun.applet.Main;

public class LoginController {

    @FXML
    private Button submit;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;



    public TextField getUsernameField() {
        return usernameField;
    }

    public TextField getPasswordField() {
        return passwordField;
    }

    private static MainApp mainApp;



    @FXML
    void forgotPasswordClicked() {

        ResourceBundle loginBundle = ResourceBundle.getBundle("scheduler/Bundle");


        Alert alert = new Alert(AlertType.INFORMATION);

        alert.setTitle(loginBundle.getString("forgot"));
        alert.setHeaderText(loginBundle.getString("forgotPword"));
        alert.setContentText(loginBundle.getString("contactHelp"));

        Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setText(loginBundle.getString("ok"));

        alert.showAndWait();
    }



    public void incorrectPassword (){
        ResourceBundle loginBundle = ResourceBundle.getBundle("scheduler/Bundle");


        Alert alert = new Alert(AlertType.ERROR);

        alert.setTitle(loginBundle.getString("error"));
        alert.setHeaderText(loginBundle.getString("incorrect"));

        Button okButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setText(loginBundle.getString("ok"));

        alert.showAndWait();
    }

    @FXML
    public void loginToSystem (){

        String parsedUsername = usernameField.getText();
        String parsedPassword = passwordField.getText();

        Connection db = MainApp.getDb().getConnection();

        String queryString = "SELECT password FROM user where userName = " + "'" + parsedUsername + "';";

        String storedPassword = null;
        try {
            Statement statement = db.prepareStatement(queryString);
            statement.executeQuery(queryString);
            ResultSet rs = statement.getResultSet();

            while(rs.next()){
                storedPassword = rs.getString("password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // The passwords match
        if(Objects.equals(parsedPassword, storedPassword)){
            mainApp.setCurrentUserName(parsedUsername);
            mainApp.showHomeScreen();
            writeToLog();

        // The passwords do not match
        } else {
            incorrectPassword();
        }


    }


    private void writeToLog () {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formatUtcTime = LocalDateTime.now(Clock.systemUTC()).format(formatter);

        try {
            final Path path = Paths.get("src/scheduler/LoginLog.txt");
            Files.write(path, Arrays.asList(String.format("%-7s%-10.9s%-16.16s%-19s%-7s","User: ", usernameField.getText(),
                    " logged in at: ", formatUtcTime, " [UTC]")), StandardCharsets.UTF_8,
                    Files.exists(path) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public void setMainApp (MainApp mainApp) {
        this.mainApp = mainApp;
    }

}
