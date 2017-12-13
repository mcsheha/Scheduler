package scheduler.controller;


import java.time.LocalDateTime;

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

    //returns current date and time in UTC as a string.
    public static String nowUtcAsString() {

        LocalDateTime currentDateTime = LocalDateTime.now(UTC);
        return currentDateTime.toString().replace("T", " ").substring(0,21);

    }

    public static String dateTimeAsString (LocalDateTime dateTime) {
        return dateTime.toString().replace("T", " ");
    }
}



