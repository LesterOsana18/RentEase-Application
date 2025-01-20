package controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class DummyDashboard implements Initializable {
	
	@FXML
	private Button button_logout;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		button_logout.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBConfig.changeScene(event, "/controller/LoginView.fxml", "RentEase: Simplifying Rent and Utility Management", null);
			}
		});
	}
	
	

}
