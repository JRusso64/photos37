package Controllers;

import Models.Album;
import Models.StorePhoto;
import Models.Admin;
import Models.User;
import Models.UserSession;
import Models.Tag;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Iterator;


import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.util.Pair;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;





/**
 * Controller for the Gallery View in a photo album application.
 * This controller handles user interactions in the gallery view, 
 * including creating, deleting, and renaming albums, as well as searching for photos.
 */
public class GalleryController {

    //Fields
    @FXML
    private ListView<Album> albumListView;
   
    @FXML
    private Button CreateAlbumBtn, DeleteAlbumBtn, RenameAlbumBtn, SearchBtn, LogoutBtn;

    private User currentUser; 
    private ObservableList<Album> observableAlbums;
    public Admin admin; 

    /**
     * Initializes current user
     */
    @FXML
    public void setCurrentUser(User user){
        currentUser = user;
    }
    
    /**
     * Initializes the controller after its root element has been completely processed.
     * Sets up the ListView of albums and configures the Admin instance.
     */
    @FXML
    public void initialize(){
        
        observableAlbums = FXCollections.observableArrayList();
        currentUser = UserSession.getInstance().getCurrentUser();
        observableAlbums.addAll(currentUser.getAlbums());

        albumListView.setItems(observableAlbums);
        albumListView.setCellFactory(param -> new CustomListCell());

        if (observableAlbums.isEmpty()) {
            albumListView.setPlaceholder(new Label("No albums "));
        }

        this.admin = Admin.getAdmin(); 

    }

    /**
     * Handles the action of creating a new album.
     * Prompts the user to enter a name for the new album and adds it to the current user's album list.
     */
    @FXML
    private void onCreateAlbumBtnClicked(){
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create Album");
        dialog.setHeaderText("Create a New Album");
        dialog.setContentText("Enter the name of an album:");

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
                showAlert("Album Already Exists", "An album with the name '" + trimmedAlbumName + "' already exists. Choose a different name.", Alert.AlertType.ERROR);
                return;
            }

            Album newAlbum = new Album(albumName);
            currentUser.CreateAlbum(albumName);
            observableAlbums.add(newAlbum);
        });

        admin.saveUsersToFile("src/User-Data/users.dat");
    }
    
    /**
     * Handles the action of deleting an existing album.
     * Prompts the user for confirmation before deleting the selected album.
     */
    @FXML
    private void onDeleteAlbumBtnClicked(){
        Album selectedAlbum = albumListView.getSelectionModel().getSelectedItem();
        if (selectedAlbum != null) {
            // Confirmation dialog
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Album");
            alert.setHeaderText("Delete Album: " + selectedAlbum.getName());
            alert.setContentText("Are you sure you want to delete this album?");
    
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Delete the album
                currentUser.DeleteAlbum(selectedAlbum.getName());
                observableAlbums.remove(selectedAlbum);
                // Update ListView
                albumListView.refresh();
            }

            admin.saveUsersToFile("src/User-Data/users.dat");

        } else {
            // No album selected
            Alert noSelectionAlert = new Alert(Alert.AlertType.INFORMATION);
            noSelectionAlert.setTitle("No Selection");
            noSelectionAlert.setHeaderText(null);
            noSelectionAlert.setContentText("Please select an album to delete.");
            noSelectionAlert.showAndWait();
        }
    }

    /**
     * Handles the action of renaming an existing album.
     * Allows the user to enter a new name for the selected album.
     */
    @FXML
    private void onRenameAlbumBtnClicked(){
        Album selectedAlbum = albumListView.getSelectionModel().getSelectedItem();
        if (selectedAlbum != null) {
            // Display a dialog to get the new name from the user
            TextInputDialog dialog = new TextInputDialog(selectedAlbum.getName());
            dialog.setTitle("Rename Album");
            dialog.setHeaderText("Rename Album: " + selectedAlbum.getName());
            dialog.setContentText("Enter new name:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(newName -> {
                // Check if the new name is different and non-empty
                if (!newName.trim().isEmpty() && !newName.equals(selectedAlbum.getName())) {
                    // Update the album name
                    currentUser.RenameAlbum(selectedAlbum.getName(), newName);
                    selectedAlbum.setName(newName);
                    albumListView.refresh(); // Refresh ListView to show new name
                } else {
                    // Handle cases where the new name is invalid or the same as the old name
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Invalid Name");
                    alert.setHeaderText(null);
                    alert.setContentText("Please enter a different name.");
                    alert.showAndWait();
                }
            });

            admin.saveUsersToFile("src/User-Data/users.dat");

        } else {
            // Handle cases where no album is selected
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No Album Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select an album to rename.");
            alert.showAndWait();
        }
    }

    /**
     * Opens the photo screen for the selected album.
     * Loads the photo screen FXML and passes the selected album to its controller.
     */
    @FXML
    private void onOpenAlbumBtnClicked(){
        Album selectedAlbum = albumListView.getSelectionModel().getSelectedItem();
        if (selectedAlbum != null) {
            try {
                // Load the photo screen FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/PhotoScreen.fxml")); 
                Parent photoScreenRoot = loader.load();
    
                // Get the controller and set the selected album
                PhotoScreenController photoScreenController = loader.getController();

                Album actualAlbum = currentUser.getAlbumByName(selectedAlbum.getName()); 
                if (actualAlbum != null) {
                    photoScreenController.setCurrentUser(currentUser); 

                    photoScreenController.setSelectedAlbum(actualAlbum);

                   
                } else {
                    
                }
                
                // Switch to the photo screen scene
                Scene photoScreenScene = new Scene(photoScreenRoot);
                Stage primaryStage = (Stage) albumListView.getScene().getWindow(); 
                primaryStage.setScene(photoScreenScene);
                primaryStage.setTitle("" + selectedAlbum.name + " photos");
                primaryStage.setResizable(false);
                primaryStage.show();
    
            } catch (IOException e) {
                e.printStackTrace();                
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Cannot Open Album");
                alert.setContentText("An error occurred while trying to open the album.");
                alert.showAndWait();
            }
        } else {
            // No album selected
            Alert noSelectionAlert = new Alert(Alert.AlertType.INFORMATION);
            noSelectionAlert.setTitle("No Album Selected");
            noSelectionAlert.setHeaderText(null);
            noSelectionAlert.setContentText("Please select an album to open.");
            noSelectionAlert.showAndWait();
        }


    }

    /**
     * Handles user logout action.
     * Closes the current stage and opens the login screen.
     */
    @FXML
    private void onLogoutBtnClicked(ActionEvent event){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/LoginView.fxml"));
            Parent root = loader.load();

            // Get the current stage (Login screen stage)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the scene to the admin screen
            Stage adminStage = new Stage();
            adminStage.setScene(new Scene(root));
            UserSession.getInstance().clearSession();
            // Show the admin screen and close the login screen
            adminStage.setTitle("Photo Storage Application");
            adminStage.show();
            stage.close();
            } catch(Exception e)
            {
                e.printStackTrace();
            }
    }
    
     /**
     * Initiates the search process based on the user's choice of search type.
     * Offers options for searching by date range or tags.
     */
    @FXML
    private void onSearchBtnClicked(){
        // Ask for search type
        List<String> choices = Arrays.asList("Date Range", "Tag Search");
        ChoiceDialog<String> initialChoiceDialog = new ChoiceDialog<>(null, choices);
        initialChoiceDialog.setTitle("Search Options");
        initialChoiceDialog.setHeaderText("Choose your search type");
        initialChoiceDialog.setContentText("Select:");

        Optional<String> initialChoice = initialChoiceDialog.showAndWait();
        initialChoice.ifPresent(choice -> {
            switch (choice) {
                case "Date Range":
                    performDateRangeSearch();
                    break;
                case "Tag Search":
                    onSearchByTagClicked();
                    break;
            }
        });
    }
    
    /**
     * Initiates the search for min and max date and showcase user input dialog
     */
    private void performDateRangeSearch(){
        Dialog<Pair<LocalDate, LocalDate>> dialog = new Dialog<>();
        dialog.setTitle("Search by Date Range");

        // Set the button types.
        ButtonType searchButtonType = new ButtonType("Search", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(searchButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        DatePicker startDatePicker = new DatePicker();
        DatePicker endDatePicker = new DatePicker();

        LocalDate minDate = getMinDateFromAlbums();
        LocalDate maxDate = getMaxDateFromAlbums();

        Label dateRangeInfo = new Label(String.format("Min Date: %s\nMax Date: %s", minDate, maxDate));
        grid.add(dateRangeInfo, 0, 0, 2, 1);

        grid.add(new Label("Start Date:"), 0, 1);
        grid.add(startDatePicker, 1, 1);
        grid.add(new Label("End Date:"), 0, 2);
        grid.add(endDatePicker, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the start date field by default.
        Platform.runLater(startDatePicker::requestFocus);

        // Convert the result to a pair of LocalDate when the search button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == searchButtonType) {
                return new Pair<>(startDatePicker.getValue(), endDatePicker.getValue());
            }
            return null;
        });

        Optional<Pair<LocalDate, LocalDate>> result = dialog.showAndWait();
        
        result.ifPresent(dateRange -> {
            LocalDate startDate = dateRange.getKey();
            LocalDate endDate = dateRange.getValue();
            // Check if either startDate or endDate is null (i.e., not provided by the user)
            if (startDate == null || endDate == null) {
                showAlert("Incomplete Date Range", "Please provide both start and end dates.", Alert.AlertType.ERROR);
                return;
            }

            List<StorePhoto> results = getPhotosInDateRange(startDate, endDate);

            if (results.isEmpty()) {
                showAlert("No Results", "No photos found in the given date range.", Alert.AlertType.INFORMATION);
                return;
            } else {
                displaySearchResults(results);
            }
            
        });
    }
    
    /**
     * gets min date from all albums
     */
    private LocalDate getMinDateFromAlbums() {
        return currentUser.getAlbums().stream()
        .flatMap(album -> album.getPhotos().stream())
        .map(photo -> photo.getDateTaken().toLocalDate())
        .min(Comparator.naturalOrder())
        .orElse(LocalDate.now());// In case there are no photos, return the current date as a fallback
    }

    /**
     * gets max date from all albums
     */
    private LocalDate getMaxDateFromAlbums() {
        return currentUser.getAlbums().stream()
            .flatMap(album -> album.getPhotos().stream())
            .map(photo -> photo.getDateTaken().toLocalDate())
            .max(Comparator.naturalOrder())
            .orElse(LocalDate.now()); // In case there are no photos, return the current date as a fallback
    }

    /**
     * gets list of photos from date range given by user
     */
    private List<StorePhoto> getPhotosInDateRange(LocalDate startDate, LocalDate endDate) {
        List<StorePhoto> matchingPhotos = new ArrayList<>();

        // Convert start and end date to LocalDateTime at start/end of day for comparison
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        for (Album album : currentUser.getAlbums()) {
            for (StorePhoto photo : album.getPhotos()) {
                // Get the photo's LocalDateTime directly
                LocalDateTime photoDateTime = photo.getDateTaken();

                // Check if the photo's date is within the range
                if (!photoDateTime.isBefore(startDateTime) && !photoDateTime.isAfter(endDateTime)) {
                    matchingPhotos.add(photo);
                }
            }
        }

        return matchingPhotos;
    }

    /**
     * Generates dialog box for tag searching
     */
    private void onSearchByTagClicked() {
        // Ask user for the number of tags to search by
        List<String> choices = new ArrayList<>();
        choices.add("One Tag");
        choices.add("Two Tags");

        ChoiceDialog<String> dialog = new ChoiceDialog<>("One Tag", choices);
        dialog.setTitle("Search by Tags");
        dialog.setHeaderText("How many tags would you like to search by?");
        dialog.setContentText("Choose your option:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(numberOfTags -> {
            if (numberOfTags.equals("One Tag")) {
                performSingleTagSearch();
            } else if (numberOfTags.equals("Two Tags")) {
                performDoubleTagSearch();
            }
        });
    }

    /**
     * generates results of single tag search
     */
    private void performSingleTagSearch() {
        Set<String> availableTagPairs = getAllAvailableTags();

        ChoiceDialog<String> tagDialog = new ChoiceDialog<>(null, availableTagPairs);
        tagDialog.setTitle("Search by Single Tag");
        tagDialog.setHeaderText("Select a tag for searching:");
        tagDialog.setContentText("Available tags:");

        Optional<String> tagResult = tagDialog.showAndWait();
        tagResult.ifPresent(tag -> {
            // Split the tag into key and value
            String[] parts = tag.split(": ");
            String key = parts[0];
            String value = parts[1];

            // Perform search with the selected tag
            Tag searchTag = new Tag(key, value);

            // Call the single tag search method and get the results
            List<StorePhoto> results = getPhotosBySingleTag(searchTag);
            
            displaySearchResults(results);
        });
    }

    /**
     * helper method to get photos by single tag
     */
    private List<StorePhoto> getPhotosBySingleTag(Tag searchTag) {
        List<StorePhoto> matchingPhotos = new ArrayList<>();
        for (Album album : currentUser.getAlbums()) {
            for (StorePhoto photo : album.getPhotos()) {
                if (photo.getTags().contains(searchTag)) {
                    matchingPhotos.add(photo);
                }
            }
        }
        return matchingPhotos;
    }

    /**
     * generates results of double tag search
     */
    private void performDoubleTagSearch() {
        Set<String> availableTagPairs = getAllAvailableTags();

        // Create the custom dialog.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Search by Tags");

        // Set the button types.
        ButtonType searchButtonType = new ButtonType("Search", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(searchButtonType, ButtonType.CANCEL);

        // Create the tag and condition ComboBoxes
        ComboBox<String> tag1ComboBox = new ComboBox<>(FXCollections.observableArrayList(availableTagPairs));
        ComboBox<String> tag2ComboBox = new ComboBox<>(FXCollections.observableArrayList(availableTagPairs));
        ComboBox<String> conditionComboBox = new ComboBox<>(FXCollections.observableArrayList("AND", "OR"));

        // Layout the dialog components
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label("First Tag:"), 0, 0);
        grid.add(tag1ComboBox, 1, 0);
        grid.add(new Label("Second Tag:"), 0, 1);
        grid.add(tag2ComboBox, 1, 1);
        grid.add(new Label("Condition:"), 0, 2);
        grid.add(conditionComboBox, 1, 2);

        dialog.getDialogPane().setContent(grid);

        // Request focus on the first field by default.
        Platform.runLater(tag1ComboBox::requestFocus);

        // Convert the result when the search button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == searchButtonType) {
                return new Pair<>(tag1ComboBox.getValue(), tag2ComboBox.getValue());
            }
            return null;
        });
        
       
        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(tagPair -> {
            String condition = conditionComboBox.getValue();
            
            if (tagPair.getKey() == null || tagPair.getKey().isEmpty() || tagPair.getValue() == null || tagPair.getValue().isEmpty()  ) {
                showAlert("Incomplete Tags", "Please select both tags.", Alert.AlertType.ERROR);
                return;
            }

            if (condition == null || condition.trim().isEmpty()) {
                showAlert("Condition Not Filled", "Please select a condition.", Alert.AlertType.ERROR);
                return;
            }

            // Split the tags into key and value
            String[] tag1Parts = tagPair.getKey().split(": ");
            String[] tag2Parts = tagPair.getValue().split(": ");
            
            // Perform search with the selected tags and condition
            
            Tag firstTag = new Tag(tag1Parts[0].trim(), tag1Parts[1].trim());
            Tag secondTag = new Tag(tag2Parts[0].trim(), tag2Parts[1].trim());

            
            List<StorePhoto> results = getPhotosByDoubleTag(firstTag,secondTag,condition);

            if (results.isEmpty()) {
                showAlert("No Results", "No photos found with both tags and conditional.", Alert.AlertType.INFORMATION);
                return;
            } else {
                displaySearchResults(results);
            }
        });

    }

    /**
     * helper method to get photos by double tag
     */
    private List<StorePhoto> getPhotosByDoubleTag(Tag firstTag, Tag secondTag, String condition) 
    {
        List<StorePhoto> matchingPhotos = new ArrayList<>();
        for (Album album : currentUser.getAlbums()) {
            for (StorePhoto photo : album.getPhotos()) {
                boolean firstTagMatch = photo.getTags().contains(firstTag);
                boolean secondTagMatch = photo.getTags().contains(secondTag);

                // Check condition
                if ("AND".equals(condition) && firstTagMatch && secondTagMatch) {
                    matchingPhotos.add(photo);
                } else if ("OR".equals(condition) && (firstTagMatch || secondTagMatch)) {
                    matchingPhotos.add(photo);
                }
            }
        }
        return matchingPhotos;
    }

    /**
     * helper method to get all tags from all albums
     */
    private Set<String> getAllAvailableTags() 
    {
        
        Set<String> allTagPairs = new HashSet<>();
        for (Album album : currentUser.getAlbums()) {
            for (StorePhoto photo : album.getPhotos()) {
                for (Tag tag : photo.getTags()) {
                    allTagPairs.add(tag.getKey() + ": " + tag.getVal());
                }
            }
        }
        return allTagPairs;
    }

    /**
     * Displays search results in a new stage or scene.
     * Passes the matching photos to the search results controller.
     * 
     * @param matchingPhotos List of photos that match the search criteria
     */
    private void displaySearchResults(List<StorePhoto> matchingPhotos) {
        try {
            // Load the search results screen FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/SearchedPhotosScreen.fxml")); // Update the path to your FXML file
            Parent searchResultsRoot = loader.load();

            // Get the controller for the search results screen
            SearchPhotoController searchResultsController = loader.getController();
            

            // remove duplicates

            Set<String> paths = new HashSet<>(); // to store unique paths
            Iterator<StorePhoto> iterator = matchingPhotos.iterator();

            while (iterator.hasNext()) {
                StorePhoto photo = iterator.next();
                String path = photo.getPath();
                
                // If the set already contains this path, remove the photo from the iterator (and thus the list)
                if (!paths.add(path)) {
                    iterator.remove();
                }
            }
            // Pass the list of matching photos to the controller
            
            searchResultsController.setMatchingPhotos(matchingPhotos,currentUser);

            // Prepare and display the new stage or scene
            Scene searchResultsScene = new Scene(searchResultsRoot);
            Stage searchResultsStage = new Stage();
            searchResultsStage.setTitle("Search Results");
            searchResultsStage.setScene(searchResultsScene);
            
            // Optional: if you want to block interaction with other windows until this one is closed
            searchResultsStage.initModality(Modality.APPLICATION_MODAL); 
            searchResultsStage.setResizable(false);
            Stage currentStage = (Stage) SearchBtn.getScene().getWindow();
            currentStage.close();
            searchResultsStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            // Handle the IOException here, perhaps with an error dialog
            showAlert("Error", "An error occurred while trying to display the search results.", Alert.AlertType.ERROR);
        }
    }

    /**
     * Shows an alert dialog with the specified title, content, and alert type.
     * 
     * @param title     Title of the alert dialog
     * @param content   Content text for the alert dialog
     * @param alertType Type of the alert (e.g., ERROR, INFORMATION)
     */
    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

 }

