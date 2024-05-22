module gamesource.olympicgamesapplicationfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;

    opens gamesource.olympicgamesapplicationfx to javafx.fxml;
    exports gamesource.olympicgamesapplicationfx;
}
