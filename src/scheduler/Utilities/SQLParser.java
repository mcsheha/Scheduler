package scheduler.Utilities;

import scheduler.MainApp;

import javax.swing.plaf.nimbus.State;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;

public class SQLParser {

    private static MainApp mainApp;
    private static Connection db;

    // Used to send and receive rows from SQL tables;

    public static Statement prepareSQL (String stringToFormat)  {
        Statement statement = null;
        try {
            db = mainApp.getDb().getConnection();
            statement = db.prepareStatement(stringToFormat);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return statement;

    }
/*
    public static Statement prepareUserUpdate (int userId, String userName, String password, int active, String createdBy,
                                               String createDate, String lastUpdate, String lastUpdatedBy) {
        Statement preparedUserUpdate = null;
        String str = null;


        try{
            str = "INSERT INTO user (userId, userName, password, active, createBy, createDate, lastUpdate, lastUpdatedBy) VALUES (" +
                    userId + ", '" + userName + "', '" + password + "', " + active + ", '" + createdBy + "', '" + createDate +
                    "', '" + lastUpdate + "', '" + lastUpdatedBy + "');";

            //System.out.println("String to prepare: " + str);
            preparedUserUpdate = db.prepareStatement(str);

        } catch (SQLException e){
            e.printStackTrace();
        }
        return preparedUserUpdate;
    }
    */

    public static void executePreparedStatement (PreparedStatement stmt) {
        try {
            stmt.execute();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public static PreparedStatement prepareUserUpdate (int userId, String userName, String password, int active, String createdBy,
                                           String createDate, String lastUpdate, String lastUpdatedBy) {

        String sql = "INSERT INTO user (userId, userName, password, active, createBy, createDate, lastUpdate, lastUpdatedBy) " +
                "VALUES (?,?,?,?,?,?,?,?)";

        PreparedStatement psmt = null;

        try {
            db = mainApp.getDb().getConnection();
            psmt = db.prepareStatement(sql);
            psmt.setInt(1, userId);
            psmt.setString(2, userName);
            psmt.setString(3, password);
            psmt.setInt(4, active);
            psmt.setString(5, createdBy);
            psmt.setString(6, createDate);
            psmt.setString(7, lastUpdate);
            psmt.setString(8, lastUpdatedBy);


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return psmt;
    }


}
