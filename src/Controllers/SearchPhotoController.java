package Controllers;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import Models.Admin;
import Models.Album;
import Models.StorePhoto;
import Models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.ListView;

/**
 * Controller for the photo search result screen in a photo storage application.
 * This controller manages the display and interaction with the search results of photos.
 */
public class SearchPhotoController {
   
    @FXML
    private ListView<Node> PhotoListView;
    
    @FXML
    private Button CreateAlbumBtn, GalleryViewBtn;
    
    // Other FXML elements if necessary
    public Admin admin; // Admin instance variable
    private User currentUser; // This should be set when the user logs in
    List<StorePhoto> matchingPhotos;
    private StorePhoto selectedPhoto;
    
    
    /**
     * Called to pass the search results to the controller.
     *
     * @param photos List of photos that match the search criteria
     * @param user The currently logged-in user
     */
    public void setMatchingPhotos(List<StorePhoto> photos,  User user) {     
        matchingPhotos = photos;
        currentUser = user;
        this.admin = Admin.getAdmin();
        loadPhotos();
    }

    /**
     * Loads the matching photos into the ListView for display.
     */
    private void loadPhotos() {
        PhotoListView.getItems().clear(); // Clear the ListView

        if (matchingPhotos != null && !matchingPhotos.isEmpty()) {
            for (StorePhoto photo : matchingPhotos) {
                Node photoView = createPhotoViewWithCaption(photo);
                PhotoListView.getItems().add(photoView); // Add the VBox to the ListView
            }
        } else {
            PhotoListView.setPlaceholder(new Label("No Photos Available")); // Set placeholder for empty list
        }
    }

    /**
     * Creates a Node that contains a photo view with its caption for display in the ListView.
     *
     * @param photo The photo to be displayed
     * @return A Node containing the photo view and caption
     */
    private Node createPhotoViewWithCaption(StorePhoto photo) {
        
        ImageView imageView = new ImageView(new Image("file:" + photo.getPath()));
        imageView.setFitHeight(100); 
        imageView.setPreserveRatio(true);
        
        Label captionLabel = new Label(photo.getCaption());
        captionLabel.setWrapText(true);

        VBox container = new VBox(5, imageView, captionLabel); 
        container.setAlignment(Pos.CENTER); 
        container.setPadding(new Insets(10)); 
        
        container.setOnMouseClicked(event -> {
            PhotoListView.getSelectionModel().clearSelection();
            PhotoListView.getSelectionModel().select(container);
            selectedPhoto = photo; 
        });

        return container;
    }

    /**
     * Handles the action of creating an album from the search results.
     */
    @FXML
    private void onCreateAlbumBtnClicked ()
    {
            if (matchingPhotos == null || matchingPhotos.isEmpty()) {
        showAlert("No Photos", "There are no photos to add to a new album.");
        return;
    }

    // Prompt user for the new album name
    TextInputDialog dialog = new TextInputDialog();
    dialog.setTitle("Create Album from Search Results");
    dialog.setHeaderText("Enter a name for the new album:");
    dialog.setContentText("Album Name:");

    Optional<String> result = dialog.showAndWait();

    
    result.ifPresent(albumName -> {
        String trimmedAlbumName = albumName.trim();
        // Check if the album name is empty
        if (trimmedAlbumName.isEmpty()) {
            showAlert("Invalid Input", "Album name cannot be empty.", Alert.AlertType.ERROR);
            return;
        }

        // Check if an album with this name already exists
        if (currentUser.getAlbumByName(trimmedAlbumName) != null) {
            showAlert("Album Exists", "An album with the name '" + trimmedAlbumName + "' already exists. Please choose a different name.", Alert.AlertType.ERROR);
            return;
        }
        if (!albumName.trim().isEmpty()) {
            // Create a new album with the same photos
            Album newAlbum = new Album(albumName);
            for (StorePhoto photo : matchingPhotos) {
                newAlbum.addPhoto(photo);
            }

            // Add the new album to the user's list of albums
            currentUser.albums.add(newAlbum);
            admin.saveUsersToFile("src/User-Data/users.dat");
            showAlert("Album Created", "A new album has been created with the search results.");

            

            // Open the gallery view
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/UserMainScreen.fxml")); 
                Parent galleryRoot = loader.load();

                Scene galleryScene = new Scene(galleryRoot);
                Stage galleryStage = new Stage();
                galleryStage.setTitle("Gallery");
                galleryStage.setScene(galleryScene);

                Stage currentStage = (Stage) PhotoListView.getScene().getWindow(); 
                currentStage.close();

                galleryStage.show();
            } catch (IOException e) {
                e.printStackTrace();
                
            }
        } else {
            showAlert("Invalid Name", "Please enter a valid name for the album.");
        }
    });
    }

     /**
     * Navigates back to the gallery view when the user clicks the corresponding button.
     *
     * @param event The action event triggered by clicking the button
     */
    @FXML
    private void onGalleryViewBtnClicked (ActionEvent event)
    {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Views/UserMainScreen.fxml"));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root1));  
            stage.show();
    
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
     * @param title The title of the alert
     * @param content The content message of the alert
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Displays an alert dialog to the user with a specific alert type.
     *
     * @param title The title of the alert
     * @param content The content message of the alert
     * @param alertType The type of alert to display
     */
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
