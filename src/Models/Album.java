package Models;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;

import java.util.Objects;
import java.util.List;



/**
 * Represents an album in the system.
 * Each album has a name and can contain multiple photos.
 */
public class Album implements Serializable{
    private static final long serialVersionUID = -3753195309277241711L;
    public String name;
    public List<StorePhoto> photos;

    /**
     * Constructs a new Album with the specified name.
     *
     * @param name the name of the album
     */
    public Album(String name){
        this.name = name;
        this.photos = new ArrayList<>();
    }
    
    /**
     * Adds a photo to the album.
     *
     * @param photo the photo to add
     */
    public void addPhoto(StorePhoto photo){
        photos.add(photo);
    }
    
    /**
     * Removes a photo from the album.
     *
     * @param photo the photo to remove
     * @return true if the photo was removed successfully, false otherwise
     */
    public boolean removePhoto(StorePhoto photo){
        return photos.remove(photo);
    }
    
    /**
     * Gets the name of the album.
     *
     * @return the name of the album
     */
    public String getName(){
        return name;
    }
    
    /**
     * Sets the name of the album.
     *
     * @param name the new name of the album
     */
    public void setName(String name){
        this.name = name;
    }
    
    /**
     * Gets the list of photos in the album.
     *
     * @return the list of photos
     */
    public List<StorePhoto> getPhotos(){
        return photos;
    }
    
    /**
     * Sets the list of photos in the album.
     *
     * @param photos the list of photos to set
     */
    public void setPhotos(List<StorePhoto> photos){
        this.photos = photos;
    }

    /**
     * Compares this album to another object for equality.
     * Albums are considered equal if they have the same name.
     *
     * @param obj the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof Album) {
            Album otherAlbum = (Album) other;
            return this == otherAlbum || (this.name != null && this.name.equals(otherAlbum.name));
        }
        return false;
    }
    /**
     * Computes the date range of photos in the album.
     * 
     * @return an array of two LocalDateTime objects, where the first element is the earliest date
     *         and the second element is the latest date of the photos in the album.
     *         Returns null if there are no photos in the album.
     */
    public LocalDateTime[] getDate() {
        if (photos.isEmpty()) {
        return null;
            }

            LocalDateTime early = photos.get(0).getDateTaken();
            LocalDateTime late = photos.get(0).getDateTaken();

            
            for (StorePhoto photo : photos) {
                LocalDateTime date = photo.getDateTaken();
                if (date.isBefore(early)) {
                    early = date;
                }
                if (date.isAfter(late)) {
                    late = date;
                }
            }
            return new LocalDateTime[] {early, late};
        }
    
    /**
     * Gets the count of photos in the album.
     *
     * @return the number of photos in the album
     */
    public int getCount() 
    {
        return photos.size();
    }
    
    /**
     * Returns a string representation of the album.
     *
     * @return a string containing the name of the album and the number of photos it contains
     */
    @Override
    public String toString(){
        return "Album Name: " + name + "  (" + photos.size() + " photos)";
    }
    
}