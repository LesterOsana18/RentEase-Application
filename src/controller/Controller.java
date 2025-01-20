package controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
	
	@FXML
	private Button button_login;
	
	@FXML
	private Button button_signup;
	
	@FXML
	private TextField tf_username;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		button_login.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBConfig.logInUser(event, tf_username.getText());
			}
		});
		
		button_signup.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBConfig.changeScene(event, "/controller/SignUpView.fxml", "RentEase: Simplifying Rent and Utility Management", null);
			}
		});
	}
}