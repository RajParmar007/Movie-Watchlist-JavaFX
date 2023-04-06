package search.moviewatchlist;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

//API
import java.io.IOException;
import java.net.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

//JSON PARSING
import javafx.scene.layout.VBox;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MainController {
    @FXML
    private BorderPane loginPane;

    @FXML
    private Button loginSignupButton;

    @FXML
    private TextField resultsSearch;

    @FXML
    private Button resultsSearchButton;

    @FXML
    private GridPane searchResultPane;

    @FXML
    private Button signUpButton;

    @FXML
    private BorderPane signUpPane;

    @FXML
    private Button signinButton;

    @FXML
    private StackPane stackMain;

    @FXML
    private VBox movieList = null;

    //For database validation
    @FXML
    private Label leftblankerror;


    @FXML
    private TextField username;

    @FXML
    private TextField password;

    @FXML
    private TextField email;

    @FXML
    private TextField confirmpassword;

    //For sign in
    @FXML
    private TextField userid;

    @FXML
    private TextField passid;

    @FXML
    private Label validationlabel;

    @FXML
    private VBox watchList = null;

    @FXML
    private Button refresh;


    int flag = 0;



    private final String search_API_link = "https://api.themoviedb.org/3/search/multi?api_key=60861577c310df46ea9a16c2bcd51716&language=en-US&query=search_query&page=1&include_adult=false";
    private final String poster_API_link = "http://image.tmdb.org/t/p/w92/";

    private String currentUser;

    //sign in button click function
    @FXML
    public void signIn() throws IOException {

        if ((!userid.getText().isBlank()) && (!passid.getText().isBlank())){
            validateLogin();
            if (flag == 1){
                validationlabel.setStyle("-fx-background-color: TRANSPARENT");
                validationlabel.setText("");
                currentUser = userid.getText();
                stackMain.getChildren().clear();
                stackMain.getChildren().add(searchResultPane);

            }
            else {
                validationlabel.setText("Enter Correct Details!");
                validationlabel.setStyle("-fx-background-color: #2962FF");
            }
        }
        else {
            validationlabel.setText("Enter Username and Password!");
            validationlabel.setStyle("-fx-background-color: #2962FF");
        }
    }

    //for validating sign in
    public void validateLogin(){
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String verifyLogin = "SELECT count(1) FROM user_info WHERE Userdb = '" + userid.getText() + "' AND Passdb = '" + passid.getText() + "'";

        try{
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(verifyLogin);

            while(queryResult.next()) {
                if (queryResult.getInt(1) == 1){
                    flag = 1;
                }
                else {
                    break;
                }
            }

        }catch(Exception e){
            e.printStackTrace();
        }

    }
    @FXML
    public void showSignUp() {

        // reset the error label
        leftblankerror.setStyle("-fx-background-color: TRANSPARENT");
        leftblankerror.setText("");
        username.clear();
        email.clear();
        password.clear();
        confirmpassword.clear();


        stackMain.getChildren().clear();
        stackMain.getChildren().add(signUpPane);
    }

    @FXML
    public void initialize() {
        stackMain.getChildren().clear();
        stackMain.getChildren().add(loginPane);
    }

    @FXML
    public void showSearchResult() {

        if ((!username.getText().isBlank()) && (!password.getText().isBlank()) && (!email.getText().isBlank())){
            if (email.getText().contains("@")){
                if(confirmpassword.getText().equals(password.getText())){
                    storeData();

                    // reset the error label
                    validationlabel.setStyle("-fx-background-color: TRANSPARENT");
                    validationlabel.setText("");
                    userid.clear();
                    passid.clear();

                    stackMain.getChildren().clear();
                    stackMain.getChildren().add(loginPane);
                }
                else{
                    leftblankerror.setText("Password Must be the Same!");
                    leftblankerror.setStyle("-fx-background-color: #2962FF");
                }
            }
            else {
                leftblankerror.setText("Email is invalid.");
                leftblankerror.setStyle("-fx-background-color: #2962FF");
            }
        }
        else {
            leftblankerror.setText("All Fields must be filled!");
            leftblankerror.setStyle("-fx-background-color: #2962FF");
        }
    }

    //For storing data when sign up is clicked
    public void storeData() {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String userName = username.getText();
        String passWord = password.getText();

        String insertData = "INSERT INTO user_info (Userdb, Passdb) " +
                "VALUES ('" + userName + "', '" + passWord + "')";

        try {
            Statement statement = connectDB.createStatement();
            int rowsInserted = statement.executeUpdate(insertData);

            if (rowsInserted > 0) {
                System.out.println("Data inserted successfully!");
            } else {
                System.out.println("Failed to insert data");
            }

            statement.close();
            connectDB.close();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Username");
            alert.setHeaderText("Username Already Exists");
            alert.setContentText("Please pick another username!");
            alert.showAndWait();
        }
    }



    public void testing() throws IOException, ParseException {
        String searchBarText = (resultsSearch.getText()).trim();

        if( searchBarText.isEmpty() ){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Entry");
            alert.setHeaderText ("Search for Something!");
            alert.setContentText ("Enter a Movie Title or Television Show Name in the search box");
            alert.showAndWait();
            return;
        }

        searchBarText = searchBarText.replace(" ", "%20");

        String search_link = search_API_link.replace("search_query", searchBarText);

        URL url = new URL(search_link);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        int responseCode = conn.getResponseCode();

        if (responseCode != 200) {
            System.out.println("Error");
        }
        else {
            StringBuilder informationString = new StringBuilder();
            Scanner scanner = new Scanner(url.openStream());

            while (scanner.hasNext()) {
                informationString.append(scanner.nextLine());
            }
            //Close the scanner
            scanner.close();

            String results = String.valueOf(informationString);

            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(results);
            JSONArray media_array = (JSONArray) jsonObject.get("results");

            int s = media_array.size();

            if (s == 0) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Entry");
                alert.setHeaderText("Nothing Found!");
                alert.setContentText("No Movie Titles or Television Shows found!");
                alert.showAndWait();
            }
            else {
                List<String> titles = new ArrayList<>(); //contains name
                List<String> overviews = new ArrayList<>(); //contains overview
                List<String> types = new ArrayList<>(); //contains media type - tv or movie
                List<String> posters = new ArrayList<>(); //contains poster links
                List<Long> ids = new ArrayList<>(); //contains imdb id of the media item
                List<Double> rating = new ArrayList<>(); //contains media item rating

                int count = 0;

                for (int i = 0; i < s; i++) {
                    JSONObject media_object = (JSONObject) media_array.get(i);
                    if (media_object.get("media_type").equals("tv") || media_object.get("media_type").equals("movie")) {
                        count++;
                    }
                }

                Node[] movieItems = new Node[count];

                movieList.getChildren().clear();
                for (int i = 0, j = 0; i < s; i++) {
                    JSONObject media_object = (JSONObject) media_array.get(i);
                    if (media_object.get("media_type").equals("tv") || media_object.get("media_type").equals("movie")) {

                        types.add((String) media_object.get("media_type"));

                        if (Objects.equals((String) media_object.get("media_type"), "tv")) {
                            titles.add((String) media_object.get("name"));
                        } else {
                            titles.add((String) media_object.get("title"));
                        }


                        overviews.add((String) media_object.get("overview"));
                        posters.add(poster_API_link + (String) media_object.get("poster_path"));
                        ids.add((long) media_object.get("id"));
                        rating.add((double) (media_object.get("vote_average")));


                        FXMLLoader loader = new FXMLLoader(getClass().getResource("movieListItem.fxml"));
                        movieItems[j] = loader.load();

                        movieItemController controller = loader.getController();
                        controller.setMovieInfo(titles.get(j), overviews.get(j), rating.get(j), posters.get(j), ids.get(j), currentUser);
                        if(j%2 == 1)
                            movieItems[j].setStyle("-fx-background-color: #1565C0");

                        movieList.getChildren().add(movieItems[j]);
                        j++;
                    }
                }


            }
        }
    }



    public void WatchlistData() throws IOException {
        DatabaseConnection connectNow = new DatabaseConnection();
        Connection connectDB = connectNow.getConnection();

        String retrieveWatchlist = "SELECT ids FROM user_info WHERE Userdb = '" + currentUser + "'";
        String cellValue = "";

        try {
            Statement statement = connectDB.createStatement();
            ResultSet queryResult = statement.executeQuery(retrieveWatchlist);
            if (queryResult.next()) {
                cellValue = queryResult.getString("ids");
            }

            statement.close();
            queryResult.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!(cellValue == null))
        {
            String[] moviesArray = cellValue.split(",");
            Node[] watchListItems = new Node[moviesArray.length];



            watchList.getChildren().clear();
            for (int i = 0; i < moviesArray.length; i++){
                FXMLLoader loader = new FXMLLoader(getClass().getResource("watchListItem.fxml"));
                watchListItems[i] = loader.load();
                watchListController controller = loader.getController();

                controller.setItem(moviesArray[i], currentUser);

                if(i%2 == 1)
                    watchListItems[i].setStyle("-fx-background-color: #1565C0");

                watchList.getChildren().add(watchListItems[i]);
            }
        }
        else {
            watchList.getChildren().clear();
        }




    }



}
