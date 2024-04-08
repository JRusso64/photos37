package Models;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a tag that can be attached to photos.
 * A tag consists of a key (e.g., "Location") and a value (e.g., "New York").
 */
public class Tag implements Serializable{
    
    private String key; 
    private String value; 

     /**
     * Constructs a new Tag with the specified key and value.
     *
     * @param key The key of the tag.
     * @param value The value of the tag.
     */
    public Tag(String key, String value){
        this.key = key;
        this.value = value;
    }
    
    /**
     * Returns the key of the tag.
     *
     * @return The key of the tag.
     */
    public String getKey(){
        return key;
    }
    
    /**
     * Sets the key of the tag.
     *
     * @param key The new key of the tag.
     */
    public void setKey(String key){
        this.key = key;
    }
    
    /**
     * Returns the value of the tag.
     *
     * @return The value of the tag.
     */
    public String getVal(){
        return value;
    }
    
    /**
     * Sets the value of the tag.
     *
     * @param value The new value of the tag.
     */
    public void setVal(String value){
        this.value = value;
    }
    
    /**
     * Indicates whether some other object is "equal to" this tag.
     * Two tags are considered equal if they have the same key and value.
     *
     * @param k The reference object with which to compare.
     * @return true if this tag is the same as the k argument; false otherwise.
     */
    @Override
    public boolean equals(Object k){
        if (this == k) return true;
        if (k == null || getClass() != k.getClass()) return false;
        Tag tags = (Tag) k;
        return key.equals(tags.key) && value.equals(tags.value);
    }

    /**
     * Returns a hash code value for the tag.
     *
     * @return A hash code value for this tag.
     */
    @Override
    public int hashCode(){
        return Objects.hash(key, value);
    }
    
    /**
     * Returns a string representation of the tag.
     * The string consists of the tag's key and value, separated by a colon and a space.
     *
     * @return A string representation of the tag.
     */
    @Override
    public String toString(){
        return key + ": " + value;
    }
}
