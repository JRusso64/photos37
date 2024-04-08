package Controllers;
    
import java.util.Optional;
import java.util.List;


import Models.Admin;
import Models.User;
import Models.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;


/**
 * Controller for handling the login process in a photo album application.
 * This controller manages the login interactions and navigates to the appropriate user or admin views based on credentials.
 */
public class LoginController {
    
    private Admin admin; 

    /**
     * Initializes the controller. Sets up the Admin instance for user authentication.
     */
    public void initialize() {
        admin = Admin.getAdmin(); 

    }

    @FXML
    public TextField usernameField; 

    /**
     * Handles the action triggered by the login button.
     *
     * @param event The event that triggered this action
     */
    @FXML
    private void onLoginBtnClicked(ActionEvent event) {
        handleLoginAction(event);
    }

    @FXML
    private void handleEnterPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            ActionEvent actionEvent = new ActionEvent();
            onLoginBtnClicked(actionEvent);
        }
    }
    
    /**
     * Processes the login request. Directs the user to the appropriate screen based on the username provided.
     *
     * @param event The event that triggered the login action
     */
    @FXML
    private void handleLoginAction(ActionEvent event) {
        String username = usernameField.getText();

        Optional<User> userOpt = admin.getUserByUsername(username);
        //admin username
        if (username.equals("admin")) {
            // Load the admin screen FXML
            try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/MainAdmin.fxml"));
            Parent root = loader.load();

            // Get the current stage (Login screen stage)
            Stage stage = (Stage) usernameField.getScene().getWindow();

            // Set the scene to the admin screen
            Stage adminStage = new Stage();
            adminStage.setScene(new Scene(root));

            // Show the admin screen and close the login screen
            adminStage.setResizable(false);
            adminStage.setTitle("Admin System");
            adminStage.show();
            stage.close();
            } catch(Exception e)
            {
                e.printStackTrace();
            }


        } else if (userOpt.isPresent()){ //username check
            //load user screen
            try{

            //set current user for session
            List<User> copy = admin.getUsers();
            for (User u: copy)
            {      
                if (u.username.equals(username))
                {
                    UserSession.getInstance().setCurrentUser(u); 
                }
            }

 

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/UserMainScreen.fxml"));
            Parent root = loader.load();

            
            // Get the current stage (Login screen stage)
            Stage stage = (Stage) usernameField.getScene().getWindow();

            // Set the scene to the admin screen
            Stage adminStage = new Stage();
            adminStage.setScene(new Scene(root));

            // Show the admin screen and close the login screen
            adminStage.setTitle("Gallery View");
            adminStage.setResizable(false);
            adminStage.show();
            stage.close();
            } catch(Exception e)
            {
                e.printStackTrace();
            }
        } else {
            showAlert("Invalid Username", "The username entered does not exist. Please try again.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Displays an alert dialog to the user.
     *
     * @param title     The title of the alert
     * @param content   The content message of the alert
     * @param alertType The type of the alert
     */
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}

