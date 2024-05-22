package gamesource.olympicgamesapplicationfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {

        // Load the menu view initially
        FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("menu-view.fxml"));
        Parent menuRoot = menuLoader.load();

        // Pass the stage reference to the MenuController
        MenuController menuController = menuLoader.getController();
        menuController.setStage(primaryStage);

        double screenWidth = Screen.getPrimary().getVisualBounds().getWidth();
        double screenHeight = Screen.getPrimary().getVisualBounds().getHeight();

        primaryStage.setTitle("Olympic Games Application");

        primaryStage.setScene(new Scene(menuRoot, screenWidth, screenHeight));
//        primaryStage.setScene(new Scene(menuRoot, 600, 400));
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");

        primaryStage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}
