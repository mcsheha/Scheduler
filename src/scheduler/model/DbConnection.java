package scheduler.model;

import java.sql.*;
import java.util.Properties;

public class DbConnection {
    // init database constants
    private static final String DATABASE_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DATABASE_URL = "jdbc:mysql://52.206.157.109:3306/U03S4L";
    private static final String USERNAME = "U03S4L";
    private static final String PASSWORD = "53688066336";
    private static final String MAX_POOL = "150";

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    // init connection object
    private Connection connection;
    // init properties object
    private Properties properties;

    // create properties
    private Properties getProperties() {
        if (properties == null) {
            properties = new Properties();
            properties.setProperty("user", USERNAME);
            properties.setProperty("password", PASSWORD);
            properties.setProperty("MaxPooledStatements", MAX_POOL);
        }
        return properties;
    }

    // connect database
    public Connection connect() {
        if (connection == null) {
            try {
                Class.forName(DATABASE_DRIVER);
                connection = DriverManager.getConnection(DATABASE_URL, getProperties());
                System.out.println("Connected to DB");
            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
                System.out.println("Could not Connect to DB");
            }
        }
        return connection;
    }

    // disconnect database
    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Disconnected from DB");
                connection = null;
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Could not disconnect from DB");
            }
        }

    }

    public void sendSQL (String sql){
        try {
            PreparedStatement statement = this.getConnection().prepareStatement(sql);
            System.out.println("Sending: " + sql);
            // statement.execute();

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Unable to send " + sql + " check syntax.");
        }

    }


    public void printTable (String tableName)
            throws SQLException {

        Connection con = this.getConnection();
        Statement stmt = null;
        String query = "Select * FROM " + tableName;

        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();

            while (rs.next()) {
                for(int i = 1 ; i <= columnsNumber; i++){

                    System.out.print(rs.getString(i) + "\t"); //Print one element of a row

                }

                System.out.println();

            }
        } catch (SQLException e){
            System.out.println("unable to print table " + tableName);

        } finally {
            if (stmt != null) {stmt.close();}
        }
    }


}