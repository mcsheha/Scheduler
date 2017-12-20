package scheduler.controller;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

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


    public static String dateTimeAsString (LocalDateTime dateTime) {
        return dateTime.toString().replace("T", " ");
    }
}



