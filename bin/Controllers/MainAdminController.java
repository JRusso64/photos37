package Controllers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import Models.Admin;
import Models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * Controller for managing administrative functionalities in a photo album application.
 * This controller handles operations such as adding and deleting users, as well as navigating to other admin screens.
 */
public class MainAdminController {

    @FXML
    private AnchorPane mainAdminPane;

    public Admin admin; 

    /**
     * Initializes the controller. Sets up the Admin instance for managing users.
     */
    public void initialize() {
        this.admin = Admin.getAdmin(); 
    }

    /**
     * Handles the action to add a new user to the system.
     *
     * @param event The event that triggered this action
     */
    @FXML
    private void onAddUserClicked(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add New User");
        dialog.setHeaderText("Create a New User");
        dialog.setContentText("Enter username:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(username -> {
        // Validate the input and then add the user
        String trimmedUsername = username.trim();

         // Check if the username already exists
         if (admin.getUserByUsername(trimmedUsername).isPresent()) {
            showAlert("User Exists", "User " + trimmedUsername + " already exists. Please use a different username.", Alert.AlertType.ERROR);
            return;
        }

        if (!username.trim().isEmpty()) {
            admin.addUser(username);
            admin.saveUsersToFile("src/User-Data/users.dat");
            // Inform the admin that the user has been added
            showAlert("User Added", "User " + username + " has been successfully added.", Alert.AlertType.INFORMATION);
        } else {
            showAlert("Invalid Input", "Username cannot be empty.", Alert.AlertType.ERROR);
        }
        });
    }

    /**
     * Handles the action to delete an existing user from the system.
     *
     * @param event The event that triggered this action
     */
    @FXML
    private void onDeleteUserClicked(ActionEvent event) {
        List<User> users = admin.getUsers(); // Method to get all User objects
        if (users.isEmpty()) {
            showAlert("No Users", "There are no users to delete.", Alert.AlertType.INFORMATION);
            return;
        }

        List<String> usernames = users.stream().map(User::getUsername).collect(Collectors.toList());
        ChoiceDialog<String> dialog = new ChoiceDialog<>(null, usernames);
        dialog.setTitle("Delete User");
        dialog.setHeaderText("Select a User to Delete");
        dialog.setContentText("Choose user:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(username -> {
            admin.deleteUser(username);
            admin.saveUsersToFile("src/User-Data/users.dat");
            showAlert("User Deleted", "User " + username + " has been successfully deleted.", Alert.AlertType.INFORMATION);
        });
     }

     /**
     * Logs out the current admin and returns to the login screen.
     *
     * @param event The event that triggered the logout
     */
    @FXML
    private void onLogOutClicked(ActionEvent event) {
        // Load the Delete User screen
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Views/LoginView.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1));  
            stage.show();
            stage.setTitle("Photo Storage Application");
            // Close the current (Main Screen) window
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    
    }

     /**
     * Opens the user listing screen.
     *
     * @param event The event that triggered this action
     */
    @FXML
    private void onListUsersClicked(ActionEvent event) {
        // Load the Delete User screen
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Views/ListUser.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1));  
            stage.show();
            stage.setResizable(false);
            stage.setTitle("List of Users");
            // Close the current (Main Screen) window
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch(Exception e) {
            e.printStackTrace();
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
