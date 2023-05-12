module com.example.uicrawler {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jetbrains.annotations;
    requires org.jsoup;
    requires json.simple;


    opens com.example.uicrawler to javafx.fxml;
    exports com.example.uicrawler;
}