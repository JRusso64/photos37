package Controllers;

import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.geometry.Insets;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import Models.Album;

/**
 * A custom list cell for displaying an album in a ListView.
 * Each cell displays an album's name, date range, and photo count.
 */
public class CustomListCell extends ListCell<Album> {
   
    private HBox content;
    private Text title;
    private Text details;
    private ImageView image;

    /**
     * Constructs a CustomListCell with initialized components.
     */
    public CustomListCell() {
        super();
        title = new Text();
        details = new Text();
        image = new ImageView(new Image("/Controllers/Icon/folder.jpg")); // Update path
        image.setFitHeight(30);
        image.setFitWidth(30);
        content = new HBox(image, title, details);
        content.setSpacing(10);
        content.setPadding(new Insets(5, 0, 5, 0));
    }

    /**
     * Updates the album item in the list cell.
     * This method is called automatically to configure the cell based on the album data.
     *
     * @param album the album to display in this cell
     * @param empty a flag indicating whether the cell is empty
     */
    @Override
    protected void updateItem(Album album, boolean empty) {
        super.updateItem(album, empty);
        if (album != null && !empty) {
            title.setText(album.getName());
            
            // Get the date range
            LocalDateTime[] dateRange = album.getDate();
            if (dateRange != null) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy");
                String startDate = dateRange[0].format(formatter);
                String endDate = dateRange[1].format(formatter);
                details.setText("Dates: " + startDate + " - " + endDate + " | Photos: " + album.getCount());
            } else {
                details.setText("Dates: N/A | Photos: " + album.getCount());
            }

            setGraphic(content);
        } else {
            setGraphic(null);
        }
        }

}
