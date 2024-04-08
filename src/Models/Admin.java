package Models;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Represents the admin functionality of the photo storage application.
 * This includes managing users and persisting user data.
 */
public class Admin implements Serializable {
    private static final long serialVersionUID = 1L;
    private static Admin admin;
    private transient List<User> users;

    /**
     * Gets the singleton singletonInstance of Admin.
     * Creates a new instance if it doesn't already exist.
     *
     * @return the singleton Admin instance
     */
    public Admin() {
        this.users = new ArrayList<>();
    }

    
    /** 
     * @return Admin
     */
    public static Admin getAdmin() {
        if (admin == null) {
            admin = new Admin();
        }
        return admin;
    }

    /**
     * Adds a user with the specified username if it doesn't already exist.
     *
     * @param username the username of the user to add
     */
    public void addUser(String username) {
        if (getUserByUsername(username).isEmpty()) {
            users.add(new User(username));
        } else {
            
        }
    }

    /**
     * Deletes a user with the specified username.
     *
     * @param username the username of the user to delete
     */
    public void deleteUser(String username) {
        users.removeIf(user -> user.getUsername().equals(username));
            
    }

    /**
     * Gets a user by their username.
     *
     * @param username the username of the user to find
     * @return an Optional containing the user if found, or an empty Optional otherwise
     */
    public Optional<User> getUserByUsername(String username) {
        return users.stream()
                    .filter(user -> user.getUsername().equals(username))
                    .findFirst();
    }

    /**
     * Gets a list of all users.
     *
     * @return a list of users
     */
    public List<User> getUsers() {
        return new ArrayList<>(users); 
    }

    /**
     * Saves the list of users to a file.
     *
     * @param filename the name of the file to save to
     */
    public void saveUsersToFile(String filename) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(users);
        } catch (IOException err) {
            System.err.println("Error saving users: " + err.getMessage());
        }
    }

    /**
     * Loads the users from a file.
     *
     * @param fname the name of the file to load from
     */
    @SuppressWarnings("unchecked")
    public void loadUsers(String fname) {
        File file = new File(fname);
        if (!file.exists() || file.length() == 0) {
            return; 
        }

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = in.readObject();
            if (obj instanceof List) {
                this.users = (List<User>) obj;
            }
        } catch (EOFException error) {
            System.err.println("End of file reached unexpectedly: " + error.getMessage());
        } catch (IOException | ClassNotFoundException error) {
            System.err.println("Error loading users: " + error.getMessage());
        }
    }
    
    /**
     * Returns a string of all users.
     *
     * @return a string listing all users
     */
    public String listUsers(){
        StringBuilder sb = new StringBuilder("Users:\n");
        for (User user: users){
            sb.append(user.getUsername()).append("\n");
        }
        return sb.toString();
    }


}