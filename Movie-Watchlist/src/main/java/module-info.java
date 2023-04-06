module search.moviewatchlist {
    requires javafx.controls;
    requires javafx.fxml;
    requires json.simple;
    requires java.sql;

    opens search.moviewatchlist to javafx.fxml;
    exports search.moviewatchlist;
}