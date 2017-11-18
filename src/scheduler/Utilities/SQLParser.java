package scheduler.Utilities;

import scheduler.MainApp;
import sun.applet.Main;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import static java.time.ZoneOffset.UTC;

public class SQLParser {

    private static MainApp mainApp;
    private static Connection db;
    private static String currentUserName;

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
            psmt.executeUpdate();


        } catch (SQLException e) {
            e.printStackTrace();
        }

        return psmt;
    }
/*
    public boolean checkIfInTable(String searchTerm, String columnName, String tableName) {
        boolean rtn = false;
        Connection dBase = mainApp.getDb().getConnection();

        String queryString = "SELECT " + columnName + " FROM " + tableName;

        try {
            Statement statement = dBase.prepareStatement(queryString);
            statement.executeQuery(queryString);
            ResultSet rs = statement.getResultSet();

            while(rs.next()){
                String tempString = rs.getString(columnName);
                if (Objects.equals(tempString,searchTerm)){
                    rtn = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rtn;
    }*/
/*

        // Check if the specified country is already in the table and returns the country ID.
        // If not already in table calls addCountry and returns new country ID.
    public int getCountryId(String country) {
        int countryId = 0;
        Connection dBase = mainApp.getDb().getConnection();
        String queryString = "SELECT countryId FROM country where country = " + "'" + country + "';";

        int storedCountryId = 0;
        if (checkIfInTable(country, "country", "country")) {
            try {
                Statement statement = dBase.prepareStatement(queryString);
                ResultSet rs = statement.getResultSet();

                while (rs.next()) {
                    countryId = rs.getInt("countryId");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            countryId = addCountry(country);
        }

        return countryId;
    }


    //Adds new country to the DB
    private int addCountry(String countryToAdd) {
        int countryId = findLowestAvailableID("country");
        String currentUser = currentUserName;
        //String currentUser = "mike";


        String sql = "INSERT INTO country (countryId, country, createDate, createdBy, lastUpdate, lastUpdateBy) " +
                "VALUES (?,?,?,?,?,?)";

        PreparedStatement psmt = null;

        try {
            Connection dBase = mainApp.getDb().getConnection();
            psmt = dBase.prepareStatement(sql);
            psmt.setInt(1, countryId);
            psmt.setString(2, countryToAdd);
            psmt.setString(3, nowUtcAsString());
            psmt.setString(4, currentUser);
            psmt.setString(5, nowUtcAsString());
            psmt.setString(6, currentUser);
            psmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
       return countryId;
    }
*/

/*

    //returns current date and time in UTC as a string.
    public String nowUtcAsString() {

        LocalDateTime currentDateTime = LocalDateTime.now(UTC);
        return currentDateTime.toString().replace("T", " ").substring(0,21);

    }

    //returns current date and time in UTC as a string.
    public String nowLocalAsString () {

        LocalDateTime currentDateTime = LocalDateTime.now();
        return currentDateTime.toString().replace("T", " ").substring(0,21);

    }
*/

/*
    public int findLowestAvailableID (String tableName) {
        Connection dBase = MainApp.getDb().getConnection();

        String columnName = tableName + "Id";
        String queryString = "SELECT " + columnName + " FROM " + tableName +";";
        int rtnInt = 1;
        ArrayList<Integer> arrayList = new ArrayList<>();

        try {
            Statement statement = dBase.prepareStatement(queryString);
            statement.executeQuery(queryString);
            ResultSet rs = statement.getResultSet();

            while(rs.next()){
                arrayList.add(rs.getInt(columnName));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Collections.sort(arrayList);

        while (arrayList.contains(rtnInt)) {
            rtnInt++;
        }
        return rtnInt;


    }

    // Check if the specified city is already in the table and returns the city ID.
    // If not already in table calls addCity and returns new city ID.
    public int getCityId(String city, int countryId) {
        int cityId = 0;
        Connection dBase = mainApp.getDb().getConnection();
        String queryString = "SELECT cityId FROM city where city = " + "'" + city + "';";

        int storedCityId = 0;
        if (checkIfInTable(city, "city", "city")) {
            try {
                Statement statement = dBase.prepareStatement(queryString);
                ResultSet rs = statement.getResultSet();

                while (rs.next()) {
                    cityId = rs.getInt("cityId");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            cityId = addCity(city, countryId);
        }

        return cityId;
    }


    //Adds new city to the DB
    private int addCity(String cityToAdd, int countryId) {
        int cityId = findLowestAvailableID("city");
        String currentUser = currentUserName;
        //String currentUser = "mike";

        String sql = "INSERT INTO city (cityId, city, countryId, createDate, createdBy, lastUpdate, lastUpdateBy) " +
                "VALUES (?,?,?,?,?,?,?)";

        PreparedStatement psmt = null;

        try {
            Connection dBase = mainApp.getDb().getConnection();
            psmt = dBase.prepareStatement(sql);
            psmt.setInt(1, cityId);
            psmt.setString(2, cityToAdd);
            psmt.setInt(3, countryId);
            psmt.setString(4, nowUtcAsString());
            psmt.setString(5, currentUser);
            psmt.setString(6, nowUtcAsString());
            psmt.setString(7, currentUser);
            psmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cityId;
    }


    // Check if the specified address is already in the table and returns the address ID.
    // If not already in table calls addAddress and returns new address ID.
    public int getAddressId(String address, String address2, int cityId, String postalCode, String phone) {
        int addressId = 0;
        Connection dBase = mainApp.getDb().getConnection();
        String queryString = "SELECT addressId FROM address where address = " + "'" + address + "';";

        int storedAddressId = 0;
        if (checkIfInTable(address, "address", "address")) {
            try {
                Statement statement = dBase.prepareStatement(queryString);
                ResultSet rs = statement.getResultSet();

                while (rs.next()) {
                    addressId = rs.getInt("addressId");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            addressId = addAddress(address, address2, cityId, postalCode, phone);
        }

        return addressId;
    }


    //Adds new address to the DB
    private int addAddress(String addressToAdd, String address2ToAdd, int cityId, String postalCode, String phone) {
        int addressId = findLowestAvailableID("address");
        String currentUser = currentUserName;
        //String currentUser = "mike";

        String sql = "INSERT INTO address (addressId, address, address2, cityId, postalCode, phone, createDate, " +
                "createdBy, lastUpdate, lastUpdateBy) VALUES (?,?,?,?,?,?,?,?,?,?)";

        PreparedStatement psmt = null;

        try {
            Connection dBase = mainApp.getDb().getConnection();
            psmt = dBase.prepareStatement(sql);
            psmt.setInt(1, addressId);
            psmt.setString(2, addressToAdd);
            psmt.setString(3, address2ToAdd);
            psmt.setInt(4, cityId);
            psmt.setString(5, postalCode);
            psmt.setString(6, phone);
            psmt.setString(7, nowUtcAsString());
            psmt.setString(8, currentUser);
            psmt.setString(9, nowUtcAsString());
            psmt.setString(10, currentUser);
            psmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return addressId;
    }*/

    public static String getCurrentUserName() {
        return currentUserName;
    }

    public static void setCurrentUserName(String currentUserName) {
        SQLParser.currentUserName = currentUserName;
    }
}

