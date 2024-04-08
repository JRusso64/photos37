package Controllers;

import Models.Admin;
import Models.Album;
import Models.StorePhoto;
import Models.User;
import Models.Tag;

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
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import java.util.List;

import java.io.File;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import javafx.scene.control.ListView;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Controller for the photo screen view in a photo album application.
 * This controller handles the display and management of photos within an album.
 */
public class PhotoScreenController {  
    
    @FXML
    private ListView<Node> PhotoListView;

    @FXML
    private Button addPhotoButton, deletePhotoButton, addTagButton, deleteTagButton,
                   captionPhotoButton, copyPhotoButton, movePhotoButton,
                   displayPhotoButton, slideshowButton, exitAppButton, logoutButton;

    // Initial setup method
    public Admin admin; 
    private Album selectedAlbum;
    private User currentUser;
    private StorePhoto selectedPhoto;
    
    /**
     * Initializes the controller and sets up the list view for photo display.
     */
    @FXML
    public void initialize() {
        this.admin = Admin.getAdmin(); 
        setupListView();
        loadPhotos();

    }
    
    /**
     * Sets the current user of the application.
     *
     * @param user The current user
     */
    @FXML
    public void setCurrentUser(User user)
    {
        this.currentUser = user;
    }

    /**
     * Sets the selected album for displaying photos.
     *
     * @param album The album selected by the user
     */
    @FXML
    public void setSelectedAlbum(Album album) {
        for (Album userAlbum : currentUser.getAlbums()) {
            if (userAlbum.equals(album)) { 
                this.selectedAlbum = userAlbum;
                loadPhotos();

                return;
            }
        }
      
    }
    
    /**
     * does logic for when the add photo button is clicked
     */
    @FXML
    private void onAddPhotoBtnClicked() {


        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter imageFilter = new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png");
        fileChooser.getExtensionFilters().add(imageFilter);

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                LocalDateTime photoDate = Instant.ofEpochMilli(file.lastModified())
                                                .atZone(ZoneId.systemDefault())
                                                .toLocalDateTime()
                                                .withNano(0);

                // Check if the photo already exists in the album
                String newPhotoPath = file.getAbsolutePath();
                boolean isDuplicate = selectedAlbum.getPhotos().stream()
                                        .anyMatch(photo -> photo.getPath().equals(newPhotoPath));

                if (isDuplicate) {
                    // Show error alert for duplicate photo
                    showAlert("Duplicate Photo", "This photo already exists in the album.", Alert.AlertType.ERROR);
                } else {
                    // Add the new photo
                    StorePhoto photo = new StorePhoto(newPhotoPath, photoDate);
                    selectedAlbum.addPhoto(photo);
                    admin.saveUsersToFile("src/User-Data/users.dat");

                    loadPhotos(); 
                }

            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Cannot Add Photo");
                alert.setContentText("An error occurred while trying to add the photo.");
                alert.showAndWait();
            }}

    }

    /**
     * does logic for when the delete  photo button is clicked
     */
    @FXML
    private void onDeletePhotoBtnClicked() {
        // Confirm and delete selected photo from grid
        if (selectedPhoto == null) {
        Alert noSelectionAlert = new Alert(Alert.AlertType.INFORMATION);
        noSelectionAlert.setTitle("No Photo Selected");
        noSelectionAlert.setHeaderText(null);
        noSelectionAlert.setContentText("Please select a photo to delete.");
        noSelectionAlert.showAndWait();
        return;
    }

    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
    alert.setTitle("Delete Photo");
    alert.setHeaderText("Delete Photo");
    alert.setContentText("Are you sure you want to delete this photo?");

    Optional<ButtonType> result = alert.showAndWait();
    if (result.isPresent() && result.get() == ButtonType.OK) {
        selectedAlbum.getPhotos().remove(selectedPhoto);
        admin.saveUsersToFile("src/User-Data/users.dat"); 
        loadPhotos(); 
    }
    }

    /**
     * does logic for when the add tag button is clicked
     */
    @FXML
    private void onAddTagBtnClicked() {
        if (selectedPhoto == null) {
            showAlert("No Photo Selected", "Please select a photo to add a tag.", Alert.AlertType.INFORMATION);
            return;
        }
    
        // Create the custom dialog
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Add Tag");
        dialog.setHeaderText("Add a tag to the selected photo");
    
        // Set the button types
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
    
        // Create the tag name and value fields
        Set<String> availableTags = currentUser.getAllTags();
        availableTags.add("Person");
        availableTags.add("Location");
        availableTags.add("custom");

        ComboBox<String> tagNameComboBox = new ComboBox<>();
        tagNameComboBox.getItems().addAll(availableTags); 
        TextField tagNameField = new TextField();
        TextField tagValueField = new TextField();
    
        // Layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("Tag Name:"), 0, 0);
        grid.add(tagNameComboBox, 1, 0);
        grid.add(new Label("Tag Value:"), 0, 1);
        grid.add(tagValueField, 1, 1, 2, 1);
    
        // Add listener to ComboBox for custom tag name input
        tagNameComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if ("custom".equals(newVal)) {
                grid.add(tagNameField, 2, 0); 
            } else {
                grid.getChildren().remove(tagNameField); 
            }
        });
    
        dialog.getDialogPane().setContent(grid);
    
        // Process dialog result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                String tagName = "custom".equals(tagNameComboBox.getValue()) ? tagNameField.getText() : tagNameComboBox.getValue();
                return new Pair<>(tagName, tagValueField.getText());
            }
            return null;
        });
    
        // Show the dialog and process the result
        Optional<Pair<String, String>> result = dialog.showAndWait();
        result.ifPresent(tag -> {
            String tagName = tag.getKey();
            String tagValue = tag.getValue();
    
            if ((tagName == null || tagName.isEmpty()) || tagValue.isEmpty()) {
                showAlert("Invalid Input", "Tag name and value cannot be empty.", Alert.AlertType.ERROR);
                return;
            }
    
            Tag newTag = new Tag(tagName, tagValue);
            selectedPhoto.addTag(newTag);
            admin.saveUsersToFile("src/User-Data/users.dat");
            
            
        });
    }
    
    /**
     * does logic for when the delete tag button is clicked
     */
    @FXML
    private void onDeleteTagBtnClicked() {
        // Delete a tag from the selected photo
        if (selectedPhoto == null) {
        showAlert("No Photo Selected", "Please select a photo to delete its tag.", Alert.AlertType.INFORMATION);
        return;
        }


        // If there are no tags, there's nothing to delete
        if (selectedPhoto.getTags().isEmpty()) {
            showAlert("No Tags to Delete", "The selected photo has no tags to delete.", Alert.AlertType.INFORMATION);
            return;
        }

        // Create a choice dialog to let the user select which tag to delete
        ChoiceDialog<Tag> dialog = new ChoiceDialog<>(null, selectedPhoto.getTags());
        dialog.setTitle("Delete Tag");
        dialog.setHeaderText("Select a tag to delete from the photo");

        // Set the content for the dialog
        dialog.setContentText("Choose tag:");

        // Show the dialog and wait for the user's choice
        Optional<Tag> result = dialog.showAndWait();

        // If a tag is selected, remove it from the photo
        result.ifPresent(selectedTag -> {
            selectedPhoto.removeTags(selectedTag);
            admin.saveUsersToFile("src/User-Data/users.dat"); 
            
        });
    }

    /**
     * does logic for when the caption photo button is clicked
     */
    @FXML
    private void onCaptionPhotoBtnClicked() {
       if (selectedPhoto == null) {
        Alert noSelectionAlert = new Alert(Alert.AlertType.INFORMATION);
        noSelectionAlert.setTitle("No Photo Selected");
        noSelectionAlert.setHeaderText(null);
        noSelectionAlert.setContentText("Please select a photo to set a caption.");
        noSelectionAlert.showAndWait();
        return;
    }

    TextInputDialog dialog = new TextInputDialog(selectedPhoto.getCaption());
    dialog.setTitle("Set Photo Caption");
    dialog.setHeaderText("Enter a caption for the selected photo:");
    dialog.setContentText("Caption:");

    Optional<String> result = dialog.showAndWait();
    result.ifPresent(caption -> {
        selectedPhoto.setCaption(caption);
        admin.saveUsersToFile("src/User-Data/users.dat"); 
        loadPhotos(); 
    });
    }

    /**
     * does logic for when the copy photo button is clicked
     */
    @FXML
    private void onCopyPhotoBtnClicked() {
        // Copy selected photo to another album
        if (selectedPhoto == null) {
        showAlert("No Photo Selected", "Please select a photo to copy.", Alert.AlertType.INFORMATION);
        return;
        }

        // Get a list of all albums except the current one
        List<Album> otherAlbums = currentUser.getAlbums().stream()
                                            .filter(album -> !album.equals(selectedAlbum))
                                            .collect(Collectors.toList());

        if (otherAlbums.isEmpty()) {
            showAlert("No Other Albums", "There are no other albums to copy the photo to.", Alert.AlertType.INFORMATION);
            return;
        }

        // Create a choice dialog to let the user select which album to copy the photo to
        ChoiceDialog<Album> dialog = new ChoiceDialog<>(null, otherAlbums);
        dialog.setTitle("Copy Photo");
        dialog.setHeaderText("Select an album to copy the photo to");
        dialog.setContentText("Choose album:");

        // Show the dialog and wait for the user's choice
        Optional<Album> result = dialog.showAndWait();

        // If an album is selected, copy the photo to that album
        result.ifPresent(targetAlbum -> {

            boolean photoExists = targetAlbum.getPhotos().stream()
                                     .anyMatch(photo -> photo.getPath().equals(selectedPhoto.getPath()));

            if (photoExists) {
                showAlert("Photo Exists", "This photo already exists in the target album.", Alert.AlertType.WARNING);
            } else {
                targetAlbum.addPhoto(selectedPhoto);
                admin.saveUsersToFile("src/User-Data/users.dat"); 
                showAlert("Photo Copied", "The photo was successfully copied to the target album.", Alert.AlertType.INFORMATION);
            }
            
        });
    }

    /**
     * does logic for when the move photo button is clicked
     */
    @FXML
    private void onMovePhotoBtnClicked() {
        // Move selected photo to another album
        if (selectedPhoto == null) {
            showAlert("No Photo Selected", "Please select a photo to move.", Alert.AlertType.INFORMATION);
            return;
        }
    
        // Get a list of all albums except the current one (source)
        List<Album> otherAlbums = currentUser.getAlbums().stream()
                                             .filter(album -> !album.equals(selectedAlbum))
                                             .collect(Collectors.toList());
    
        if (otherAlbums.isEmpty()) {
            showAlert("No Other Albums", "There are no other albums to move the photo to.", Alert.AlertType.INFORMATION);
            return;
        }
    
        // Create a choice dialog to let the user select which album to move the photo to
        ChoiceDialog<Album> dialog = new ChoiceDialog<>(null, otherAlbums);
        dialog.setTitle("Move Photo");
        dialog.setHeaderText("Select an album to move the photo to");
        dialog.setContentText("Choose album:");
    
        // Show the dialog and wait for the user's choice
        Optional<Album> result = dialog.showAndWait();
    
        // If an album is selected, move the photo to that album
        result.ifPresent(targetAlbum -> {

            boolean photoExists = targetAlbum.getPhotos().stream()
                                     .anyMatch(photo -> photo.getPath().equals(selectedPhoto.getPath()));

            if (photoExists) {
                showAlert("Photo Exists", "This photo already exists in the target album.", Alert.AlertType.WARNING);
            } else {
                 targetAlbum.addPhoto(selectedPhoto);
            // Remove the photo from the current (source) album
            selectedAlbum.removePhoto(selectedPhoto);
            admin.saveUsersToFile("src/User-Data/users.dat"); // Save the changes

            showAlert("Photo moved", "The photo was successfully moved to the target album.", Alert.AlertType.INFORMATION);
            }

           
        });

        loadPhotos();
    }

    /**
     * does logic for when the display photo button is clicked
     */
    @FXML
    private void onDisplayPhotoBtnClicked() {
        // Display photo in a larger view with details
        if (selectedPhoto == null) {
        showAlert("No Photo Selected", "Please select a photo to display.", Alert.AlertType.INFORMATION);
        return;
        }

        // Create a new stage for the photo display
        Stage photoStage = new Stage();
        photoStage.setTitle("Photo Display");

        // Create an ImageView for the photo
        ImageView imageView = new ImageView(new Image("file:" + selectedPhoto.getPath()));
        imageView.setFitWidth(800); // Adjust this to suit the full resolution or the size of the new window
        imageView.setPreserveRatio(true);

        // Create labels for caption, date-time, and tags
        Label captionLabel = new Label("Caption: " + selectedPhoto.getCaption());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = selectedPhoto.getDateTaken().format(formatter);


        Label dateTimeLabel =  new Label("Date-Time: " + formattedDateTime);
        String tagsString = selectedPhoto.getTags().stream()
                .map(Tag::toString)
                .collect(Collectors.joining(", "));
        Label tagsLabel = new Label("Tags: " + tagsString);

        // Layout for the new scene
        VBox layout = new VBox(10, imageView, captionLabel, dateTimeLabel, tagsLabel);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10));

        // Set the scene and show the stage
        Scene scene = new Scene(layout);
        photoStage.setScene(scene);
        photoStage.setResizable(false);
        photoStage.show();
    }

    /**
     * does logic for when the add slideshow button is clicked
     */
    @FXML
    private void onSlideshowBtnClicked() {
        // Start a slideshow of photos
        if (selectedAlbum == null || selectedAlbum.getPhotos().isEmpty()) {
        showAlert("No Photos Available", "There are no photos in the current album to display.", Alert.AlertType.INFORMATION);
        return;
        }

        // Create a new stage for the slideshow
        Stage slideshowStage = new Stage();
        slideshowStage.initModality(Modality.APPLICATION_MODAL); // Block input events to other windows
        slideshowStage.setTitle("Slideshow");

        // Create ImageView for displaying photos
        ImageView imageView = new ImageView();
        imageView.setFitWidth(400); // Adjust as needed
        imageView.setPreserveRatio(true);

        // Buttons for navigating the slideshow
        Button prevButton = new Button("Previous");
        Button nextButton = new Button("Next");

        // Current photo index
        final int[] photoIndex = {0};

        // Function to update the image view for the current photo index
        Consumer<Integer> updateImageView = index -> {
            StorePhoto currentPhoto = selectedAlbum.getPhotos().get(photoIndex[0]);
            imageView.setImage(new Image("file:" + currentPhoto.getPath()));
        };

        // Initial update
        updateImageView.accept(photoIndex[0]);

        // Set button actions
        prevButton.setOnAction(event -> {
            if (photoIndex[0] > 0) {
                photoIndex[0]--;
                updateImageView.accept(photoIndex[0]);
            }
        });

        nextButton.setOnAction(event -> {
            if (photoIndex[0] < selectedAlbum.getPhotos().size() - 1) {
                photoIndex[0]++;
                updateImageView.accept(photoIndex[0]);
            }
        });

        // Layout for buttons
        HBox buttonBox = new HBox(10, prevButton, nextButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Main layout
        VBox layout = new VBox(10, imageView, buttonBox);
        layout.setAlignment(Pos.CENTER);

        // Set scene and show stage
        Scene scene = new Scene(layout, 800, 600); // Adjust the size as needed
        slideshowStage.setScene(scene);
        slideshowStage.showAndWait();
    }

    /**
     * does logic for when the gallery photo button is clicked
     */
    @FXML
    private void onGalleryViewBtnClicked(ActionEvent event) {
        // Close the application
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
     * does logic for when the logout button is clicked
     */
    @FXML
    private void onLogoutBtnClicked(ActionEvent event) {
        // Logout user and show login screen
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
     * Loads photos from the selected album into the list view.
     */
    private void loadPhotos() {
        PhotoListView.getItems().clear(); 

        if (selectedAlbum != null && selectedAlbum.getPhotos() != null && !selectedAlbum.getPhotos().isEmpty()) {
            for (StorePhoto photo : selectedAlbum.getPhotos()) {
                Node photoView = createPhotoViewWithCaption(photo);
                PhotoListView.getItems().add(photoView);
            }
        } else {
            Label noPhotosLabel = new Label("No Photos Available");
            PhotoListView.setPlaceholder(noPhotosLabel);
        }
    }

     /**
     * Sets up the ListView for displaying photos.
     */
    private void setupListView() {

    // Configure ListView properties
    PhotoListView.setCellFactory(lv -> new ListCell<Node>() {
        @Override
        protected void updateItem(Node item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setGraphic(null);
            } else {
                
                setGraphic(item);
                }
            }
        });

    }
    
    /**
     * Creates a Node that contains a photo view with its caption.
     *
     * @param photo The photo to be displayed
     * @return A Node containing the photo view and caption
     */
    private Node createPhotoViewWithCaption(StorePhoto photo) 
    {
        ImageView imageView = new ImageView(new Image("file:" + photo.getPath()));
        imageView.setFitHeight(100); 
        imageView.setPreserveRatio(true);
        
        Label captionLabel = new Label(photo.getCaption());
        captionLabel.setWrapText(true);

        VBox container = new VBox(5, imageView, captionLabel); 
        container.setPadding(new Insets(10)); 
        container.setAlignment(Pos.CENTER); 
        
        container.setOnMouseClicked(event -> {
            PhotoListView.getSelectionModel().clearSelection();
            PhotoListView.getSelectionModel().select(container);
            selectedPhoto = photo; 
        });

    return container;
    }

    /**
     * Displays an alert dialog to the user.
     *
     * @param title     The title of the alert
     * @param content   The content message of the alert
     * @param alertType The type of alert
     */
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
}
