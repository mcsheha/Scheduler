package scheduler.controller;



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



