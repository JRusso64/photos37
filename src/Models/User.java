package Models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.io.Serializable;
import java.util.Iterator;


/**
 * Represents a user in the system.
 * A user has a username and can own multiple albums.
 */
public class User implements Serializable {
    
    public static final long serialVersionUID = 1L; 
    public String username;
    public List<Album> albums; 

     /**
     * Constructs a new User with the specified username.
     * Initializes the list of albums owned by the user.
     *
     * @param username The username of the user.
     */
    public User(String username) {
        this.username = username;
        this.albums = new ArrayList<>();
    }
    
     /**
     * Returns a list of albums owned by the user.
     *
     * @return The list of albums.
     */
    public List<Album> getAlbums() {
        return albums;
    }

    /**
     * Returns the username of the user.
     *
     * @return The username of the user.
     */
    public String getUsername() {
        return username;
    }

     /**
     * Creates a new album with the given name and adds it to the user's list of albums.
     *
     * @param AlbumName The name of the new album to be created.
     */
    public void CreateAlbum(String AlbumName) 
    {
        albums.add(new Album(AlbumName));
    }
    /**
     * Renames an existing album from its original name to a new name.
     *
     * @param Og The original name of the album.
     * @param newName The new name for the album.
     */
    public void RenameAlbum(String Og, String newName) 
    {
        for (Album album : albums) {
            if (album.getName().equals(Og)) {
                album.setName(newName);
                break; // Assuming album names are unique, break after finding the album
            }
        }
    }


    /**
     * Deletes an album with the given name from the user's list of albums.
     *
     * @param AlbumName The name of the album to be deleted.
     */
    public void DeleteAlbum(String AlbumName) 
    {
        Iterator<Album> iterator = albums.iterator();
        while (iterator.hasNext()) {
        Album a = iterator.next();
        if (a.getName().equals(AlbumName)) { // Assuming getName() is the method to get album's name
            iterator.remove();
            break;
        }
    }
    }

    
    /**
     * Retrieves an album by its name.
     *
     * @param albumName The name of the album to retrieve.
     * @return The album with the specified name, or null if no such album exists.
     */
    public Album getAlbumByName(String albumName) 
    {
        for (Album album : albums) {
            if (album.getName().equals(albumName)) {
                return album;
            }
        }
        return null; 
    }

    /**
     * Collects all unique tag keys from the user's albums.
     *
     * @return A set of unique tag keys.
     */
    public Set<String> getAllTags() {
        Set<String> allTags = new HashSet<>();
        for (Album album : this.albums) { 
            for (StorePhoto photo : album.getPhotos()) {
                for (Tag tag : photo.getTags()) {
                    allTags.add(tag.getKey()); 
                }
            }
        }
        return allTags;
    }
    
}