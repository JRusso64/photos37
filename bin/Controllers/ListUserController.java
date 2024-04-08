package Controllers;
import java.util.stream.Collectors;

import Models.Admin;
import Models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.ListView;

import javafx.stage.Stage;

/**
 * Controller for the user list view in an admin panel of a photo album application.
 * This controller handles the display of all users in a list format.
 */
public class ListUserController {
   
    @FXML
    private ListView<String> userTable; 

    private Admin admin; 

    /**
     * Initializes the controller.
     * Creates the Admin instance and populates the ListView with usernames.
     */
    public void initialize() {
        admin = Admin.getAdmin(); 
        updateListView();
    }

    /**
     * Updates the ListView with the latest list of usernames.
     * Fetches usernames from the Admin instance and displays them in the ListView.
     */
    private void updateListView() {
        userTable.getItems().setAll(
            admin.getUsers().stream()
                 .map(User::getUsername)
                 .collect(Collectors.toList())
        );
    }

    /**
     * Handles returning to the home screen
     * Closes the window and opens the admin screen.
     *
     * @param event The event that triggered this action.
     */
    @FXML
    private void onHomeBtnClicked(ActionEvent event) {
        try {
            //load admin screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/MainAdmin.fxml"));
            Parent root = loader.load();
    
            //creates new stage and scene
            Stage mainStage = new Stage();
            mainStage.setScene(new Scene(root));
    
            //displays admin screen
            mainStage.show();
    
            //closes the current window
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.close();
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
}
