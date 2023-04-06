package search.moviewatchlist;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.text.Text;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class watchListController {

    @FXML
    private Text titleLabel;

    @FXML
    private Button removeButton;


    private String User, Title;
    public void setItem(String Title, String User){

        this.User = User;
        this.Title = Title;
        titleLabel.setText(Title);
    }




    public void removeItem(ActionEvent ae){
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

        String[] itemArray = cellValue.split(",");


        String[] newItems  = new String[itemArray.length - 1];

        String update = "";

        for (int i = 0, j = 0; i < itemArray.length; i++){
            String str = itemArray[i];
            if (str.equals(Title)){
                continue;
            }
            else {
                newItems[j] = itemArray[i];
                j++;
            }
        }

         for (int i = 0; i < newItems.length; i++) {
             String str = newItems[i];
             if (i == newItems.length - 1) {
                 update += str;
             } else {
                 update += str + ",";
             }
         }

         String removeMovie;


         if (update == "")
                removeMovie = "UPDATE user_info SET ids = NULL WHERE Userdb = '" + User + "'";

        else {
                removeMovie = "UPDATE user_info SET ids = '" + update + "' WHERE Userdb = '" + User + "'";
         }

        try {
            Statement statement = connectDB.createStatement();
            statement.executeUpdate(removeMovie);

            statement.close();
            connectDB.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
