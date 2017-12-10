package scheduler.model;

public class AppointmentList {

    private static AppointmentList firstInstance = null;






    public static AppointmentList getInstance() {
        if (firstInstance == null) {
            firstInstance = new AppointmentList();
        }
        return firstInstance;
    }
}
