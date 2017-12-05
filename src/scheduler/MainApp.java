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
import scheduler.model.Customer;
import scheduler.view.HomeScreenController;
import scheduler.view.LoginController;
import scheduler.model.DbConnection;
import scheduler.view.ModifyCustomerController;

import static java.lang.String.valueOf;


public class MainApp extends Application {

    private static MainApp firstInstance = null;
    private Stage primaryStage;
    private static DbConnection dbConnect = DbConnection.getInstance();
    private static LoginController loginController = new LoginController();
    private static HomeScreenController homeScreenController = new HomeScreenController();
    private static ModifyCustomerController modifyCustomerController = new ModifyCustomerController();
    private ResourceBundle loginBundle;
    private static SQLParser sqlParser = new SQLParser();
    private static String currentUsrName;


    public static MainApp getInstance() {
        if(firstInstance == null) {
            firstInstance = new MainApp();
        }
        return firstInstance;
    }


    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        dbConnect.connect();
        //Locale.setDefault(new Locale("fr", "FR"));
        System.out.println("Locale set to: " + Locale.getDefault());
        loginBundle = ResourceBundle.getBundle("scheduler/Bundle");
        dbConnect.printTable("user");
        showLoginScreen();
        bypassLogin();

    }


    public void stop(){
        dbConnect.disconnect();
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

            System.out.println("When showHomeScreen is called the currentUserName is: " + currentUserName);

            homeScreenController.setMainApp();
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


    public ModifyCustomerController showAddCustomerScreen(String currentUserName) throws IOException {

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
        controller.setCurrentUserName(currentUserName);
        controller.setModifyCustomerScreenStage(dialogStage);
        controller.setNewCustomer(true);
        controller.setCustomerIdField(valueOf(controller.findLowestAvailableID("customer")));

        controller.setTitleLabel("Add Customer");

        System.out.println("The currentUserName passed to showAddCustomerScreen of MainApp is: " + currentUserName);
        System.out.println("The currentUserName of the Add customer screen is: " + controller.getCurrentUserName());

        dialogStage.showAndWait();
        return controller;
    }


    public ModifyCustomerController showModifyCustomerScreen(String currentUserName, Customer customer) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(MainApp.class.getResource("view/ModifyCustomer.fxml"));
        VBox page = (VBox) loader.load();

        Stage dialogStage = new Stage();

        dialogStage.setTitle("Modify Customer");

        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        Scene scene = new Scene(page, 550, 500);
        dialogStage.setScene(scene);

        ModifyCustomerController controller = loader.getController();
        controller.setCurrentUserName(currentUserName);
        controller.setSelectedCustomer(customer);
        controller.setModifyCustomerScreenStage(dialogStage);
        controller.setNewCustomer(false);
        controller.setCustomerIdField(String.valueOf(customer.getCustomerId()));
        controller.setNameField(customer.getCustomerName());
        controller.setStreet1Field(customer.getStreet1());
        controller.setStreet2Field(customer.getStreet2());
        controller.setCityField(customer.getCity());
        controller.setCountryField(customer.getCountry());
        controller.setPostalCodeField(customer.getPostalCode());
        controller.setPhoneNumberField(customer.getPhone());


        controller.setTitleLabel("Modify Customer");

        System.out.println("The currentUserName passed to showAddCustomerScreen of MainApp is: " + currentUserName);
        System.out.println("The currentUserName of the Add customer screen is: " + controller.getCurrentUserName());

        dialogStage.showAndWait();
        return controller;
    }


    public static void main(String[] args) {
        launch(args);
    }


    public static DbConnection getDb() {
        return dbConnect;
    }

    public static SQLParser getSqlParser() {
        return sqlParser;
    }


    private void bypassLogin (){
        //this.currentUsrName = "michael";
        showHomeScreen("mike");
    }


}
