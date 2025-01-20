package controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class SignUpController implements Initializable {

	@FXML 
	private Button button_signup;
	
	@FXML 
	private Button button_login;
	
	@FXML
	private TextField tf_username;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		button_signup.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (!tf_username.getText().trim().isEmpty()) {
					DBConfig.signUpUser(event, tf_username.getText());
				} else {
					System.out.println("Please fill in the required information.");
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setContentText("Please enter a username to sign up!");
					alert.show();
				}
			}
		});
		
		button_login.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DBConfig.changeScene(event, "LoginView.fxml", "RentEase: Simplifying Rent and Utility Management", null);
			}
		});
	}
}
