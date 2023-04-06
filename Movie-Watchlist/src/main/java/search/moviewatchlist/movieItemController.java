package search.moviewatchlist;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.text.DecimalFormat;

import java.net.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;

public class movieItemController {

    @FXML
    private Button movieAddButton;

    @FXML
    private Label movieDesc;

    @FXML
    private ImageView movieImage;

    @FXML
    private Label movieRating;

    @FXML
    private Label movieTitle;
    long ID;
    private final File file = new File("src/main/resources/images/no-movie-poster.jpeg");
    private final Image image = new Image(file.toURI().toString());

    private String Title, User;


    public void setMovieInfo(String Title, String Desc, double Rating, String imageURL, long ID, String User)
    {
//        System.out.println("Title = " + Title + ", Desc = " + Desc + ", Rating = " + Rating + ", image = " + imageURL + ", ID = " + ID);
        this.Title = Title;
        this.User = User;

        movieTitle.setText(Title);
        movieDesc.setText(Desc);

        DecimalFormat df = new DecimalFormat("#.#");

        movieRating.setText("Rating: " + (df.format(Rating).equals("0") ? "None" : (df.format(Rating)) + "/10.0"));

        if(imageURL.endsWith("null"))
            movieImage.setImage(this.image);
        else
            movieImage.setImage(new Image(imageURL));
        this.ID = ID;
    }

    public void AddMovie(ActionEvent e){
        storeData();
    }


    public void storeData() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String retrieveWatchlist = "SELECT ids FROM user_info WHERE Userdb = '" + User + "'";
        String cellValue = "";

        try{
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(retrieveWatchlist);
            if (queryResult.next()) {
                cellValue = queryResult.getString("ids");
            }

            statement.close();
            queryResult.close();

        }catch(Exception e){
            e.printStackTrace();
        }

        String insertMovie = "";

        Title = (Title.replace("'", ""));

        if (cellValue == null){
            insertMovie = "UPDATE user_info SET ids = '" + Title + "' WHERE Userdb = '" + User + "'";
        }
        else {

            String[] moviesArray =  cellValue.split(",");

            boolean test = Arrays.asList(moviesArray).contains(Title);

            if (test == true) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Duplicate");
                alert.setHeaderText("Item not added!");
                alert.setContentText("Item was already present in the watchlist!");
                alert.showAndWait();
                return;
            }

            insertMovie = "UPDATE user_info SET ids = '" + (cellValue + "," + Title) + "' WHERE Userdb = '" + User + "'";
        }



        try {
            Statement statement = connectDB.createStatement();
            statement.executeUpdate(insertMovie);

            statement.close();
            connectDB.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




}
