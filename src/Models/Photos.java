package Models;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;  
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

  
/**
 * The main application class for the Photo Storage Application.
 * It initializes the application, loads user data, and sets up the primary stage.
 */
public class Photos extends Application implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * Starts the primary stage.
     * Initializes the Admin instance, loads the users, creates a stock user with the intial photos if they do not exist already,
     * and it sets up the login view.
     * 
     * @param primary The primary stage for this application. The application is set on this
     * @throws Exception if any error occurs during loading FXML or setting the stage.
     */
    @Override
    public void start(Stage primary) throws Exception{
        // Initialize the Admin instance
        Admin admin = Admin.getAdmin();
    
        // Load user data from the file
        try {
            admin.loadUsers("src/User-Data/users.dat");

            //Initialize stock user

            //Check if the stock user still exists
            List <User> people = admin.getUsers();

            User copy = null; //Initialize a User to store the stock user
            for (User user : people) {
                if ("stock".equals(user.getUsername())) { 
                    copy = user; 
                    break; 
                }
            }


            if (copy == null || !copy.username.equals( "stock")){
                admin.addUser("stock");
                List <User> up = admin.getUsers();

                User stockUser = null; 
                for (User user : up) {
                    if ("stock".equals(user.getUsername())) { 
                        stockUser = user; 
                        break; 
                    }
                }   

                stockUser.CreateAlbum("stock");
                Album stock = stockUser.getAlbumByName("stock");
                LocalDateTime now = LocalDateTime.now();
                stock.addPhoto(new StorePhoto("data/coffee1.jpg", now));
                stock.addPhoto(new StorePhoto("data/coffee2.jpg", now));
                stock.addPhoto(new StorePhoto("data/coffee3.jpg", now));
                stock.addPhoto(new StorePhoto("data/coffee4.jpg", now));
                stock.addPhoto(new StorePhoto("data/coffee5.jpg", now));
            }
            
        } catch (Exception e) {
            System.err.println("Error loading user data: " + e.getMessage());
            
        }
        
        Parent root = FXMLLoader.load(getClass().getResource("/Views/LoginView.fxml"));
        primary.setTitle("Photo Album Application");
        primary.setScene(new Scene(root));
        primary.setResizable(false);
        primary.show();
    }

    /**
     * The start method is called after the init method has returned, runs our app
     *
     * @param args command line arguments passed to the application.
     *             An application may get these parameters using the getParameters() method.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
