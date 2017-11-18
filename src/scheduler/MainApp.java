package scheduler;

import com.sun.org.apache.xpath.internal.SourceTree;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import scheduler.Utilities.SQLParser;
import scheduler.view.HomeScreenController;
import scheduler.view.LoginController;
import scheduler.model.DbConnection;
import scheduler.view.ModifyCustomerController;


public class MainApp extends Application {

    private Stage primaryStage;
    private static DbConnection db = new DbConnection();
    private static LoginController loginController = new LoginController();
    private static HomeScreenController homeScreenController = new HomeScreenController();
    private static ModifyCustomerController modifyCustomerController = new ModifyCustomerController();
    private ResourceBundle loginBundle;
    private static SQLParser sqlParser = new SQLParser();
    private static String currentUsrName;


    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        db.connect();
        //Locale.setDefault(new Locale("fr", "FR"));
        System.out.println("Locale set to: " + Locale.getDefault());
        loginBundle = ResourceBundle.getBundle("scheduler/Bundle");
        db.printTable("user");
        showLoginScreen();
        bypassLogin();
        System.out.println(sqlParser.checkIfInTable("dog", "userName", "user"));
        System.out.println(sqlParser.nowUtcAsString());

        //sqlParser.prepareUserUpdate(111,"admin","admin", 1, "mike",
        //        "2016-11-02 01:01:01", "2016-11-02 01:01:01", "mike");
        System.out.println("This lowest available customerId is: " + sqlParser.findLowestAvailableID("customer"));

    }


    public void stop(){
        db.disconnect();
    }


    private void showLoginScreen() {

        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("view/Login.fxml"),loginBundle);
            loginController.setMainApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        primaryStage.setTitle(loginBundle.getString("scheduler"));
        primaryStage.setScene(new Scene(root, 500, 250));
        primaryStage.show();
    }


    public void showHomeScreen(String currentUserName) {

        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("view/HomeScreen.fxml"));

            //HomeScreenController homeScreenController = new HomeScreenController(currentUserName);
            System.out.println("When showHomeScreen is called the currentUserName is: " + currentUserName);

            homeScreenController.setMainApp(this);
            homeScreenController.setCurrentUserName(currentUserName);
            System.out.println("homeScreenController.getCurrentUserName() is: " + homeScreenController.getCurrentUserName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        primaryStage.setTitle("Scheduler");
        primaryStage.setScene(new Scene(root,900, 750));
        primaryStage.show();
        System.out.println(currentUsrName);
    }


    public void showAddCustomerScreen(String currentUserName) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("view/ModifyCustomer.fxml"));
        VBox page = (VBox) loader.load();

        Stage dialogStage = new Stage();
        dialogStage.setTitle("Add Customer");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        Scene scene = new Scene(page, 550, 500);
        dialogStage.setScene(scene);

        ModifyCustomerController controller = loader.getController();
        controller.setMainApp(this);
        controller.setCurrentUserName(currentUserName);
        controller.setSqlParser(getSqlParser(), currentUserName);
        controller.setModifyCustomerScreenStage(dialogStage);
        controller.setTitleLabel("Add Customer");
        System.out.println("The currentUserName passed to showAddCustomerScreen of MainApp is: " + currentUserName);
        System.out.println("The currentUserName of the Add customer screen is: " + controller.getCurrentUserName());

        dialogStage.showAndWait();
    }


    public static void main(String[] args) {
        launch(args);
    }


    public static DbConnection getDb() {
        return db;
    }

    public static SQLParser getSqlParser() {
        return sqlParser;
    }


    private void bypassLogin (){
        //this.currentUsrName = "michael";
        showHomeScreen("mike");
    }


}
