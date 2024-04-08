package Controllers;

import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import Models.StorePhoto;


/**
 * Handles the update image ui, helper class
 *
 * 
 */
public class PhotoListCell extends ListCell<StorePhoto> {
    
    /**
     * Handles update photo image
     */
    @Override
    protected void updateItem(StorePhoto photo, boolean empty) {
        super.updateItem(photo, empty);
        if (empty || photo == null) {
            setText(null);
            setGraphic(null);
        } else {
            ImageView imageView = new ImageView(new Image("file:" + photo.getPath()));
            imageView.setFitHeight(100);
            imageView.setPreserveRatio(true);

            Label captionLabel = new Label(photo.getCaption());
            VBox vbox = new VBox(imageView, captionLabel);
            vbox.setSpacing(10); 

            setGraphic(vbox);
        }
    }
}