package controller;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class HelpsFAQsController {

    @FXML
    private Text balance_due_btn;

    @FXML
    private Text create_invoice_btn;

    @FXML
    private Text dashboard_btn;

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
    private Button help_faqs_btn;

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
        loadScene(event, "/controller/MyProfileView.fxml", "RentEase: My Profile");
    }

    @FXML
    void payment_history_btn_clicked(MouseEvent event) {
        loadScene(event, "/controller/PaymentHistoryView.fxml", "RentEase: Payment History");
    }
    
    @FXML
    void help_faqs_btn_clicked(ActionEvent event) {
        // Open Gmail with a pre-filled email to support@rentease.com
        openGmail("support@rentease.com");
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

    private void openGmail(String recipient) {
        try {
            String mailto = "https://mail.google.com/mail/?view=cm&fs=1&to=" + recipient;
            URI mailUri = new URI(mailto);
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(mailUri);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Opening the default browser is not supported on this environment.");
            }
        } catch (IOException | URISyntaxException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to open Gmail: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}