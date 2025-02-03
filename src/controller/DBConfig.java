package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class DBConfig {
	
	private static final String URL = "jdbc:mysql://localhost:3306/rentease_application";
    private static final String USER = "root";
    private static String PASSWORD = "";
    
    static {
        int user = 1; // Set the user value here

        if (user == 1) {
            PASSWORD = "lesterosana";
        } else if (user == 2) {
            PASSWORD = "1123";
        }
    }
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
	
	public static void changeScene(ActionEvent event, String fxmlFile, String title, String username) {
		Parent root = null;
		
		if (username != null) {
			try {
				FXMLLoader loader = new FXMLLoader(DBConfig.class.getResource(fxmlFile));
				root = loader.load();
				// Dashboard dashboard = loader.getController();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				root = FXMLLoader.load(DBConfig.class.getResource(fxmlFile));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		stage.setTitle(title);
		stage.setScene(new Scene(root, 1200, 780));
		stage.show();
	}
	
	public static void signUpUser(ActionEvent event, String username) {
		Connection connection = null;
		PreparedStatement psInsert = null;
		PreparedStatement psCheckUserExists = null;
		ResultSet resultSet = null;
		
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			psCheckUserExists = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
			psCheckUserExists.setString(1, username);
			resultSet = psCheckUserExists.executeQuery();
			
			if (resultSet.isBeforeFirst()) {
				System.out.println("User already exists!");
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setContentText("You cannot use this username.");
				alert.show();
			} else {
				psInsert = connection.prepareStatement("INSERT INTO users (username) VALUES (?)");
				psInsert.setString(1, username);
				psInsert.executeUpdate();
				
				changeScene(event, "/controller/LoginView.fxml", "Dashboard", null);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (psCheckUserExists != null) {
				try {
					psCheckUserExists.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (psInsert != null) {
				try {
					psInsert.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void logInUser(ActionEvent event, String username) {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			connection = DriverManager.getConnection(URL, USER, PASSWORD);
			preparedStatement = connection.prepareStatement("SELECT username FROM users WHERE username = ?");
			preparedStatement.setString(1, username);
			resultSet = preparedStatement.executeQuery();
			
			if (!resultSet.isBeforeFirst()) {
				System.out.println("User not found.");
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setContentText("Provided credentials are incorrect!");
				alert.show();
			} else {
				while (resultSet.next()) {
					String retrievedUsername = resultSet.getString("username");
					
					if (retrievedUsername.equals(username)) {
						changeScene(event, "/controller/DashboardView.fxml", "Dashboard", null);
					} 
					}
				} 
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (preparedStatement != null) {
				try {
					preparedStatement.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	} 
}
