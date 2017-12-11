package scheduler.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AppointmentList {

    private static AppointmentList firstInstance = null;
    private ArrayList<Appointment> appointmentArrayList = new ArrayList<>();
    public static Connection dbConnect;





    public static AppointmentList getInstance() {
        if (firstInstance == null) {
            firstInstance = new AppointmentList();
        }
        return firstInstance;
    }

    public AppointmentList () {
        dbConnect = DbConnection.getInstance().getConnection();

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

                appointmentArrayList.add(new Appointment(appointmentId, customerId, title, description, location,
                        contact, url, start, end, createDate, createdBy, lastUpdate, lastUpdateBy));

            }
        }
        catch (SQLException e) {
            e.printStackTrace();
        }



    }

    public ArrayList<Appointment> getAppointmentArrayList() {
        return appointmentArrayList;
    }
}
