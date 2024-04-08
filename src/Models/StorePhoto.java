package Models;

import java.util.Set;
import java.util.HashSet;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Represents a photo stored in an album.
 * The photo has properties like tags, date taken, caption, and image path.
 */
public class StorePhoto implements Serializable {
    private static final long serialVersionUID = -2955413310850663444L;
    public Set<Tag> tags;
    public LocalDateTime  date;
    public String caption;
    public String imagePath; 
    
    /**
     * Constructs a new StorePhoto with the specified image path and date.
     *
     * @param imagePath Path to the image file.
     * @param date Date when the photo was taken.
     */
    public StorePhoto(String imagePath, LocalDateTime  date ) {
        this.imagePath = imagePath;
        this.date = date;
        this.tags = new HashSet<>();
        
    }


    /**
     * Returns the file path of the photo.
     *
     * @return The file path of the photo.
     */
    public String getPath(){
        return imagePath;
    }

    /**
     * Sets the caption of the photo.
     *
     * @param caption The caption to set for the photo.
     */
    public void setCaption(String Caption)
    {
        this.caption = Caption;
    }

    /**
     * Returns the caption of the photo.
     *
     * @return The caption of the photo.
     */
    public String getCaption()
    {
        return caption;
    }

    /**
     * Adds a tag to the photo.
     *
     * @param tag The tag to add to the photo.
     */
    public void addTag(Tag tag){
        tags.add(tag);
    }
    
    /**
     * Removes a tag from the photo.
     *
     * @param tag The tag to remove from the photo.
     * @return true if the tag was removed; false otherwise.
     */
    public boolean removeTags(Tag tag){
        return tags.remove(tag);
    }
    
    /**
     * Returns the set of tags associated with the photo.
     *
     * @return The set of tags.
     */
    public Set<Tag> getTags(){
        return tags;
    }

    /**
     * Sets the set of tags for the photo.
     *
     * @param tags The set of tags to be associated with the photo.
     */
    public void setTags(Set<Tag> tag){
        this.tags = tag;
    }
    
    /**
     * Returns the date when the photo was taken.
     *
     * @return The date when the photo was taken.
     */
    public LocalDateTime getDateTaken(){
        return date;
    }
    
    /**
     * Sets the date when the photo was taken.
     *
     * @param date The date to set as the date taken.
     */
    public void setDateTaken(LocalDateTime  date){
        this.date = date;
    }
   
}
