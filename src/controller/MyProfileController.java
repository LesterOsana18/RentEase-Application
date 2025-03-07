package controller;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MyProfileController implements Initializable {

    @FXML
    private Text balance_due_btn;

    @FXML
    private Text create_invoice_btn;

    @FXML
    private Text dashboard_btn;
    
    @FXML
    private Button delete_btn;

    @FXML
    private Text edit_invoice_btn;

    @FXML
    private Text help_btn;

    @FXML
    private Button logout_btn;

    @FXML
    private Text my_profile_btn;

    @FXML
    private Text payment_history_btn;

    @FXML
    private TextField current_username_text_field;

    @FXML
    private TextField new_username_text_field;

    @FXML
    private Button save_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            // Get the current stage
            Stage stage = (Stage) current_username_text_field.getScene().getWindow();

            // Fetch the username
            String currentUsername = DBConfig.getUsername(stage);

            // Set the current username in the text field
            if (currentUsername != null) {
                current_username_text_field.setText("CURRENT USERNAME: " + currentUsername);
            } else {
                current_username_text_field.setText("CURRENT USERNAME: Not found");
            }
        });
    }
    
    @FXML
    void balance_due_btn_clicked(MouseEvent event) {
        loadScene(event, "/controller/BalanceDueView.fxml", "RentEase: Balance Due");
    }

    @FXML
    void create_invoice_btn(MouseEvent event) {
        loadScene(event, "/controller/CreateInvoiceView.fxml", "RentEase: Create Invoice");
    }

    @FXML
    void dashboard_btn_clicked(MouseEvent event) {
        loadScene(event, "/controller/DashboardView.fxml", "RentEase: Dashboard");
    }

    @FXML
    void delete_btn(ActionEvent event) {
        // Get the current stage
        Stage stage = (Stage) current_username_text_field.getScene().getWindow();

        // Confirm deletion
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Deletion");
        alert.setContentText("Are you sure you want to delete your account and all related data?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Delete the user and related data
                DBConfig.deleteUser(stage);

                // Show success message
                showAlert(Alert.AlertType.INFORMATION, "Success", "User and related data deleted successfully.");

                // Redirect to login or another appropriate screen
                loadScene(event, "/controller/LoginView.fxml");
            }
        });
    }

    @FXML
    void edit_invoice_btn_clicked(MouseEvent event) {
        loadScene(event, "/controller/EditInvoiceView.fxml", "RentEase: Edit Invoice");
    }

    @FXML
    void help_btn_clicked(MouseEvent event) {
        loadScene(event, "/controller/HelpsFAQsView.fxml", "RentEase: Help & FAQs");
    }

    @FXML
    void logout_btn_clicked(MouseEvent event) {
        // Create a confirmation alert
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Logout");
        alert.setHeaderText("Logout Confirmation");
        alert.setContentText("Are you sure you want to logout?");

        // Add Yes and No buttons to the alert
        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);
        alert.getButtonTypes().setAll(yesButton, noButton);

        // Show the alert and wait for the user's response
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get().getButtonData() == ButtonBar.ButtonData.YES) {
            // If the user clicked Yes, load the Login view
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/controller/LoginView.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("RentEase: Login");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("Error", "Unable to load view: " + e.getMessage());
            }
        }
    }

    // Utility method to show alert messages
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void my_profile_btn_clicked(MouseEvent event) {
        // Implement my profile logic here
    }

    @FXML
    void payment_history_btn_clicked(MouseEvent event) {
        loadScene(event, "/controller/PaymentHistoryView.fxml", "RentEase: Payment History");
    }
    
    @FXML
    void save_btn_clicked(ActionEvent event) {
        // Get the new username from the text field
        String newUsername = new_username_text_field.getText();

        if (newUsername == null || newUsername.trim().isEmpty()) {
            // Show error message if the new username is empty
            showAlert(Alert.AlertType.ERROR, "Error", "New username cannot be empty.");
        } else {
            // Get the current stage
            Stage stage = (Stage) current_username_text_field.getScene().getWindow();

            // Update the username in the database
            DBConfig.updateUsername(stage, newUsername);

            // Show success message
            showAlert(Alert.AlertType.INFORMATION, "Success", "Username updated successfully.");

            // Refresh the current username text field
            String currentUsername = DBConfig.getUsername(stage);
            current_username_text_field.setText("CURRENT USERNAME: " + currentUsername);
        }
    }

    private void loadScene(MouseEvent event, String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadScene(ActionEvent event, String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}