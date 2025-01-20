package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/* Color Code/s Used
 * #FF7F50 - Bright Orange
 * #FFFFFF - White
 * #000000 - Black */

public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			Parent root = FXMLLoader.load(getClass().getResource("/controller/LoginView.fxml"));
			Scene scene = new Scene(root, 1200, 780);
			scene.getStylesheets().add(getClass().getResource("/styles/application.css").toExternalForm());
			primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/imgs/logo.png")));
			primaryStage.setTitle("RentEase: Simplifying Rent and Utility Management");
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}